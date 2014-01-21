package com.garbagemule.MobArena;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.garbagemule.MobArena.events.ArenaCompleteEvent;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import com.garbagemule.MobArena.events.NewWaveEvent;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.region.ArenaRegion;
import com.garbagemule.MobArena.waves.*;
import com.garbagemule.MobArena.waves.enums.WaveType;
import com.garbagemule.MobArena.waves.types.BossWave;
import com.garbagemule.MobArena.waves.types.SupplyWave;
import com.garbagemule.MobArena.waves.types.UpgradeWave;

public class MASpawnThread implements Runnable
{
    private MobArena plugin;
    private Arena arena;
    private ArenaRegion region;
    private RewardManager rewardManager;
    private WaveManager waveManager;
    private MonsterManager monsterManager;

    private int playerCount, monsterLimit;
    private boolean waveClear, bossClear, preBossClear, wavesAsLevel;

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
        waveClear = arena.getSettings().getBoolean("clear-wave-before-next", false);
        bossClear = arena.getSettings().getBoolean("clear-boss-before-next", false);
        preBossClear = arena.getSettings().getBoolean("clear-wave-before-boss", false);
        wavesAsLevel = arena.getSettings().getBoolean("display-waves-as-level", false);
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

        // Grant rewards (if any) for the wave about to spawn
        grantRewards(nextWave);

        // Check if this is the final wave, in which case, end instead of spawn
        if (nextWave > 1 && (nextWave - 1) == waveManager.getFinalWave()) {
            // Fire the complete event
            ArenaCompleteEvent complete = new ArenaCompleteEvent(arena);
            plugin.getServer().getPluginManager().callEvent(complete);

            // Then force leave everyone
            List<Player> players = new ArrayList<Player>(arena.getPlayersInArena());
            for (Player p : players) {
                arena.playerLeave(p);
            }
            return;
        }

        // Spawn the next wave.
        spawnWave(nextWave);

        // Update stats
        updateStats(nextWave);

        // Reschedule the spawner for the next wave.
        arena.scheduleTask(this, arena.getSettings().getInt("wave-interval", 3) * 20);
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

                // Add it to the arena.
                monsterManager.addMonster(e);

                // Set the health.
                e.resetMaxHealth(); // Avoid conflicts/enormous multiplications from other plugins handling Mob health
                int health = (int) Math.max(1D, e.getMaxHealth() * mul);
                e.setMaxHealth(health);
                e.setHealth(health);

                // Switch on the type.
                switch (w.getType()){
                    case BOSS:
                        BossWave bw = (BossWave) w;
                        int maxHealth = bw.getMaxHealth(playerCount);
                        MABoss boss = monsterManager.addBoss(e, maxHealth);
                        boss.setReward(bw.getReward());
                        bw.addMABoss(boss);
                        bw.activateAbilities(arena);
                        if (bw.getBossName() != null) {
                            e.setCustomName(bw.getBossName());
                            e.setCustomNameVisible(true);
                        }
                        break;
                    case SWARM:
                        health = (int) (mul < 1D ? e.getMaxHealth() * mul : 1);
                        health = Math.max(1, health);
                        e.setHealth(Math.min(health, e.getMaxHealth()));
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
            String className = arena.getArenaPlayer(p).getArenaClass().getLowercaseName();
            uw.grantItems(arena, p, className);
            uw.grantItems(arena, p, "all");
        }
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

        // Check for wave and pre boss clear
        if (waveClear && !monsterManager.getMonsters().isEmpty()) {
            return false;
        }
        
        // Check for pre boss clear
        if (preBossClear && waveManager.getNext().getType() == WaveType.BOSS && !monsterManager.getMonsters().isEmpty()) {
            return false;
        }

        // Check for final wave
        if (!monsterManager.getMonsters().isEmpty() && waveManager.getWaveNumber() == waveManager.getFinalWave()) {
            return false;
        }

        return true;
    }

    private void removeDeadMonsters() {
        List<Entity> tmp = new ArrayList<Entity>(monsterManager.getMonsters());
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
        List<Player> players = new ArrayList<Player>(arena.getPlayersInArena());
        for (Player p : players) {
            if (region.contains(p.getLocation())) {
                continue;
            }
            
            Messenger.tell(p, "Leaving so soon?");
            p.getInventory().clear();
            arena.playerLeave(p);
        }
    }

    private void grantRewards(int wave) {
        for (Map.Entry<Integer, List<ItemStack>> entry : arena.getEveryWaveEntrySet()) {
            if (wave % entry.getKey() == 0) {
                addReward(entry.getValue());
            }
        }

        List<ItemStack> after = arena.getAfterWaveReward(wave);
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
    private void addReward(List<ItemStack> rewards) {
        for (Player p : arena.getPlayersInArena()) {
            ItemStack reward = MAUtils.getRandomReward(rewards);
            rewardManager.addReward(p, reward);

            if (reward == null) {
                Messenger.tell(p, "ERROR! Problem with rewards. Notify server host!");
                Messenger.warning("Could not add null reward. Please check the config-file!");
            }
            else if (reward.getTypeId() == MobArena.ECONOMY_MONEY_ID) {
                if (plugin.giveMoney(p, reward)) { // Money already awarded here, not needed at end of match as well
                    Messenger.tell(p, Msg.WAVE_REWARD, plugin.economyFormat(reward));
                }
                else {
                    Messenger.warning("Tried to add money, but no economy plugin detected!");
                }
            }
            else {
                Messenger.tell(p, Msg.WAVE_REWARD, MAUtils.toCamelCase(reward.getType().toString()) + ":" + reward.getAmount());
            }
        }
    }
}