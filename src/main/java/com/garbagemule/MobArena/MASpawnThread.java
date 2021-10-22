package com.garbagemule.MobArena;

import com.garbagemule.MobArena.events.ArenaCompleteEvent;
import com.garbagemule.MobArena.events.NewWaveEvent;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.healthbar.CreatesHealthBar;
import com.garbagemule.MobArena.healthbar.HealthBar;
import com.garbagemule.MobArena.region.ArenaRegion;
import com.garbagemule.MobArena.things.ExperienceThing;
import com.garbagemule.MobArena.things.Thing;
import com.garbagemule.MobArena.things.ThingPicker;
import com.garbagemule.MobArena.waves.MABoss;
import com.garbagemule.MobArena.waves.MACreature;
import com.garbagemule.MobArena.waves.Wave;
import com.garbagemule.MobArena.waves.WaveManager;
import com.garbagemule.MobArena.waves.enums.WaveType;
import com.garbagemule.MobArena.waves.types.BossWave;
import com.garbagemule.MobArena.waves.types.SupplyWave;
import com.garbagemule.MobArena.waves.types.UpgradeWave;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MASpawnThread implements Runnable
{
    private MobArena plugin;
    private Arena arena;
    private ArenaRegion region;
    private RewardManager rewardManager;
    private WaveManager waveManager;
    private MonsterManager monsterManager;
    private CreatesHealthBar createsHealthBar;

    private int playerCount, monsterLimit, waveLeeway;
    private boolean waveClear, bossClear, preBossClear, wavesAsLevel, endAfterBossKill;
    private int waveInterval;
    private int nextWaveDelay;

    private BukkitTask task;

    /**
     * Create a new monster spawner for the input arena.
     * Note that the arena's WaveManager is reset
     * @param plugin a MobArena instance
     * @param arena an arena
     */
    public MASpawnThread(MobArena plugin, Arena arena) {
        this.plugin = plugin;
        this.arena = arena;
        this.region = arena.getRegion();
        this.rewardManager = arena.getRewardManager();
        this.waveManager = arena.getWaveManager();
        this.monsterManager = arena.getMonsterManager();
        this.createsHealthBar = new CreatesHealthBar(arena.getSettings().getString("boss-health-bar", "none"));

        reset();
    }

    /**
     * Reset the spawner, so all systems and settings are
     * ready for a new session.
     */
    public void reset() {
        waveManager.reset();
        playerCount = arena.getPlayersInArena().size();
        monsterLimit = arena.getSettings().getInt("monster-limit", 100);
        waveLeeway = arena.getSettings().getInt("clear-wave-leeway", 0);
        waveClear = arena.getSettings().getBoolean("clear-wave-before-next", false);
        bossClear = arena.getSettings().getBoolean("clear-boss-before-next", false);
        preBossClear = arena.getSettings().getBoolean("clear-wave-before-boss", false);
        endAfterBossKill = arena.getSettings().getBoolean("end-after-final-boss-kill", false);
        wavesAsLevel = arena.getSettings().getBoolean("display-waves-as-level", false);
        waveInterval = arena.getSettings().getInt("wave-interval", 3);
        nextWaveDelay = arena.getSettings().getInt("next-wave-delay", 0);
    }

    public void start() {
        if (task != null) {
            plugin.getLogger().warning("Starting spawner in arena " + arena.configName() + " with existing spawner still running. This should never happen.");
            task.cancel();
            task = null;
        }

        int delay = arena.getSettings().getInt("first-wave-delay", 5) * 20;
        task = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            arena.getEventListener().pvpActivate();
            this.run();
        }, delay);
    }

    public void stop() {
        if (task == null) {
            plugin.getLogger().warning("Can't stop non-existent spawner in arena " + arena.configName() + ". This should never happen.");
            return;
        }

        arena.getEventListener().pvpDeactivate();

        task.cancel();
        task = null;
    }

    public void run() {
        // If the arena isn't running or if there are no players in it.
        if (!arena.isRunning() || arena.getPlayersInArena().isEmpty()) {
            return;
        }

        // Clear out all dead monsters in the monster set.
        removeDeadMonsters();
        removeCheatingPlayers();

        // In case some players were removed, check again.
        if (!arena.isRunning()) {
            return;
        }

        // Grab the wave number.
        int nextWave = waveManager.getWaveNumber() + 1;

        // Check if wave needs to be cleared first. If so, return!
        if (!isWaveClear()) {
            arena.scheduleTask(this, 60);
            return;
        }

        // Fire off the event. If cancelled, try again in 3 seconds.
        NewWaveEvent event = new NewWaveEvent(arena, waveManager.getNext(), nextWave);
        plugin.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            arena.scheduleTask(this, 60);
            return;
        }

        // Delay the next wave
        if (nextWaveDelay > 0) {
            task = Bukkit.getScheduler().runTaskLater(plugin, this::spawnNextWave, nextWaveDelay * 20);
        } else {
            spawnNextWave();
        }
    }

    private void spawnNextWave() {
        // Grab the wave number.
        int nextWave = waveManager.getWaveNumber() + 1;

        // Grant rewards (if any) for the wave that just ended
        grantRewards(nextWave - 1);

        // Check if this is the final wave, in which case, end instead of spawn
        if (nextWave > 1 && (nextWave - 1) == waveManager.getFinalWave()) {
            // Fire the complete event
            ArenaCompleteEvent complete = new ArenaCompleteEvent(arena);
            plugin.getServer().getPluginManager().callEvent(complete);

            // Then force leave everyone
            List<Player> players = new ArrayList<>(arena.getPlayersInArena());
            for (Player p : players) {
                if (arena.getSettings().getBoolean("keep-exp", false)) {
                    arena.getRewardManager().addReward(p, new ExperienceThing(p.getTotalExperience()));
                }
                arena.playerLeave(p);
            }
            return;
        }

        // Spawn the next wave.
        spawnWave(nextWave);

        // Update stats
        updateStats(nextWave);

        // Reschedule the spawner for the next wave.
        task = Bukkit.getScheduler().runTaskLater(plugin, this, waveInterval * 20);
    }

    private void spawnWave(int wave) {
        Wave w = waveManager.next();

        w.announce(arena, wave);

        arena.getScoreboard().updateWave(wave);

        // Set the players' level to the wave number
        if (wavesAsLevel) {
            for (Player p : arena.getPlayersInArena()) {
                p.setLevel(wave);
                p.setExp(0.0f);
            }
        }

        if (w.getType() == WaveType.UPGRADE) {
            handleUpgradeWave(w);
            return;
        }

        Map<MACreature, Integer> monsters = w.getMonstersToSpawn(wave, playerCount, arena);
        List<Location> spawnpoints = w.getSpawnpoints(arena);

        World world = arena.getWorld();
        int totalSpawnpoints = spawnpoints.size();
        int index = 0;
        double mul = w.getHealthMultiplier();

        for (Map.Entry<MACreature, Integer> entry : monsters.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++, index++) {
                // Check if monster limit has been reached.
                if (monsterManager.getMonsters().size() >= monsterLimit) {
                    return;
                }

                // Grab a spawnpoint
                Location spawnpoint = spawnpoints.get(index % totalSpawnpoints);

                // Spawn the monster
                LivingEntity e = entry.getKey().spawn(arena, world, spawnpoint);

                // Add potion effects
                e.addPotionEffects(w.getEffects());

                // Add it to the arena.
                monsterManager.addMonster(e);

                // Set the health.
                int health = (int) Math.max(1D, e.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() * mul);
                try {
                    e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
                    e.setHealth(health);
                } catch (IllegalArgumentException ex) {
                    // Spigot... *facepalm*
                    plugin.getLogger().severe("Can't set health to " + health + ", using default health. If you are running Spigot, set 'maxHealth' higher in your Spigot settings.");
                    plugin.getLogger().severe(ex.getLocalizedMessage());
                    if (w.getType() == WaveType.BOSS) {
                        ((BossWave) w).setBossName("SPIGOT ERROR");
                    } else {
                        e.setCustomName("SPIGOT ERROR");
                    }
                }

                // Switch on the type.
                switch (w.getType()){
                    case BOSS:
                        BossWave bw = (BossWave) w;
                        double maxHealth = bw.getHealth().evaluate(arena);
                        MABoss boss = monsterManager.addBoss(e, maxHealth);
                        HealthBar healthbar = createsHealthBar.create(e, bw.getBossName());
                        arena.getPlayersInArena().forEach(healthbar::addPlayer);
                        healthbar.setProgress(1);
                        boss.setHealthBar(healthbar);
                        boss.setReward(bw.getReward());
                        boss.setDrops(bw.getDrops());
                        bw.addMABoss(boss);
                        bw.activateAbilities(arena);
                        if (bw.getBossName() != null) {
                            e.setCustomName(bw.getBossName());
                            e.setCustomNameVisible(true);
                        }
                        break;
                    case SWARM:
                        health = (int) (mul < 1D ? e.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() * mul : 1);
                        health = Math.max(1, health);
                        e.setHealth(Math.min(health, e.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()));
                        break;
                    case SUPPLY:
                        SupplyWave sw = (SupplyWave) w;
                        monsterManager.addSupplier(e, sw.getDropList());
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private void handleUpgradeWave(Wave w) {
        UpgradeWave uw = (UpgradeWave) w;

        for (Player p : arena.getPlayersInArena()) {
            String slug = arena.getArenaPlayer(p).getArenaClass().getSlug();
            uw.grantItems(p, slug);
            uw.grantItems(p, "all");
        }

        plugin.getArenaMaster().getSpawnsPets().spawn(arena);
    }

    /**
     * Check if the wave is clear for new spawns.
     * If clear-boss-before-next: true, bosses must be dead.
     * If clear-wave-before-next: true, all monsters must be dead.
     * @return true, if the wave is "clear" for new spawns.
     */
    private boolean isWaveClear() {
        // Check for monster limit
        if (monsterManager.getMonsters().size() >= monsterLimit) {
            return false;
        }

        // Check for boss clear
        if (bossClear && !monsterManager.getBossMonsters().isEmpty()) {
            return false;
        }

        // Check for wave clear
        if (waveClear && monsterManager.getMonsters().size() > waveLeeway) {
            return false;
        }

        // Check for pre boss clear
        if (preBossClear && waveManager.getNext().getType() == WaveType.BOSS && monsterManager.getMonsters().size() > waveLeeway) {
            return false;
        }

        // Check for final wave
        if (!endAfterBossKill && !monsterManager.getMonsters().isEmpty() && waveManager.getWaveNumber() == waveManager.getFinalWave()) {
            return false;
        }

        // Check for boss clear final wave
        if (endAfterBossKill && !monsterManager.getBossMonsters().isEmpty() && waveManager.getWaveNumber() == waveManager.getFinalWave()) {
            return false;
        }

        return true;
    }

    private void removeDeadMonsters() {
        List<Entity> tmp = new ArrayList<>(monsterManager.getMonsters());
        for (Entity e : tmp) {
            if (e == null) {
                continue;
            }

            if (e.isDead() || !region.contains(e.getLocation())) {
                monsterManager.remove(e);
                e.remove();
            }
        }
    }

    private void removeCheatingPlayers() {
        List<Player> players = new ArrayList<>(arena.getPlayersInArena());
        for (Player p : players) {
            if (region.contains(p.getLocation())) {
                continue;
            }

            arena.getMessenger().tell(p, "Leaving so soon?");
            p.getInventory().clear();
            arena.playerLeave(p);
        }
    }

    private void grantRewards(int wave) {
        for (Map.Entry<Integer, ThingPicker> entry : arena.getEveryWaveEntrySet()) {
            if (wave > 0 && wave % entry.getKey() == 0) {
                addReward(entry.getValue());
            }
        }

        ThingPicker after = arena.getAfterWaveReward(wave);
        if (after != null) {
            addReward(after);
        }
    }

    private void updateStats(int wave) {
        for (ArenaPlayer ap : arena.getArenaPlayerSet()) {
            if (arena.getPlayersInArena().contains(ap.getPlayer())) {
                ap.getStats().inc("lastWave");
            }
        }
    }

    /*
     * ////////////////////////////////////////////////////////////////////
     * //
     * // Getters/setters
     * //
     * ////////////////////////////////////////////////////////////////////
     */

    public int getPlayerCount() {
        return playerCount;
    }

    /**
     * Rewards all players with an item from the input String.
     */
    private void addReward(ThingPicker picker) {
        for (Player p : arena.getPlayersInArena()) {
            Thing reward = picker.pick();
            if (reward != null) {
                rewardManager.addReward(p, reward);
                arena.getMessenger().tell(p, Msg.WAVE_REWARD, reward.toString());
            }
        }
    }
}
