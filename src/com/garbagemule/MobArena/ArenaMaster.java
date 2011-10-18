package com.garbagemule.MobArena;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.config.Configuration;

import com.garbagemule.MobArena.MAMessages.Msg;

//import com.garbagemule.ArenaPlugin.Master;

public class ArenaMaster //implements Master
{
    private MobArena plugin;
    private Configuration config;
    protected Arena selectedArena;
    
    // Settings
    protected boolean enabled, updateNotify;
    
    // Classes
    protected List<String> classes;
    protected Map<String,List<ItemStack>>  classItems, classArmor;
    protected Map<String,Map<String,Boolean>> classPerms;
    //protected Map<Integer,Map<Player,List<ItemStack>>> classBonuses;
    protected Map<Player,Arena> arenaMap;
    
    // Location map
    protected Map<Player,Location> locations = new HashMap<Player,Location>();
    
    // Arena list
    protected List<Arena> arenas;
    
    // Listeners
    protected Set<MobArenaListener> listeners = new HashSet<MobArenaListener>();
    
    
    
    /**
     * Default constructor.
     */
    public ArenaMaster(MobArena instance)
    {
        plugin   = instance;
        config   = plugin.getMAConfig();
        arenas   = new LinkedList<Arena>();
        arenaMap = new HashMap<Player,Arena>();
    }
    
    
    
    /*/////////////////////////////////////////////////////////////////////////
    //
    //      Arena getters
    //
    /////////////////////////////////////////////////////////////////////////*/
    
    public List<Arena> getEnabledArenas()
    {
        List<Arena> result = new LinkedList<Arena>();
        for (Arena arena : arenas)
            if (arena.enabled)
                result.add(arena);
        return result;
    }
    
    public List<Arena> getPermittedArenas(Player p)
    {
        List<Arena> result = new LinkedList<Arena>();
        for (Arena arena : arenas)
            if (plugin.has(p, "mobarena.arenas." + arena.configName()))
                result.add(arena);
        return result;
    }
    
    public Arena getArenaAtLocation(Location loc)
    {
        for (Arena arena : arenas)
            if (arena.inRegion(loc))
                return arena;
        return null;
    }
    
    public List<Arena> getArenasInWorld(World world)
    {
        List<Arena> result = new LinkedList<Arena>();
        for (Arena arena : arenas)
            if (arena.world.equals(world))
                result.add(arena);
        return result;
    }
    
    public List<Player> getAllPlayers()
    {
        List<Player> result = new LinkedList<Player>();
        for (Arena arena : arenas)
            result.addAll(arena.getAllPlayers());
        return result;
    }
    
    public List<Player> getAllPlayersInArena(String arenaName)
    {
        Arena arena = getArenaWithName(arenaName);
        return (arena != null) ? arena.getLivingPlayers() : new LinkedList<Player>();
    }
    
    public List<Player> getAllLivingPlayers()
    {
        List<Player> result = new LinkedList<Player>();
        for (Arena arena : arenas)
            result.addAll(arena.getLivingPlayers());
        return result;
    }
    
    public List<Player> getLivingPlayersInArena(String arenaName)
    {
        Arena arena = getArenaWithName(arenaName);
        return (arena != null) ? arena.getLivingPlayers() : new LinkedList<Player>();
    }
    
    public Arena getArenaWithPlayer(Player p)
    {
        return arenaMap.get(p);
    }
    
    public Arena getArenaWithPlayer(String playerName)
    {
        return arenaMap.get(Bukkit.getServer().getPlayer(playerName));
    }
    
    public Arena getArenaWithSpectator(Player p)
    {
        for (Arena arena : arenas)
        {
            if (arena.specPlayers.contains(p))
                return arena;
        }
        return null;
    }
    
    public Arena getArenaWithMonster(Entity e)
    {
        for (Arena arena : arenas)
            if (arena.monsters.contains(e))
                return arena;
        return null;
    }
    
    public Arena getArenaWithPet(Entity e)
    {
        for (Arena arena : arenas)
            if (arena.pets.contains(e))
                return arena;
        return null;
    }
    
    public Arena getArenaWithName(String configName)
    {
        for (Arena arena : arenas)
            if (arena.configName().equals(configName))
                return arena;
        return null;
    }
    
    
    
    /*/////////////////////////////////////////////////////////////////////////
    //
    //      Initialization
    //
    /////////////////////////////////////////////////////////////////////////*/
    
    public void initialize()
    {
        config.load();
        loadSettings();
        loadClasses();
        loadArenas();
        config.save();
    }

    /**
     * Load the global settings.
     */
    public void loadSettings()
    {
        if (config.getKeys("global-settings") == null)
        {
            config.setProperty("global-settings.enabled", true);
            config.setProperty("global-settings.update-notification", true);
        }
        
        enabled      = config.getBoolean("global-settings.enabled", true);
        updateNotify = config.getBoolean("global-settings.update-notification", true);
    }
    
    /**
     * Load all class-related stuff.
     */
    public void loadClasses()
    {
        classes = config.getKeys("classes");
        if (classes == null)
        {
            config.setProperty("classes.Archer.items", "wood_sword, bow, arrow:128, grilled_pork");
            config.setProperty("classes.Archer.armor", "298,299,300,301");
            config.setProperty("classes.Knight.items", "diamond_sword, grilled_pork:2");
            config.setProperty("classes.Knight.armor", "306,307,308,309");
            config.setProperty("classes.Tank.items",   "iron_sword, grilled_pork:3, apple");
            config.setProperty("classes.Tank.armor",   "310,311,312,313");
            config.setProperty("classes.Oddjob.items", "stone_sword, flint_and_steel, netherrack:2, wood_pickaxe, tnt:4, fishing_rod, apple, grilled_pork:3");
            config.setProperty("classes.Oddjob.armor", "298,299,300,301");
            config.setProperty("classes.Chef.items",   "stone_sword, bread:6, grilled_pork:4, mushroom_soup, cake:3, cookie:12");
            config.setProperty("classes.Chef.armor",   "314,315,316,317");
            classes = config.getKeys("classes");
        }
        classItems = MAUtils.getClassItems(config, "items");
        classArmor = MAUtils.getClassItems(config, "armor");
        classPerms = MAUtils.getClassPerms(config);
    }
    
    /**
     * Load all arena-related stuff.
     */
    public void loadArenas()
    {
        arenas = new LinkedList<Arena>();
        
        if (config.getKeys("arenas") == null)
            createArenaNode("default", Bukkit.getServer().getWorlds().get(0));

        for (String configName : config.getKeys("arenas"))
        {
            String arenaPath = "arenas." + configName + ".";
            String worldName = config.getString(arenaPath + "settings.world", null);
            World  world;
            if (worldName == null || worldName.equals(""))
            {
                MobArena.warning("Could not find the world for arena '" + configName + "'. Using default world! Check the config-file!");
                world = Bukkit.getServer().getWorlds().get(0);
            }
            else world = Bukkit.getServer().getWorld(worldName);
            
            Arena arena = new Arena(MAUtils.nameConfigToArena(configName), world);
            arena.load(config);
            arenas.add(arena);
        }
        
        selectedArena = arenas.get(0);
    }
    
    public Arena createArenaNode(String configName, World world)
    {
        config.setProperty("arenas." + configName + ".settings.world", world.getName());
        config.save();
        config.load();
        config.setProperty("arenas." + configName + ".settings.enabled", true);
        config.save();
        config.load();
        config.setProperty("arenas." + configName + ".settings.protect", true);
        config.save();
        config.load();
        config.setProperty("arenas." + configName + ".settings.entry-fee", "");
        config.save();
        config.load();
        config.setProperty("arenas." + configName + ".settings.logging", false);
        config.setProperty("arenas." + configName + ".settings.clear-wave-before-next", false);
        config.setProperty("arenas." + configName + ".settings.detonate-creepers", false);
        config.setProperty("arenas." + configName + ".settings.detonate-damage", false);
        config.setProperty("arenas." + configName + ".settings.lightning", true);
        config.setProperty("arenas." + configName + ".settings.auto-equip-armor", true);
        config.setProperty("arenas." + configName + ".settings.force-restore", false);
        config.setProperty("arenas." + configName + ".settings.soft-restore", false);
        config.setProperty("arenas." + configName + ".settings.soft-restore-drops", false);
        config.setProperty("arenas." + configName + ".settings.require-empty-inv-join", true);
        config.setProperty("arenas." + configName + ".settings.require-empty-inv-spec", true);
        config.setProperty("arenas." + configName + ".settings.hellhounds", false);
        config.setProperty("arenas." + configName + ".settings.pvp-enabled", false);
        config.setProperty("arenas." + configName + ".settings.monster-infight", false);
        config.setProperty("arenas." + configName + ".settings.allow-teleporting", false);
        config.setProperty("arenas." + configName + ".settings.spectate-on-death", true);
        config.setProperty("arenas." + configName + ".settings.share-items-in-arena", true);
        config.save();
        config.load();
        config.setProperty("arenas." + configName + ".settings.player-limit", 0);
        config.setProperty("arenas." + configName + ".settings.max-join-distance", 0);
        config.save();
        config.load();
        config.setProperty("arenas." + configName + ".settings.repair-delay", 5);
        config.setProperty("arenas." + configName + ".settings.first-wave-delay", 5);
        config.setProperty("arenas." + configName + ".settings.wave-interval", 20);
        config.setProperty("arenas." + configName + ".settings.special-modulo", 4);
        config.setProperty("arenas." + configName + ".settings.max-idle-time", 0);
        config.save();
        config.load();

        Arena arena = new Arena(MAUtils.nameConfigToArena(configName), world);
        arena.load(config);
        return arena;
    }
    
    public void removeArenaNode(String configName)
    {
        config.removeProperty("arenas." + configName);
        config.save();
    }
    
    
    
    /*/////////////////////////////////////////////////////////////////////////
    //
    //      Manipulation
    //
    /////////////////////////////////////////////////////////////////////////*/
    
    public boolean joinArena(Player p, String arenaName) {
        List<Arena> arenas = getEnabledArenas();
        if (!enabled || arenas.size() < 1)
        {
            MAUtils.tellPlayer(p, Msg.JOIN_NOT_ENABLED);
            return true;
        }
        
        // Grab the arena to join
        Arena arena = arenas.size() == 1 ? arenas.get(0) : getArenaWithName(arenaName);
        
        // Run a couple of basic sanity checks
        if (!sanityChecks(p, arena, arenaName, arenas))
            return true;
        
        // Run a bunch of per-arena sanity checks
        if (!arena.canJoin(p))
            return true;
        
        // If player is in a boat/minecart, eject!
        if (p.isInsideVehicle())
            p.leaveVehicle();
        
        // Take entry fee and store inventory
        arena.takeFee(p);
        if (!arena.emptyInvJoin) MAUtils.storeInventory(p);
        
        // If player is in a bed, unbed!
        if (p.isSleeping())
        {
            p.kickPlayer("Banned for life... Nah, just don't join from a bed ;)");
            return true;
        }
        
        // Join the arena!
        arena.playerJoin(p, p.getLocation());
        
        MAUtils.tellPlayer(p, Msg.JOIN_PLAYER_JOINED);
        if (!arena.entryFee.isEmpty())
            MAUtils.tellPlayer(p, Msg.JOIN_FEE_PAID.get(MAUtils.listToString(arena.entryFee, plugin)));
        if (arena.hasPaid.contains(p))
            arena.hasPaid.remove(p);
        
        return true;
    }
    
    public boolean leaveArena(Player p) {
        if (!arenaMap.containsKey(p))
        {
            Arena arena = getArenaWithSpectator(p);
            if (arena != null)
            {            
                arena.playerLeave(p);
                MAUtils.tellPlayer(p, Msg.LEAVE_PLAYER_LEFT);
                return true;
            }
            
            MAUtils.tellPlayer(p, Msg.LEAVE_NOT_PLAYING);
            return true;
        }
        
        Arena arena = arenaMap.get(p);            
        arena.playerLeave(p);
        MAUtils.tellPlayer(p, Msg.LEAVE_PLAYER_LEFT);
        return true;
    }
    
    public boolean spectateArena(Player p, String arenaName) {
        List<Arena> arenas = getEnabledArenas();
        if (!enabled || arenas.size() < 1)
        {
            MAUtils.tellPlayer(p, Msg.JOIN_NOT_ENABLED);
            return true;
        }

        // Grab the arena to join
        Arena arena = arenas.size() == 1 ? arenas.get(0) : getArenaWithName(arenaName);

        // Run a couple of basic sanity checks
        if (!sanityChecks(p, arena, arenaName, arenas))
            return true;

        // Run a bunch of arena-specific sanity-checks
        if (!arena.canSpec(p))
            return true;
        
        // If player is in a boat/minecart, eject!
        if (p.isInsideVehicle())
            p.leaveVehicle();
        
        // If player is in a bed, unbed!
        if (p.isSleeping())
        {
            p.kickPlayer("Banned for life... Nah, just don't join from a bed ;)");
            return true;
        }
        
        // Spectate the arena!
        arena.playerSpec(p, p.getLocation());
        
        MAUtils.tellPlayer(p, Msg.SPEC_PLAYER_SPECTATE);
        return true;
    }

    
    private boolean sanityChecks(Player p, Arena arena, String arenaName, List<Arena> arenas)
    {
        if (arenas.size() > 1 && arenaName.isEmpty())
            MAUtils.tellPlayer(p, Msg.JOIN_ARG_NEEDED);
        else if (arena == null)
            MAUtils.tellPlayer(p, Msg.ARENA_DOES_NOT_EXIST);
        else if (arenaMap.containsKey(p) && (arenaMap.get(p).arenaPlayers.contains(p) || arenaMap.get(p).lobbyPlayers.contains(p)))
            MAUtils.tellPlayer(p, Msg.JOIN_IN_OTHER_ARENA);
        else
            return true;
        
        return false;
    }
    
    
    
    /*/////////////////////////////////////////////////////////////////////////
    //
    //      Update and serialization methods
    //
    /////////////////////////////////////////////////////////////////////////*/
    
    /**
     * Update one, two or all three of global settings, classes
     * and arenas (arenas with deserialization).
     */
    public void update(boolean settings, boolean classes, boolean arenalist)
    {
        boolean tmp = enabled;
        enabled = false;
        
        for (Arena arena : arenas)
            arena.forceEnd();
        
        config.load();
        if (settings)  loadSettings();
        if (classes)   loadClasses();
        if (arenalist) deserializeArenas();
        config.save();
        
        enabled = tmp;
    }
    
    /**
     * Serialize the global settings.
     */
    public void serializeSettings()
    {        
        String settings = "global-settings.";
        config.setProperty(settings + "enabled", enabled);
        config.save();
    }
    
    /**
     * Serialize all arena configs.
     */
    public void serializeArenas()
    {
        for (Arena arena : arenas)
            arena.serializeConfig();
    }
    
    /**
     * Deserialize all arena configs. Updates the arena list to
     * include only the current arenas (not ones added in the
     * actual file) that are also in the config-file.
     */
    public void deserializeArenas()
    {
        // Get only the arenas in the config.
        List<String> strings = config.getKeys("arenas");
        if (strings == null)
            return;
        
        // Get their Arena objects.
        List<Arena> configArenas = new LinkedList<Arena>();
        for (String s : strings)
            if (getArenaWithName(s) != null)
                configArenas.add(getArenaWithName(s));
        
        // Remove all Arenas no longer in the config.
        arenas.retainAll(configArenas);
        
        for (Arena arena : arenas)
            arena.deserializeConfig();
        
        // Make sure to update the selected arena to a valid one.
        if (!arenas.contains(selectedArena) && arenas.size() >= 1)
            selectedArena = arenas.get(0);
    }
    
    public void updateSettings() { update(true,  false, false); }
    public void updateClasses()  { update(false, true,  false); }
    public void updateArenas()   { update(false, false, true);  }
    public void updateAll()      { update(true,  true,  true);  }
}
