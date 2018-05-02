package com.garbagemule.MobArena;

import static com.garbagemule.MobArena.util.config.ConfigUtils.makeSection;

import com.garbagemule.MobArena.ArenaClass.ArmorType;
import com.garbagemule.MobArena.ScoreboardManager.NullScoreboardManager;
import com.garbagemule.MobArena.steps.Step;
import com.garbagemule.MobArena.steps.StepFactory;
import com.garbagemule.MobArena.steps.PlayerJoinArena;
import com.garbagemule.MobArena.steps.PlayerSpecArena;
import com.garbagemule.MobArena.events.ArenaEndEvent;
import com.garbagemule.MobArena.events.ArenaPlayerDeathEvent;
import com.garbagemule.MobArena.events.ArenaPlayerJoinEvent;
import com.garbagemule.MobArena.events.ArenaPlayerLeaveEvent;
import com.garbagemule.MobArena.events.ArenaPlayerReadyEvent;
import com.garbagemule.MobArena.events.ArenaStartEvent;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.leaderboards.Leaderboard;
import com.garbagemule.MobArena.region.ArenaRegion;
import com.garbagemule.MobArena.repairable.Repairable;
import com.garbagemule.MobArena.repairable.RepairableComparator;
import com.garbagemule.MobArena.repairable.RepairableContainer;
import com.garbagemule.MobArena.things.Thing;
import com.garbagemule.MobArena.util.ClassChests;
import com.garbagemule.MobArena.util.inventory.InventoryManager;
import com.garbagemule.MobArena.util.timer.AutoStartTimer;
import com.garbagemule.MobArena.util.timer.StartDelayTimer;
import com.garbagemule.MobArena.waves.SheepBouncer;
import com.garbagemule.MobArena.waves.WaveManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class ArenaImpl implements Arena
{
    // General stuff
    private MobArena plugin;
    private String name;
    private World world;
    private Messenger messenger;
    
    // Settings section of the config-file for this arena.
    private ConfigurationSection settings;
    
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
    
    private Set<Player> arenaPlayers, lobbyPlayers, readyPlayers, specPlayers, deadPlayers;
    private Set<Player> movingPlayers;
    private Set<Player> leavingPlayers;
    private Set<Player> randoms;
    
    // Classes stuff
    private ArenaClass defaultClass;
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
    private MASpawnThread spawnThread;
    private SheepBouncer  sheepBouncer;
    private Map<Integer,List<Thing>> everyWaveMap, afterWaveMap;
    
    // Misc
    private ArenaListener eventListener;
    private List<Thing> entryFee;
    private AutoStartTimer autoStartTimer;
    private StartDelayTimer startDelayTimer;
    private boolean isolatedChat;
    
    // Scoreboards
    private ScoreboardManager scoreboard;

    // Last player standing
    private Player lastStanding;
    
    // Actions
    private Map<Player, Step> histories;
    private StepFactory playerJoinArena;
    private StepFactory playerSpecArena;

    /**
     * Primary constructor. Requires a name and a world.
     */
    public ArenaImpl(MobArena plugin, ConfigurationSection section, String name, World world) {
        if (world == null)
            throw new NullPointerException("[MobArena] ERROR! World for arena '" + name + "' does not exist!");
        
        this.name     = name;
        this.world    = world;
        this.plugin   = plugin;
        this.settings = makeSection(section, "settings");
        this.region   = new ArenaRegion(section, this);
        
        this.enabled = settings.getBoolean("enabled", false);
        this.protect = settings.getBoolean("protect", true);
        this.running = false;
        this.edit    = false;

        this.inventoryManager = new InventoryManager();
        this.rewardManager    = new RewardManager(this);

        // Warps, points and locations
        this.leaderboard = new Leaderboard(plugin, this, region.getLeaderboard());

        // Player stuff
        this.arenaPlayerMap = new HashMap<>();
        this.arenaPlayers   = new HashSet<>();
        this.lobbyPlayers   = new HashSet<>();
        this.readyPlayers   = new HashSet<>();
        this.specPlayers    = new HashSet<>();
        this.deadPlayers    = new HashSet<>();
        this.randoms        = new HashSet<>();
        this.movingPlayers  = new HashSet<>();
        this.leavingPlayers = new HashSet<>();

        // Classes, items and permissions
        this.classes      = plugin.getArenaMaster().getClasses();
        this.attachments  = new HashMap<>();
        this.limitManager = new ClassLimitManager(this, classes, makeSection(section, "class-limits"));

        String defaultClassName = settings.getString("default-class", null);
        if (defaultClassName != null) {
            this.defaultClass = classes.get(defaultClassName);
        }
        
        // Blocks and pets
        this.repairQueue  = new PriorityBlockingQueue<>(100, new RepairableComparator());
        this.blocks       = new HashSet<>();
        this.repairables  = new LinkedList<>();
        this.containables = new LinkedList<>();
        
        // Monster stuff
        this.monsterManager = new MonsterManager();
        
        // Wave stuff
        this.waveManager  = new WaveManager(this, section.getConfigurationSection("waves"));
        this.everyWaveMap = MAUtils.getArenaRewardMap(plugin, section, name, "every");
        this.afterWaveMap = MAUtils.getArenaRewardMap(plugin, section, name, "after");
        
        // Misc
        this.eventListener = new ArenaListener(this, plugin);
        this.allowMonsters = world.getAllowMonsters();
        this.allowAnimals  = world.getAllowAnimals();

        this.entryFee = new ArrayList<>();
        String feeString = settings.getString("entry-fee", "");
        if (feeString != null && !feeString.isEmpty()) {
            for (String fee : feeString.split(",")) {
                try {
                    Thing thing = plugin.getThingManager().parse(fee.trim());
                    if (thing == null) {
                        plugin.getLogger().warning("Failed to parse entry fee: " + fee.trim());
                    } else {
                        this.entryFee.add(thing);
                    }
                } catch (Exception e) {
                    plugin.getLogger().severe("Exception parsing entry fee '" + fee.trim() + "': " + e.getLocalizedMessage());
                }
            }
        }

        this.autoStartTimer  = new AutoStartTimer(this);
        this.startDelayTimer = new StartDelayTimer(this, autoStartTimer);

        this.isolatedChat  = settings.getBoolean("isolated-chat", false);
        
        // Scoreboards
        this.scoreboard = (settings.getBoolean("use-scoreboards", true) ? new ScoreboardManager(this) : new NullScoreboardManager(this));

        // Messenger
        String prefix = settings.getString("prefix", "");
        this.messenger = !prefix.isEmpty() ? new Messenger(prefix) : plugin.getGlobalMessenger();

        // Actions
        this.histories = new HashMap<>();
        this.playerJoinArena = PlayerJoinArena.create(this);
        this.playerSpecArena = PlayerSpecArena.create(this);
    }
    
    
    
    /*/////////////////////////////////////////////////////////////////////////
    //
    //      NEW METHODS IN REFACTORING
    //
    /////////////////////////////////////////////////////////////////////////*/
    
    @Override
    public ConfigurationSection getSettings() {
        return settings;
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public void setWorld(World world) {
        this.world = world;
        settings.set("world", world.getName());
        plugin.saveConfig();
        if (region != null) region.refreshWorld();
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

    @Override
    public int getMinPlayers() {
        return settings.getInt("min-players");
    }

    @Override
    public int getMaxPlayers() {
        return settings.getInt("max-players");
    }
    
    private int getJoinDistance() {
        return settings.getInt("max-join-distance");
    }

    @Override
    public List<Thing> getEntryFee() {
        return entryFee;
    }

    @Override
    public Set<Map.Entry<Integer,List<Thing>>> getEveryWaveEntrySet() {
        return everyWaveMap.entrySet();
    }

    @Override
    public List<Thing> getAfterWaveReward(int wave) {
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
    public ScoreboardManager getScoreboard() {
        return scoreboard;
    }
    
    
    
    
    
    
    
    
    

    @Override
    public Messenger getMessenger() {
        return messenger;
    }

    @Override
    public Messenger getGlobalMessenger() {
        return plugin.getGlobalMessenger();
    }

    @Override
    public void announce(String msg) {
        for (Player p : getAllPlayers()) {
            messenger.tell(p, msg);
        }
    }

    @Override
    public void announce(Msg msg, String s) {
        announce(msg.format(s));
    }

    @Override
    public void announce(Msg msg) {
        announce(msg.toString());
    }

    @Override
    public boolean startArena() {
        // Sanity-checks
        if (running || lobbyPlayers.isEmpty() || !readyPlayers.containsAll(lobbyPlayers)) {
            return false;
        }

        // Check if start-delay is over
        if (startDelayTimer.isRunning()) {
            return false;
        }

        // Stop the auto-start-timer regardless
        autoStartTimer.stop();

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
        
        // Initialize scoreboards
        scoreboard.initialize();
        
        // Teleport players, give full health, initialize map
        for (Player p : arenaPlayers) {
            // TODO figure out how people die in lobby and get sent to spectator area early
            // Remove player from spec list to avoid invincibility issues
            if (inSpec(p)) {
                specPlayers.remove(p);
                System.out.println("[MobArena] Player " + p.getName() + " joined the arena from the spec area!");
                System.out.println("[MobArena] Invincibility glitch attempt stopped!");
            }
            
            movingPlayers.add(p);
            p.teleport(region.getArenaWarp());
            movingPlayers.remove(p);

            assignClassPermissions(p);
            arenaPlayerMap.get(p).resetStats();

            Thing price = arenaPlayerMap.get(p).getArenaClass().getPrice();
            if (price != null) {
                price.takeFrom(p);
            }
            
            scoreboard.addPlayer(p);
        }
        
        // Start spawning monsters (must happen before 'running = true;')
        startSpawner();
        startBouncingSheep();
        
        // Set the boolean.
        running = true;
        
        // Spawn pets (must happen after 'running = true;')
        spawnPets();
        
        // Spawn mounts
        spawnMounts();
        
        // Clear the classes in use map, as they're no longer needed
        limitManager.clearClassesInUse();
        
        // Reset rewards
        rewardManager.reset();
        
        // Initialize leaderboards and start displaying info.
        leaderboard.initialize();
        leaderboard.startTracking();
        
        announce(Msg.ARENA_START);
        
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

        // Reset last standing
        lastStanding = null;
        
        // Set the running boolean and disable arena if not disabled.
        boolean en = enabled;
        enabled = false;
        running = false;
        
        // Stop tracking leaderboards
        leaderboard.stopTracking();
        leaderboard.update();
        
        // Stop spawning.
        stopSpawner();

        // Announce and clean arena floor, etc.
        if (settings.getBoolean("global-end-announce", false)) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                messenger.tell(p, Msg.ARENA_END_GLOBAL, configName());
            }
        } else {
            announce(Msg.ARENA_END);
        }
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
        Set<Player> tmp = new HashSet<>();
        tmp.addAll(lobbyPlayers);
        tmp.removeAll(readyPlayers);
        
        // Force leave.
        for (Player p : tmp) {
            playerLeave(p);
            messenger.tell(p, Msg.LEAVE_NOT_READY);
        }

        // Stop start-delay-timer and start arena
        startDelayTimer.stop();
        startArena();
    }

    @Override
    public void forceEnd() {
        List<Player> players = getAllPlayers();
        if (players.isEmpty()) {
            return;
        }
        
        players.forEach(this::playerLeave);
        cleanup();
    }

    @Override
    public boolean hasPermission(Player p) {
        String perm = "mobarena.arenas." + name;
        return !p.isPermissionSet(perm) || p.hasPermission(perm);
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

        if (movingPlayers.contains(p)) {
            return false;
        }
        movingPlayers.add(p);

        rollback(p);

        specPlayers.remove(p);

        // Announce globally (must happen before moving player)
        if (settings.getBoolean("global-join-announce", false)) {
            if (lobbyPlayers.isEmpty()) {
                for (Player q : Bukkit.getOnlinePlayers()) {
                    messenger.tell(q, Msg.ARENA_JOIN_GLOBAL, configName());
                }
            }
        }

        Step step = playerJoinArena.create(p);
        try {
            step.run();
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, () -> "Player " + p.getName() + " couldn't join arena " + name);
            return false;
        }
        histories.put(p, step);

        lobbyPlayers.add(p);
        plugin.getArenaMaster().addPlayer(p, this);
        
        arenaPlayerMap.put(p, new ArenaPlayer(p, this, plugin));

        // Start the start-delay-timer if applicable
        if (!autoStartTimer.isRunning()) {
            startDelayTimer.start();
        }
        
        // Notify player of joining
        messenger.tell(p, Msg.JOIN_PLAYER_JOINED);
        
        // Notify player of time left
        if (startDelayTimer.isRunning()) {
            messenger.tell(p, Msg.ARENA_START_DELAY, "" + startDelayTimer.getRemaining() / 20l);
        } else if (autoStartTimer.isRunning()) {
            messenger.tell(p, Msg.ARENA_AUTO_START, "" + autoStartTimer.getRemaining() / 20l);
        }

        if (defaultClass != null) {
            // Assign default class if applicable
            if (!ClassChests.assignClassFromStoredClassChest(this, p, defaultClass)) {
                assignClass(p, defaultClass.getLowercaseName());
                messenger.tell(p, Msg.LOBBY_CLASS_PICKED, defaultClass.getConfigName());
            }
        }
        
        movingPlayers.remove(p);
        return true;
    }

    @Override
    public void playerReady(Player p)
    {
        ArenaPlayerReadyEvent event = new ArenaPlayerReadyEvent(p, this);
        plugin.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        readyPlayers.add(p);
        
        int minPlayers = getMinPlayers();
        if (minPlayers > 0 && lobbyPlayers.size() < minPlayers)
        {
            messenger.tell(p, Msg.LOBBY_NOT_ENOUGH_PLAYERS, "" + minPlayers);
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

        // Protect against infinite leave loops
        if (leavingPlayers.contains(p)) {
            return false;
        }
        leavingPlayers.add(p);

        // Clear inventory if player is an arena player, and unmount
        if (arenaPlayers.contains(p)) {
            unmount(p);
            clearInv(p);
        }
        
        removeClassPermissions(p);
        removePotionEffects(p);
        
        if (inLobby(p) || inArena(p)) {
            refund(p);
        }

        if (inLobby(p)) {
            ArenaPlayer ap = arenaPlayerMap.get(p);
            if (ap.getArenaClass() != null) {
                limitManager.playerLeftClass(ap.getArenaClass(), ap.getPlayer());
            }

            // Last lobby player leaving? Stop the timer
            if (lobbyPlayers.size() == 1) {
                startDelayTimer.stop();
            }
        }
        
        discardPlayer(p);
        
        endArena();

        leavingPlayers.remove(p);
        return true;
    }

    @Override
    public boolean isMoving(Player p) {
        return movingPlayers.contains(p) || leavingPlayers.contains(p);
    }

    @Override
    public boolean isLeaving(Player p) {
        return leavingPlayers.contains(p);
    }

    @Override
    public void playerDeath(Player p)
    {
        // Check if we're the last player standing
        boolean last = arenaPlayers.size() == 1;
        if (last) lastStanding = p;

        // Fire the event
        ArenaPlayerDeathEvent event = new ArenaPlayerDeathEvent(p, this, last);
        plugin.getServer().getPluginManager().callEvent(event);

        // Clear the player's inventory, and unmount
        if (arenaPlayers.remove(p)) {
            unmount(p);
            clearInv(p);
        }
        
        if (!settings.getBoolean("auto-respawn", true)) {
            deadPlayers.add(p);
            endArena();
            return;
        }
        
        p.setHealth(20.0);
        plugin.getServer().getScheduler()
            .scheduleSyncDelayedTask(plugin, () -> revivePlayer(p));
        endArena();
    }

    private void clearInv(Player p) {
        InventoryView view = p.getOpenInventory();
        if (view != null) {
            view.setCursor(new ItemStack(0));
            view.getBottomInventory().clear();
            view.close();
        }
    }

    private void unmount(Player p) {
        Entity v = p.getVehicle();
        if (v != null) {
            monsterManager.removeMount(v);
            v.eject();
            v.remove();
        }
    }

    @Override
    public void playerRespawn(Player p) {
        if (settings.getBoolean("auto-respawn", true)) {
            return;
        }
        
        deadPlayers.remove(p);
        plugin.getServer().getScheduler()
            .scheduleSyncDelayedTask(plugin, () -> revivePlayer(p));
    }

    @Override
    @SuppressWarnings("deprecation")
    public void revivePlayer(Player p) {
        removeClassPermissions(p);
        removePotionEffects(p);
        
        discardPlayer(p);
        if (settings.getBoolean("spectate-on-death", true)) {
            playerSpec(p, null);
        }
    }

    @Override
    public Location getRespawnLocation(Player p) {
        return region.getSpecWarp();
    }

    @Override
    public void playerSpec(Player p, Location loc) {
        if (movingPlayers.contains(p)) {
            return;
        }
        movingPlayers.add(p);

        
        rollback(p);

        Step step = playerSpecArena.create(p);
        try {
            step.run();
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, () -> "Player " + p.getName() + " couldn't spec arena " + name);
            return;
        }
        histories.put(p, step);

        specPlayers.add(p);
        plugin.getArenaMaster().addPlayer(p, this);
        
        messenger.tell(p, Msg.SPEC_PLAYER_SPECTATE);
        movingPlayers.remove(p);
    }

    private void rollback(Player p) {
        Step step = histories.remove(p);
        if (step == null) {
            return;
        }
        try {
            step.undo();
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, () -> "Failed to revert player " + p.getName());
        }
    }

    private void spawnPets() {
        for (Player p : arenaPlayers) {
            // Skip players who are either null or offline
            if (p == null || !p.isOnline()) continue;

            // Skip the My Items class
            ArenaClass ac = arenaPlayerMap.get(p).getArenaClass();
            if (ac == null || ac.getConfigName().equals("My Items")) continue;

            // Grab the inventory
            PlayerInventory inv = p.getInventory();
            if (inv == null) continue;

            // Find the first slot containing bones
            int bone = inv.first(Material.BONE);
            if (bone == -1) continue;
            
            // Get the amount of pets to spawn
            int amount = inv.getItem(bone).getAmount();
            
            // Spawn each pet
            for (int i = 0; i < amount; i++) {
                Wolf wolf = (Wolf) world.spawnEntity(p.getLocation(), EntityType.WOLF);
                wolf.setTamed(true);
                wolf.setOwner(p);
                wolf.setHealth(wolf.getMaxHealth());
                if (settings.getBoolean("hellhounds"))
                    wolf.setFireTicks(32768);
                monsterManager.addPet(wolf);
            }
            
            // Remove the bones
            inv.setItem(bone, null);
        }
    }
    
    private void spawnMounts() {
        for (Player p : arenaPlayers) {
            // Skip players who are either null or offline
            if (p == null || !p.isOnline()) continue;

            // Skip the My Items class
            ArenaClass ac = arenaPlayerMap.get(p).getArenaClass();
            if (ac == null || ac.getConfigName().equals("My Items")) continue;

            // Grab the inventory
            PlayerInventory inv = p.getInventory();
            if (inv == null) continue;

            // Find the first slot containing a haybale
            int hay = inv.first(Material.HAY_BLOCK);
            if (hay == -1) continue;

            // Grab the amount and calculate the configuration
            int amount = inv.getItem(hay).getAmount();

            // Variant
            EntityType type = horseTypeFromAmount(amount);

            // Spawn the horse, set its variant, tame it, etc.
            AbstractHorse mount = (AbstractHorse) world.spawnEntity(p.getLocation(), type);
            if (MobArena.random.nextInt(20) == 0) {
                mount.setBaby();
            } else {
                mount.setAdult();
            }
            mount.setTamed(true);
            mount.setOwner(p);
            mount.setPassenger(p);
            mount.setHealth(mount.getMaxHealth());

            // Add saddle
            mount.getInventory().addItem(new ItemStack(Material.SADDLE));

            // Normal horses may have barding
            if (type == EntityType.HORSE) {
                Material barding = bardingFromAmount(amount);
                if (barding != null) {
                    ((Horse) mount).getInventory().setArmor(new ItemStack(barding));
                }
            }

            // Add to monster manager
            monsterManager.addMount(mount);

            // Remove the hay
            inv.setItem(hay, null);
        }
    }

    private EntityType horseTypeFromAmount(int amount) {
        switch (amount % 8) {
            case 2:  return EntityType.DONKEY;
            case 3:  return EntityType.MULE;
            case 4:  return EntityType.SKELETON_HORSE;
            case 5:  return EntityType.ZOMBIE_HORSE;
            default: return EntityType.HORSE;
        }
    }

    private Material bardingFromAmount(int amount) {
        switch ((amount >> 3) % 4) {
            case 1:  return Material.IRON_BARDING;
            case 2:  return Material.GOLD_BARDING;
            case 3:  return Material.DIAMOND_BARDING;
            default: return null;
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
        Bukkit.getScheduler().runTaskLater(plugin, r, delay);
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
    public void storeContainerContents()
    {
        for (Location loc : region.getContainers()) {
            BlockState state = world.getBlockAt(loc).getState();
            if (state instanceof InventoryHolder) {
                containables.add(new RepairableContainer(state, false));
            }
        }
    }

    @Override
    public void restoreContainerContents()
    {
        for (Repairable r : containables) {
            r.repair();
        }
    }

    @Override
    public void discardPlayer(Player p)
    {
        rollback(p);
        plugin.getArenaMaster().removePlayer(p);
        clearPlayer(p);
    }
    
    private void clearPlayer(Player p)
    {
        // Remove pets.
        monsterManager.removePets(p);
        
        // readyPlayers before lobbyPlayers because of startArena sanity-checks
        readyPlayers.remove(p);
        specPlayers.remove(p);
        arenaPlayers.remove(p);
        lobbyPlayers.remove(p);
        arenaPlayerMap.remove(p);
        
        scoreboard.removePlayer(p);
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
        
        InventoryManager.clearInventory(p);
        
        arenaPlayer.setArenaClass(arenaClass);
        arenaClass.grantItems(p);

        PermissionAttachment pa = arenaClass.grantLobbyPermissions(plugin, p);
        replacePermissions(p, pa);

        autoReady(p);
    }
    
    @Override
    public void assignClassGiveInv(Player p, String className, ItemStack[] contents) {
        ArenaPlayer arenaPlayer = arenaPlayerMap.get(p);
        ArenaClass arenaClass   = classes.get(className);
        
        if (arenaPlayer == null || arenaClass == null) {
            return;
        }
        
        InventoryManager.clearInventory(p);
        arenaPlayer.setArenaClass(arenaClass);
        
        PlayerInventory inv = p.getInventory();

        // Collect armor items, because setContents() now overwrites everyhing
        ItemStack helmet = null;
        ItemStack chestplate = null;
        ItemStack leggings = null;
        ItemStack boots = null;
        ItemStack offhand = null;
        
        // Check the very last slot to see if it'll work as a helmet
        int last = contents.length-1;
        if (contents[last] != null) {
            helmet = contents[last].clone();
            contents[last] = null;
        }

        // Check the remaining three of the four last slots for armor
        for (int i = contents.length-1; i > contents.length-5; i--) {
            if (contents[i] == null) continue;
            ArmorType type = ArmorType.getType(contents[i]);
            if (type == null || type == ArmorType.HELMET) continue;
            
            switch (type) {
                case CHESTPLATE: chestplate = contents[i].clone(); break;
                case LEGGINGS:   leggings   = contents[i].clone(); break;
                case BOOTS:      boots      = contents[i].clone(); break;
                default: break;
            }
            contents[i] = null;
        }
        
        // Equip the fifth last slot as the off-hand
        ItemStack fifth = contents[contents.length - 5];
        if (fifth != null) {
            offhand = fifth.clone();
            contents[contents.length - 5] = null;
        }

        // Check the remaining slots for weapons
        if (arenaClass.hasUnbreakableWeapons()) {
            for (ItemStack stack : contents) {
                if (stack != null && arenaClass.isWeapon(stack)) {
                    stack.setDurability(Short.MIN_VALUE);
                }
            }
        }

        // Set contents, THEN set armor contents
        inv.setContents(contents);
        inv.setHelmet(helmet);
        inv.setChestplate(chestplate);
        inv.setLeggings(leggings);
        inv.setBoots(boots);
        inv.setItemInOffHand(offhand);

        PermissionAttachment pa = arenaClass.grantLobbyPermissions(plugin, p);
        replacePermissions(p, pa);

        autoReady(p);
    }

    private void replacePermissions(Player p, PermissionAttachment rep) {
        PermissionAttachment old = attachments.get(p);
        if (old != null) {
            old.remove();
            p.recalculatePermissions();
        }
        if (rep != null) {
            attachments.put(p, rep);
            p.recalculatePermissions();
        }
    }
    
    private void autoReady(Player p) {
        if (settings.getBoolean("auto-ready", false)) {
            if (autoStartTimer.getRemaining() <= 0) {
                playerReady(p);
            } else {
                readyPlayers.add(p);
            }
        }
    }
    
    @Override
    public void addRandomPlayer(Player p) {
        randoms.add(p);
    }

    @Override
    public void assignRandomClass(Player p)
    {
        List<ArenaClass> classes = this.classes.values().stream()
            .filter(c -> c.hasPermission(p))
            .collect(Collectors.toList());

        if (classes.isEmpty()) {
            plugin.getLogger().info("Player '" + p.getName() + "' has no class permissions!");
            playerLeave(p);
            return;
        }
        
        int index = MobArena.random.nextInt(classes.size());
        String className = classes.get(index).getConfigName();

        assignClass(p, className);
        messenger.tell(p, Msg.LOBBY_CLASS_PICKED, this.classes.get(className).getConfigName());
    }

    @Override
    public void assignClassPermissions(Player p)
    {
        PermissionAttachment pa = arenaPlayerMap.get(p).getArenaClass().grantPermissions(plugin, p);
        replacePermissions(p, pa);
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

                plugin.getLogger().warning("[PERM01] Failed to remove permission attachment '" + perm + "' from player '" + name
                                  + "'.\nThis should not be a big issue, but please verify that the player doesn't have any permissions they shouldn't have.");
            }
        }
        p.recalculatePermissions();
    }

    @Override
    public void addPermission(Player p, String perm, boolean value) {
        PermissionAttachment pa = attachments.get(p);
        if (pa == null) {
            pa = p.addAttachment(plugin);
            attachments.put(p, pa);
        }
        pa.setPermission(perm, value);
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
            b.setType(Material.AIR);
        }
        blocks.clear();
    }
    
    private void removeEntities() {
        List<Chunk> chunks = region.getChunks();
        
        for (Chunk c : chunks) {
            for (Entity e : c.getEntities()) {
                if (e == null) {
                    continue;
                }

                switch (e.getType()) {
                    case DROPPED_ITEM:
                    case EXPERIENCE_ORB:
                    case ARROW:
                    case MINECART:
                    case BOAT:
                    case SHULKER_BULLET:
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
        List<Player> result = new LinkedList<>();
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

    @Override
    public AutoStartTimer getAutoStartTimer() {
        return autoStartTimer;
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
        List<Player> result = new LinkedList<>();
        result.addAll(lobbyPlayers);
        result.removeAll(readyPlayers);
        return result;
    }

    @Override
    public boolean canAfford(Player p) {
        for (Thing fee : entryFee) {
            if (!fee.heldBy(p)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean takeFee(Player p) {
        if (entryFee.isEmpty()) return true;

        for (Thing fee : entryFee) {
            fee.takeFrom(p);
        }

        messenger.tell(p, Msg.JOIN_FEE_PAID.format(MAUtils.listToString(entryFee, plugin)));
        return true;
    }
    
    @Override
    public boolean refund(Player p) {
        if (entryFee.isEmpty()) return true;
        if (!inLobby(p)) return false;

        for (Thing fee : entryFee) {
            fee.giveTo(p);
        }
        return true;
    }

    @Override
    public boolean canJoin(Player p) {
        if (!enabled)
            messenger.tell(p, Msg.JOIN_ARENA_NOT_ENABLED);
        else if (!region.isSetup() || waveManager.getRecurrentWaves().isEmpty())
            messenger.tell(p, Msg.JOIN_ARENA_NOT_SETUP);
        else if (edit)
            messenger.tell(p, Msg.JOIN_ARENA_EDIT_MODE);
        else if (arenaPlayers.contains(p) || lobbyPlayers.contains(p))
            messenger.tell(p, Msg.JOIN_ALREADY_PLAYING);
        else if (running)
            messenger.tell(p, Msg.JOIN_ARENA_IS_RUNNING);
        else if (!hasPermission(p))
            messenger.tell(p, Msg.JOIN_ARENA_PERMISSION);
        else if (getMaxPlayers() > 0 && lobbyPlayers.size() >= getMaxPlayers())
            messenger.tell(p, Msg.JOIN_PLAYER_LIMIT_REACHED);
        else if (getJoinDistance() > 0 && !region.contains(p.getLocation(), getJoinDistance()))
            messenger.tell(p, Msg.JOIN_TOO_FAR);
        else if (settings.getBoolean("require-empty-inv-join", true) && !InventoryManager.hasEmptyInventory(p))
            messenger.tell(p, Msg.JOIN_EMPTY_INV);
        else if (!canAfford(p))
            messenger.tell(p, Msg.JOIN_FEE_REQUIRED, MAUtils.listToString(entryFee, plugin));
        else return true;
        
        return false;
    }

    @Override
    public boolean canSpec(Player p) {
        if (!enabled)
            messenger.tell(p, Msg.JOIN_ARENA_NOT_ENABLED);
        else if (!region.isSetup())
            messenger.tell(p, Msg.JOIN_ARENA_NOT_SETUP);
        else if (edit)
            messenger.tell(p, Msg.JOIN_ARENA_EDIT_MODE);
        else if (arenaPlayers.contains(p) || lobbyPlayers.contains(p))
            messenger.tell(p, Msg.SPEC_ALREADY_PLAYING);
        else if (settings.getBoolean("require-empty-inv-spec", true) && !InventoryManager.hasEmptyInventory(p))
            messenger.tell(p, Msg.SPEC_EMPTY_INV);
        else if (getJoinDistance() > 0 && !region.contains(p.getLocation(), getJoinDistance()))
            messenger.tell(p, Msg.JOIN_TOO_FAR);
        else return true;
        
        return false;
    }

    @Override
    public boolean hasIsolatedChat() {
        return isolatedChat;
    }

    @Override
    public Player getLastPlayerStanding() {
        return lastStanding;
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
