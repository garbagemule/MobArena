package com.garbagemule.MobArena;

import java.io.File;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.PriorityBlockingQueue;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Vehicle;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.potion.PotionEffect;

import com.garbagemule.MobArena.autostart.AutoStartTimer;
import com.garbagemule.MobArena.events.*;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.leaderboards.Leaderboard;
import com.garbagemule.MobArena.log.ArenaLog;
import com.garbagemule.MobArena.log.LogSessionBuilder;
import com.garbagemule.MobArena.log.LogTotalsBuilder;
import com.garbagemule.MobArena.log.YMLSessionBuilder;
import com.garbagemule.MobArena.log.YMLTotalsBuilder;
import com.garbagemule.MobArena.region.ArenaRegion;
import com.garbagemule.MobArena.repairable.*;
import com.garbagemule.MobArena.spout.Spouty;
import com.garbagemule.MobArena.time.Time;
import com.garbagemule.MobArena.time.TimeStrategy;
import com.garbagemule.MobArena.time.TimeStrategyLocked;
import com.garbagemule.MobArena.time.TimeStrategyNull;
import com.garbagemule.MobArena.util.*;
import com.garbagemule.MobArena.util.config.Config;
import com.garbagemule.MobArena.util.config.ConfigSection;
import com.garbagemule.MobArena.util.inventory.InventoryManager;
import com.garbagemule.MobArena.util.inventory.InventoryUtils;
import com.garbagemule.MobArena.waves.*;

public class ArenaImpl implements Arena
{
    // General stuff
    private MobArena plugin;
    private String name;
    private World world;
    private File dir;
    
    // Settings section of the config-file for this arena.
    private ConfigSection settings;
    
    // Run-time settings and critical config settings
    private boolean enabled, protect, running, edit;
    
    // World stuff
    private boolean allowMonsters, allowAnimals;
    //private Difficulty spawnMonsters;
    
    // Warps, points and locations
    private ArenaRegion region;
    private Leaderboard leaderboard;
    
    // Player stuff
    private InventoryManager  inventoryManager;
    private RewardManager     rewardManager;
    private ClassLimitManager limitManager;
    private Map<Player,ArenaPlayer> arenaPlayerMap;
    private Map<Player,PlayerData> playerData = new HashMap<Player,PlayerData>();
    
    private Set<Player> arenaPlayers, lobbyPlayers, readyPlayers, specPlayers, deadPlayers;
    private Set<Player> randoms;
    
    // Classes stuff
    private Map<String,ArenaClass> classes;
    private Map<Player,PermissionAttachment> attachments;
    
    // Blocks and pets
    private PriorityBlockingQueue<Repairable> repairQueue;
    private Set<Block>             blocks;
    private LinkedList<Repairable> repairables, containables;
    
    // Monster stuff
    private MonsterManager monsterManager;
    
    // Wave stuff
    private WaveManager   waveManager;
    private Wave          currentWave;
    private MASpawnThread spawnThread;
    private SheepBouncer  sheepBouncer;
    private Map<Integer,List<ItemStack>> everyWaveMap, afterWaveMap;
    
    // Logging
    private boolean logging;
    private ArenaLog log;
    private LogSessionBuilder sessionBuilder;
    private LogTotalsBuilder  totalsBuilder;
    
    // Misc
    private ArenaListener eventListener;
    private List<ItemStack> entryFee;
    private TimeStrategy timeStrategy;
    private AutoStartTimer autoStartTimer;
    
    /**
     * Primary constructor. Requires a name and a world.
     */
    public ArenaImpl(MobArena plugin, Config config, String name, World world) {
        if (world == null)
            throw new NullPointerException("[MobArena] ERROR! World for arena '" + name + "' does not exist!");
        
        this.name     = name;
        this.world    = world;
        this.plugin   = plugin;
        this.settings = new ConfigSection(config, "arenas." + name + ".settings");
        this.region   = new ArenaRegion(new ConfigSection(config, "arenas." + name + ".coords"), this);
        
        this.enabled = settings.getBoolean("enabled", false);
        this.protect = settings.getBoolean("protect", true);
        this.logging = settings.getBoolean("logging", true);
        this.running = false;
        this.edit    = false;
        
        this.inventoryManager = new InventoryManager(this);
        this.rewardManager    = new RewardManager(this);

        // Warps, points and locations
        this.leaderboard = new Leaderboard(plugin, this, region.getLeaderboard());

        // Player stuff
        this.arenaPlayerMap = new HashMap<Player,ArenaPlayer>();
        this.arenaPlayers   = new HashSet<Player>();
        this.lobbyPlayers   = new HashSet<Player>();
        this.readyPlayers   = new HashSet<Player>();
        this.specPlayers    = new HashSet<Player>();
        this.deadPlayers    = new HashSet<Player>();
        this.randoms        = new HashSet<Player>();

        // Classes, items and permissions
        this.classes      = plugin.getArenaMaster().getClasses();
        this.attachments  = new HashMap<Player,PermissionAttachment>();
        this.limitManager = new ClassLimitManager(this, classes, new ConfigSection(config, "arenas." + name + ".class-limits"));
        
        // Blocks and pets
        this.repairQueue  = new PriorityBlockingQueue<Repairable>(100, new RepairableComparator());
        this.blocks       = new HashSet<Block>();
        this.repairables  = new LinkedList<Repairable>();
        this.containables = new LinkedList<Repairable>();
        
        // Monster stuff
        this.monsterManager = new MonsterManager();
        
        // Wave stuff
        this.waveManager  = new WaveManager(this, config);
        this.everyWaveMap = MAUtils.getArenaRewardMap(plugin, config, name, "every");
        this.afterWaveMap = MAUtils.getArenaRewardMap(plugin, config, name, "after");
        
        // Misc
        this.eventListener = new ArenaListener(this, plugin);
        this.entryFee      = ItemParser.parseItems(settings.getString("entry-fee", ""));
        this.allowMonsters = world.getAllowMonsters();
        this.allowAnimals  = world.getAllowAnimals();
        
        int autoStart       = settings.getInt("auto-start-timer", 0);
        this.autoStartTimer = new AutoStartTimer(this, autoStart);
        
        String timeString = settings.getString("player-time-in-arena", "world");
        Time time = Enums.getEnumFromString(Time.class, timeString);
        this.timeStrategy = (time != null ? new TimeStrategyLocked(time) : new TimeStrategyNull());
        
        if (logging) {
            this.dir = new File(plugin.getDataFolder() + File.separator + "arenas" + File.separator + name);
            this.sessionBuilder = new YMLSessionBuilder(new File(dir, "log_session.yml"));
            this.totalsBuilder  = new YMLTotalsBuilder(new File(dir, "log_totals.yml"));
            
            this.log = new ArenaLog(this, sessionBuilder, totalsBuilder);
        }
    }
    
    
    
    /*/////////////////////////////////////////////////////////////////////////
    //
    //      NEW METHODS IN REFACTORING
    //
    /////////////////////////////////////////////////////////////////////////*/
    
    @Override
    public ConfigSection getSettings() {
        return settings;
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public void setWorld(World world) {
        this.world = world;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean value) {
        enabled = value;
        settings.set("enabled", enabled);
    }

    @Override
    public boolean isProtected() {
        return protect;
    }

    @Override
    public void setProtected(boolean value) {
        protect = value;
        settings.set("protect", protect);
    }
    
    @Override
    public boolean isLogging() {
        return logging;
    }
    
    @Override
    public void setLogging(boolean value) {
        logging = value;
        settings.set("logging", logging);
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public boolean inEditMode() {
        return edit;
    }

    @Override
    public void setEditMode(boolean value) {
        edit = value;
    }
    
    private int getMinPlayers() {
        return settings.getInt("min-players");
    }
    
    private int getMaxPlayers() {
        return settings.getInt("max-players");
    }
    
    private int getJoinDistance() {
        return settings.getInt("max-join-distance");
    }

    @Override
    public Material getClassLogo(String classname) {
        ArenaClass arenaClass = classes.get(classname);
        if (arenaClass == null) return Material.STONE;
        
        return arenaClass.getLogo();
    }

    @Override
    public List<ItemStack> getEntryFee() {
        return entryFee;
    }

    @Override
    public Set<Map.Entry<Integer,List<ItemStack>>> getEveryWaveEntrySet() {
        return everyWaveMap.entrySet();
    }

    @Override
    public List<ItemStack> getAfterWaveReward(int wave) {
        return afterWaveMap.get(wave);
    }

    @Override
    public Set<Player> getPlayersInArena() {
        return Collections.unmodifiableSet(arenaPlayers);
    }

    @Override
    public Set<Player> getPlayersInLobby() {
        return Collections.unmodifiableSet(lobbyPlayers);
    }

    @Override
    public Set<Player> getReadyPlayersInLobby() {
        return Collections.unmodifiableSet(readyPlayers);
    }

    @Override
    public Set<Player> getSpectators() {
        return Collections.unmodifiableSet(specPlayers);
    }

    @Override
    public MASpawnThread getSpawnThread() {
        return spawnThread;
    }

    @Override
    public WaveManager getWaveManager() {
        return waveManager;
    }

    @Override
    public Location getPlayerEntry(Player p) {
        PlayerData mp = playerData.get(p);
        return (mp != null ? mp.entry() : null);
    }

    @Override
    public ArenaListener getEventListener() {
        return eventListener;
    }

    @Override
    public void setLeaderboard(Leaderboard leaderboard) {
        this.leaderboard = leaderboard;
    }

    @Override
    public ArenaPlayer getArenaPlayer(Player p) {
        return arenaPlayerMap.get(p);
    }

    @Override
    public Set<Block> getBlocks() {
        return blocks;
    }

    @Override
    public void addBlock(Block b) {
        blocks.add(b);
    }

    @Override
    public boolean removeBlock(Block b) {
        return blocks.remove(b);
    }

    @Override
    public boolean hasPet(Entity e) {
        return monsterManager.hasPet(e);
    }

    @Override
    public void addRepairable(Repairable r) {
        repairables.add(r);
    }

    @Override
    public ArenaRegion getRegion() {
        return region;
    }

    @Override
    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    @Override
    public RewardManager getRewardManager() {
        return rewardManager;
    }

    @Override
    public MonsterManager getMonsterManager() {
        return monsterManager;
    }
    
    @Override
    public ClassLimitManager getClassLimitManager() {
        return limitManager;
    }
    
    @Override
    public ArenaLog getLog() {
        return log;
    }
    
    
    
    
    
    
    
    
    
    

    @Override
    public boolean startArena() {
        // Sanity-checks
        if (running || lobbyPlayers.isEmpty() || !readyPlayers.containsAll(lobbyPlayers)) {
            return false;
        }

        // Fire the event and check if it's been cancelled.
        ArenaStartEvent event = new ArenaStartEvent(this);
        plugin.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return false;
        }

        // Store all chest contents.
        storeContainerContents();
        
        // Populate arenaPlayers and clear the lobby.
        arenaPlayers.addAll(lobbyPlayers);
        lobbyPlayers.clear();
        readyPlayers.clear();
        
        // Assign random classes.
        for (Player p : randoms) {
            assignRandomClass(p);
        }
        randoms.clear();
        
        // Then check if there are still players left.
        if (arenaPlayers.isEmpty()) {
            return false;
        }
        
        // Teleport players, give full health, initialize map
        for (Player p : arenaPlayers) {
            // TODO figure out how people die in lobby and get sent to spectator area early
            // Remove player from spec list to avoid invincibility issues
            if (inSpec(p)) {
                specPlayers.remove(p);
                System.out.println("[MobArena] Player " + p.getName() + " joined the arena from the spec area!");
                System.out.println("[MobArena] Invincibility glitch attempt stopped!");
            }
            
            p.teleport(region.getArenaWarp());
            //movePlayerToLocation(p, region.getArenaWarp());
            setHealth(p, 20);
            p.setFoodLevel(20);
            assignClassPermissions(p);
            arenaPlayerMap.get(p).resetStats();
        }
        
        // Start spawning monsters (must happen before 'running = true;')
        startSpawner();
        startBouncingSheep();
        
        // Set the boolean.
        running = true;
        
        // Spawn pets (must happen after 'running = true;')
        spawnPets();
        
        // Clear the classes in use map, as they're no longer needed
        limitManager.clearClassesInUse();
        
        // Start logging
        rewardManager.reset();
        if (logging)
            log.start();
        
        // Initialize leaderboards and start displaying info.
        leaderboard.initialize();
        leaderboard.startTracking();
        
        Messenger.tellAll(this, Msg.ARENA_START);
        
        return true;
    }

    @Override
    public boolean endArena() {
        // Sanity-checks.
        if (!running || !arenaPlayers.isEmpty()) {
            return false;
        }

        // Fire the event and check if it's been cancelled.
        ArenaEndEvent event = new ArenaEndEvent(this);
        plugin.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return false;
        }
        
        // Set the running boolean and disable arena if not disabled.
        boolean en = enabled;
        enabled = false;
        running = false;
        
        // Stop tracking leaderboards
        leaderboard.stopTracking();
        leaderboard.update();
        
        // Finish logging
        if (logging)
            log.end();
        
        // Stop spawning.
        stopSpawner();

        // Announce and clean arena floor, etc.
        Messenger.tellAll(this, Msg.ARENA_END, true);
        cleanup();
        
        // Restore region.
        if (settings.getBoolean("soft-restore", false)) {
            restoreRegion();
        }
        
        // Restore chests
        restoreContainerContents();
        
        // Restore enabled status.
        enabled = en;
        
        return true;
    }

    @Override
    public void forceStart()
    {
        if (running)
            return;
        
        // Set operations.
        Set<Player> tmp = new HashSet<Player>();
        tmp.addAll(lobbyPlayers);
        tmp.removeAll(readyPlayers);
        
        // Force leave.
        for (Player p : tmp) {
            playerLeave(p);
        }
        
        startArena();
    }

    @Override
    public void forceEnd() {
        for (Player p : getAllPlayers()) {
            playerLeave(p);
        }
        
        cleanup();
    }

    @Override
    public boolean playerJoin(Player p, Location loc)
    {
        // Fire the event and check if it's been cancelled.
        ArenaPlayerJoinEvent event = new ArenaPlayerJoinEvent(p, this);
        plugin.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return false;
        }
        
        takeFee(p);
        storePlayerData(p, loc);
        removePotionEffects(p);
        MAUtils.sitPets(p);
        setHealth(p, 20);
        p.setFoodLevel(20);
        p.setGameMode(GameMode.SURVIVAL);
        movePlayerToLobby(p);
        
        arenaPlayerMap.put(p, new ArenaPlayer(p, this, plugin));
        
        if (MobArena.hasSpout && settings.getBoolean("spout-class-select"))
            Spouty.classSelectionScreen(plugin, this, p);
        
        // Start the auto-start-timer
        autoStartTimer.start();
        
        // Notify player of joining
        Messenger.tellPlayer(p, Msg.JOIN_PLAYER_JOINED);
        
        // Notify player of time left
        if (autoStartTimer.isRunning()) {
            Messenger.tellPlayer(p, Msg.ARENA_AUTO_START, "" + autoStartTimer.getRemaining());
        }
        
        return true;
    }

    @Override
    public void playerReady(Player p)
    {
        readyPlayers.add(p);
        
        int minPlayers = getMinPlayers();
        if (minPlayers > 0 && lobbyPlayers.size() < minPlayers)
        {
            Messenger.tellPlayer(p, Msg.LOBBY_NOT_ENOUGH_PLAYERS, "" + minPlayers);
            return;
        }
        
        startArena();
    }

    @Override
    public boolean playerLeave(Player p)
    {
        // Fire the event and check if it's been cancelled.
        ArenaPlayerLeaveEvent event = new ArenaPlayerLeaveEvent(p, this);
        plugin.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return false;
        }
        
        removeClassPermissions(p);
        removePotionEffects(p);
        
        ArenaPlayer ap = arenaPlayerMap.get(p);
        if (logging)
            if (ap != null && running)
                log.playerDeath(ap);

        restoreInvAndExp(p);
        if (inLobby(p) || inArena(p)) {
            refund(p);
        }
        
        if (inLobby(p)) {
            if (ap.getArenaClass() != null) {
                limitManager.playerLeftClass(ap.getArenaClass());
            }
        }
        
        movePlayerToEntry(p);
        discardPlayer(p);
        
        endArena();
        return true;
    }

    @Override
    public void playerDeath(Player p)
    {
        // Fire the event
        ArenaPlayerDeathEvent event = new ArenaPlayerDeathEvent(p, this);
        plugin.getServer().getPluginManager().callEvent(event);
        
        ArenaPlayer ap = arenaPlayerMap.get(p);
        if (logging)
            if (ap != null)
                log.playerDeath(ap);
        
        arenaPlayers.remove(p);
        
        if (!settings.getBoolean("auto-respawn", true)) {
            deadPlayers.add(p);
            endArena();
            return;
        }
        
        p.setHealth(20);
        Delays.revivePlayer(plugin, this, p);
        endArena();
    }

    @Override
    public void playerRespawn(Player p) {
        if (settings.getBoolean("auto-respawn", true)) {
            return;
        }
        
        deadPlayers.remove(p);
        revivePlayer(p);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void revivePlayer(Player p) {
        Delays.douse(plugin, p, 1);
        removeClassPermissions(p);
        removePotionEffects(p);
        
        if (settings.getBoolean("spectate-on-death", true)) {
            movePlayerToSpec(p);
            Messenger.tellPlayer(p, Msg.SPEC_FROM_ARENA);
            Messenger.tellPlayer(p, Msg.MISC_MA_LEAVE_REMINDER);
        } else {
            restoreInvAndExp(p);
            movePlayerToEntry(p);
            discardPlayer(p);
        }
        p.updateInventory();
    }

    @Override
    public Location getRespawnLocation(Player p) {
        Location l = null;
        if (settings.getBoolean("spectate-on-death", true)) {
            l = region.getSpecWarp();
        } else {
            l = playerData.get(p).entry();
        }
        return l;
    }

    @Override
    public void playerSpec(Player p, Location loc) {
        storePlayerData(p, loc);
        MAUtils.sitPets(p);
        movePlayerToSpec(p);
        
        Messenger.tellPlayer(p, Msg.SPEC_PLAYER_SPECTATE);
    }

    private void spawnPets() {
        for (Map.Entry<Player,ArenaPlayer> entry : arenaPlayerMap.entrySet()) {
            ArenaClass arenaClass = entry.getValue().getArenaClass();
            int petAmount = arenaClass.getPetAmount();
            
            if (petAmount <= 0) {
                continue;
            }
            
            // Remove the bones from the inventory.
            Player p = entry.getKey();
            p.getInventory().removeItem(new ItemStack(Material.BONE, petAmount));
            
            for (int i = 0; i < petAmount; i++) {
                Wolf wolf = (Wolf) world.spawnEntity(p.getLocation(), EntityType.WOLF);
                wolf.setTamed(true);
                wolf.setOwner(p);
                wolf.setHealth(wolf.getMaxHealth());
                if (settings.getBoolean("hellhounds"))
                    wolf.setFireTicks(32768);
                monsterManager.addPet(wolf);
            }
        }
    }
    
    private void removePotionEffects(Player p) {
        for (PotionEffect effect : p.getActivePotionEffects()) {
            p.removePotionEffect(effect.getType());
        }
    }
    
    private void startSpawner() {
        // Set the spawn flags to enable monster spawning.
        world.setSpawnFlags(true, true);
        //world.setDifficulty(Difficulty.NORMAL);
        
        // Create a spawner if one doesn't exist, otherwise reset it
        if (spawnThread == null) {
            spawnThread  = new MASpawnThread(plugin, this);
        } else {
            spawnThread.reset();
        }
        
        // Schedule it for the initial first wave delay.
        scheduleTask(spawnThread, settings.getInt("first-wave-delay", 5) * 20);
        
        // Schedule to enable PvP if pvp-enabled: true
        scheduleTask(new Runnable() {
            public void run() {
                eventListener.pvpActivate();
            }
        }, settings.getInt("first-wave-delay", 5) * 20);
    }
    
    /**
     * Schedule a Runnable to be executed after the given delay in
     * server ticks. The method is used by the MASpawnThread to
     * repeatedly spawn new mobs instead of a scheduled repeating
     * tasks, as well as the sheep bouncer.
     */
    @Override
    public void scheduleTask(Runnable r, int delay) {
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(
                plugin,
                r,
                delay);
    }
    
    private void stopSpawner() {
        world.setSpawnFlags(allowMonsters, allowAnimals);
        eventListener.pvpDeactivate();
        //world.setDifficulty(spawnMonsters);
    }
    
    private void startBouncingSheep()
    {
        // Create a new bouncer if necessary.
        if (sheepBouncer == null) {
            sheepBouncer = new SheepBouncer(this);
        }
        
        // Start bouncing!
        scheduleTask(sheepBouncer, settings.getInt("first-wave-delay", 5) * 20);
    }

    @Override
    public void storePlayerData(Player p, Location loc)
    {
        plugin.getArenaMaster().addPlayer(p, this);
        
        PlayerData mp = playerData.get(p);
        
        // If there's no player stored, create a new one!
        if (mp == null) {
            mp = new PlayerData(p);
            playerData.put(p, mp);
        }
        
        // At any rate, update the data.
        mp.update();
        
        // And update the inventory as well.
        inventoryManager.storeInventory(p);
    }

    @Override
    public void storeContainerContents()
    {
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin,
            new Runnable() {
                public void run() {
                    for (Location loc : region.getContainers()) {
                        BlockState state = world.getBlockAt(loc).getState();
                        if (state instanceof InventoryHolder) {
                            containables.add(new RepairableContainer(state, false));
                        }
                    }
                }
            });
    }

    @Override
    public void restoreContainerContents()
    {
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin,
            new Runnable() {
                public void run() {
                    for (Repairable r : containables) {
                        r.repair();
                    }
                }
            });
    }

    @Override
    public void movePlayerToLobby(Player p)
    {
        specPlayers.remove(p); // If joining from spec area
        lobbyPlayers.add(p);
        p.teleport(region.getLobbyWarp());
        timeStrategy.setPlayerTime(p);
    }

    @Override
    public void movePlayerToSpec(Player p)
    {
        specPlayers.add(p);
        p.teleport(region.getSpecWarp());
        timeStrategy.setPlayerTime(p);
    }

    @Override
    public void movePlayerToEntry(Player p)
    {
        Location entry = playerData.get(p).entry();
        if (entry == null || p.isDead()) return;
        
        p.teleport(entry);
        timeStrategy.resetPlayerTime(p);
        
        p.setGameMode(playerData.get(p).getMode());
        p.addPotionEffects(playerData.get(p).getPotionEffects());
    }
    
    private void restoreInvAndExp(Player p) {
        inventoryManager.clearInventory(p);
        inventoryManager.restoreInventory(p);
        rewardManager.grantRewards(p);
        
        if (!settings.getBoolean("keep-exp", false)) {
            playerData.get(p).restoreData();
        }
        else {
            p.setFoodLevel(playerData.get(p).food());
        }
    }

    @Override
    public void discardPlayer(Player p)
    {
        plugin.getArenaMaster().removePlayer(p);
        clearPlayer(p);
    }
    
    private void clearPlayer(Player p)
    {
        // Remove the player data completely.
        PlayerData mp = playerData.remove(p);
        
        // Health must be handled in a certain way because of Heroes
        setHealth(p, mp.health());
        
        // Put out fire.
        Delays.douse(plugin, p, 3);
        
        // Remove pets.
        monsterManager.removePets(p);
        
        // readyPlayers before lobbyPlayers because of startArena sanity-checks
        readyPlayers.remove(p);
        specPlayers.remove(p);
        arenaPlayers.remove(p);
        lobbyPlayers.remove(p);
        arenaPlayerMap.remove(p);
    }
    
    private void setHealth(Player p, int health) {
        plugin.getHealthStrategy().setHealth(p, health);
    }

    @Override
    public void repairBlocks()
    {
        while (!repairQueue.isEmpty())
            repairQueue.poll().repair();
    }

    @Override
    public void queueRepairable(Repairable r)
    {
        repairQueue.add(r);
    }
    
    
    
    /*////////////////////////////////////////////////////////////////////
    //
    //      Items & Cleanup
    //
    ////////////////////////////////////////////////////////////////////*/

    @Override
    public void assignClass(Player p, String className) {
        ArenaPlayer arenaPlayer = arenaPlayerMap.get(p);
        ArenaClass arenaClass   = classes.get(className);
        
        if (arenaPlayer == null || arenaClass == null) {
            return;
        }
        
        inventoryManager.clearInventory(p);
        
        arenaPlayer.setArenaClass(arenaClass);
        arenaClass.grantItems(p);
    }
    
    @Override
    public void addRandomPlayer(Player p) {
        randoms.add(p);
    }

    @Override
    public void assignRandomClass(Player p)
    {
        Random r = new Random();
        List<String> classes = new LinkedList<String>(this.classes.keySet());

        String className = classes.remove(r.nextInt(classes.size()));
        while (!plugin.has(p, "mobarena.classes." + className))
        {
            if (classes.isEmpty())
            {
                Messenger.info("Player '" + p.getName() + "' has no class permissions!");
                playerLeave(p);
                return;
            }
            className = classes.remove(r.nextInt(classes.size()));
        }
        
        assignClass(p, className);
        Messenger.tellPlayer(p, Msg.LOBBY_CLASS_PICKED, TextUtils.camelCase(className), getClassLogo(className));
    }

    @Override
    public void assignClassPermissions(Player p)
    {
        PermissionAttachment pa = arenaPlayerMap.get(p).getArenaClass().grantPermissions(plugin, p);
        if (pa == null) return;
        
        attachments.put(p, pa);
        p.recalculatePermissions();
    }

    @Override
    public void removeClassPermissions(Player p)
    {
        PermissionAttachment pa = attachments.remove(p);
        if (pa == null) return;
        
        try {
            p.removeAttachment(pa);
        }
        catch (Exception e) {
            for (Entry<String,Boolean> entry : pa.getPermissions().entrySet()) {
                String perm = entry.getKey() + ":" + entry.getValue();
                String name = p.getName();

                Messenger.warning("[PERM01] Failed to remove permission attachment '" + perm + "' from player '" + name
                                  + "'.\nThis should not be a big issue, but please verify that the player doesn't have any permissions they shouldn't have.");
            }
        }
        p.recalculatePermissions();
    }
    
    private void cleanup() {
        removeMonsters();
        removeBlocks();
        removeEntities();
        clearPlayers();
    }
    
    private void removeMonsters() {
        monsterManager.clear();
    }
    
    private void removeBlocks() {
        for (Block b : blocks) {
            b.setTypeId(0);
        }
        blocks.clear();
    }
    
    private void removeEntities() {
        List<Chunk> chunks = region.getChunks();
        
        for (Chunk c : chunks) {
            for (Entity e : c.getEntities()) {
                if (!(e instanceof Item || e instanceof Vehicle || e instanceof Slime || e instanceof ExperienceOrb)) {
                    continue;
                }
                
                if (e != null) {
                    e.remove();
                }
            }
        }
    }
    
    private void clearPlayers() {
        arenaPlayers.clear();
        arenaPlayerMap.clear();
        lobbyPlayers.clear();
        readyPlayers.clear();
    }
    
    
    
    /*////////////////////////////////////////////////////////////////////
    //
    //      Initialization & Checks
    //
    ////////////////////////////////////////////////////////////////////*/

    @Override
    public void restoreRegion()
    {
        Collections.sort(repairables, new RepairableComparator());
        
        for (Repairable r : repairables)
            r.repair();
    }
    
    
    
    /*////////////////////////////////////////////////////////////////////
    //
    //      Getters & Misc
    //
    ////////////////////////////////////////////////////////////////////*/

    @Override
    public boolean inArena(Player p) {
        return arenaPlayers.contains(p);
    }

    @Override
    public boolean inLobby(Player p) {
        return lobbyPlayers.contains(p);
    }

    @Override
    public boolean inSpec(Player p) {
        return specPlayers.contains(p);
    }

    @Override
    public boolean isDead(Player p) {
        return deadPlayers.contains(p);
    }

    @Override
    public String configName()
    {
        return name;
    }

    @Override
    public String arenaName()
    {
        return MAUtils.nameConfigToArena(name);
    }

    @Override
    public MobArena getPlugin()
    {
        return plugin;
    }

    @Override
    public Wave getWave()
    {
        return currentWave;
    }

    @Override
    public Map<String,ArenaClass> getClasses()
    {
        return classes;
    }

    @Override
    public int getPlayerCount()
    {
        return spawnThread.getPlayerCount();
    }

    @Override
    public List<Player> getAllPlayers()
    {
        List<Player> result = new LinkedList<Player>();
        result.addAll(arenaPlayers);
        result.addAll(lobbyPlayers);
        result.addAll(specPlayers);
        
        return result;
    }

    @Override
    public Collection<ArenaPlayer> getArenaPlayerSet()
    {
        return arenaPlayerMap.values();
    }

    /*@Override
    public List<ArenaPlayerStatistics> getArenaPlayerStatistics(Comparator<ArenaPlayerStatistics> comparator)
    {
        List<ArenaPlayerStatistics> list = new ArrayList<ArenaPlayerStatistics>();
        
        for (ArenaPlayer ap : arenaPlayerMap.values())
            list.add(ap.getStats());
        
        Collections.sort(list, comparator);
        return list;
    }*/

    @Override
    public List<Player> getNonreadyPlayers()
    {
        List<Player> result = new LinkedList<Player>();
        result.addAll(lobbyPlayers);
        result.removeAll(readyPlayers);
        return result;
    }

    @Override
    public boolean canAfford(Player p) {
        if (entryFee.isEmpty()) return true;
        
        PlayerInventory inv = p.getInventory();
        for (ItemStack stack : entryFee) {
            // Economy money
            if (stack.getTypeId() == MobArena.ECONOMY_MONEY_ID) {
                if (!plugin.hasEnough(p, stack.getAmount())) {
                    return false;
                }
            }
            // Normal stack
            else {
                if (!inv.contains(stack.getTypeId(), stack.getAmount())) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean takeFee(Player p) {
        if (entryFee.isEmpty()) return true;
        
        PlayerInventory inv = p.getInventory();

        // Take some economy money        
        for (ItemStack stack : InventoryUtils.extractAll(MobArena.ECONOMY_MONEY_ID, entryFee)) {
            plugin.takeMoney(p, stack.getAmount());
        }
        
        // Take any other items
        for (ItemStack stack : entryFee) {
            inv.removeItem(stack);
        }
        
        Messenger.tellPlayer(p, Msg.JOIN_FEE_PAID.toString(MAUtils.listToString(entryFee, plugin)));
        return true;
    }
    
    @Override
    public boolean refund(Player p) {
        if (entryFee.isEmpty()) return true;
        if (!inLobby(p)) return false;
        
        // Refund economy money
        for (ItemStack stack : InventoryUtils.extractAll(MobArena.ECONOMY_MONEY_ID, entryFee)) {
            plugin.giveMoney(p, stack.getAmount());
        }
        
        // Refund other items.
        for (ItemStack stack : entryFee) {
            if (stack.getTypeId() > 0) {
                p.getInventory().addItem(stack);
            }
        }
        return true;
    }

    @Override
    public boolean canJoin(Player p) {
        if (!enabled)
            Messenger.tellPlayer(p, Msg.JOIN_ARENA_NOT_ENABLED);
        else if (!region.isSetup() || waveManager.getRecurrentWaves().isEmpty())
            Messenger.tellPlayer(p, Msg.JOIN_ARENA_NOT_SETUP);
        else if (edit)
            Messenger.tellPlayer(p, Msg.JOIN_ARENA_EDIT_MODE);
        else if (arenaPlayers.contains(p) || lobbyPlayers.contains(p))
            Messenger.tellPlayer(p, Msg.JOIN_ALREADY_PLAYING);
        else if (running)
            Messenger.tellPlayer(p, Msg.JOIN_ARENA_IS_RUNNING);
        else if (!plugin.has(p, "mobarena.arenas." + configName()))
            Messenger.tellPlayer(p, Msg.JOIN_ARENA_PERMISSION);
        else if (getMaxPlayers() > 0 && lobbyPlayers.size() >= getMaxPlayers())
            Messenger.tellPlayer(p, Msg.JOIN_PLAYER_LIMIT_REACHED);
        else if (getJoinDistance() > 0 && !region.contains(p.getLocation(), getJoinDistance()))
            Messenger.tellPlayer(p, Msg.JOIN_TOO_FAR);
        else if (settings.getBoolean("require-empty-inv-join", true) && !InventoryManager.hasEmptyInventory(p))
            Messenger.tellPlayer(p, Msg.JOIN_EMPTY_INV);
        else if (!canAfford(p))
            Messenger.tellPlayer(p, Msg.JOIN_FEE_REQUIRED, MAUtils.listToString(entryFee, plugin));
        else return true;
        
        return false;
    }

    @Override
    public boolean canSpec(Player p) {
        if (!enabled)
            Messenger.tellPlayer(p, Msg.JOIN_ARENA_NOT_ENABLED);
        else if (!region.isSetup())
            Messenger.tellPlayer(p, Msg.JOIN_ARENA_NOT_SETUP);
        else if (edit)
            Messenger.tellPlayer(p, Msg.JOIN_ARENA_EDIT_MODE);
        else if (arenaPlayers.contains(p) || lobbyPlayers.contains(p))
            Messenger.tellPlayer(p, Msg.SPEC_ALREADY_PLAYING);
        else if (settings.getBoolean("require-empty-inv-spec", true) && !InventoryManager.hasEmptyInventory(p))
            Messenger.tellPlayer(p, Msg.SPEC_EMPTY_INV);
        else if (getJoinDistance() > 0 && !region.contains(p.getLocation(), getJoinDistance()))
            Messenger.tellPlayer(p, Msg.JOIN_TOO_FAR);
        else return true;
        
        return false;
    }
        
    /**
     * The "perfect equals method" cf. "Object-Oriented Design and Patterns"
     * by Cay S. Horstmann.
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null) return false;
        if (getClass() != other.getClass()) return false;
        
        // Arenas must have different names.
        if (other instanceof ArenaImpl && ((ArenaImpl)other).name.equals(name))
            return true;
        
        return false;
    }

    @Override
    public String toString() {
        return ((enabled && region.isSetup()) ? ChatColor.GREEN : ChatColor.GRAY) + configName();
    }
}
