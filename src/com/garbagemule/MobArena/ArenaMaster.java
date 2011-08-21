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

//import com.garbagemule.ArenaPlugin.Master;

public class ArenaMaster //implements Master
{
    private MobArena plugin;
    private Configuration config;
    protected Arena selectedArena;
    //protected Lobby masterLobby;
    
    // Settings
    protected boolean enabled, updateNotify;
    
    // Classes
    protected List<String> classes;
    protected Map<String,List<ItemStack>>  classItems, classArmor;
    protected Map<Integer,Map<Player,List<ItemStack>>> classBonuses;
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
        plugin = instance;
        config = plugin.getConfig();
        arenas = new LinkedList<Arena>();
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
        if (config.getKeys("classes") == null)
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
        }
        classes      = config.getKeys("classes");
        classItems   = MAUtils.getClassItems(config, "items");
        classArmor   = MAUtils.getClassItems(config, "armor");
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
        
        arenaMap = new HashMap<Player,Arena>();
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
