package com.prosicraft.MobArena;

import com.prosicraft.mighty.logger.MLog;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

//import com.garbagemule.ArenaPlugin.Master;

public class ArenaMaster //implements Master
{
    private MobArena plugin;
    private FileConfiguration config;
    private File configfile;
    protected Arena selectedArena;
    //protected Lobby masterLobby;
    
    // Settings
    protected boolean enabled, updateNotify;
    
    // Classes
    protected Set<String> classes;
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
        plugin      = instance;
        config      = plugin.getConfig();
        configfile  = plugin.getConfigFile();
        arenas      = new LinkedList<Arena>();
        arenaMap    = new HashMap<Player,Arena>();
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
        try {
            config.load(configfile);
        } catch (IOException iex) {
            MLog.e("Can't load configuration file at " + ((configfile != null) ? configfile.getAbsolutePath() : "not given configuration file!"));            
        } catch (InvalidConfigurationException icex) {
            MLog.e("Loaded invalid configuration file at " + ((configfile != null) ? configfile.getAbsolutePath() : "not given configuration file!"));
        }
        
        loadSettings();
        loadClasses();
        loadArenas();
        MAUtils.saveFileConfiguration(config, configfile);
    }

    /**
     * Load the global settings.
     */
    public void loadSettings()
    {
        if (config.getConfigurationSection("global-settings") == null)
        {
            config.set("global-settings.enabled", true);
            config.set("global-settings.update-notification", true);
        }
        
        enabled      = config.getBoolean("global-settings.enabled", true);
        updateNotify = config.getBoolean("global-settings.update-notification", true);
    }
    
    /**
     * Load all class-related stuff.
     */
    public void loadClasses()
    {
        classes = MAUtils.getKeys(config, "classes");
        if (classes == null)
        {
            config.set("classes.Archer.items", "wood_sword, bow, arrow:128, grilled_pork");
            config.set("classes.Archer.armor", "298,299,300,301");
            config.set("classes.Knight.items", "diamond_sword, grilled_pork:2");
            config.set("classes.Knight.armor", "306,307,308,309");
            config.set("classes.Tank.items",   "iron_sword, grilled_pork:3, apple");
            config.set("classes.Tank.armor",   "310,311,312,313");
            config.set("classes.Oddjob.items", "stone_sword, flint_and_steel, netherrack:2, wood_pickaxe, tnt:4, fishing_rod, apple, grilled_pork:3");
            config.set("classes.Oddjob.armor", "298,299,300,301");
            config.set("classes.Chef.items",   "stone_sword, bread:6, grilled_pork:4, mushroom_soup, cake:3, cookie:12");
            config.set("classes.Chef.armor",   "314,315,316,317");
            classes = MAUtils.getKeys(config, "classes");
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
        
        if (config.getConfigurationSection("arenas") == null)
            createArenaNode("default", Bukkit.getServer().getWorlds().get(0));

        try {
            for (String configName : MAUtils.getKeys(config, "arenas"))
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
                    arena.load(config, configfile);            
                arenas.add(arena);
                selectedArena = arenas.get(0);
            }
        } catch (NullPointerException nex) {           
        }                
    }
    
    public Arena createArenaNode(String configName, World world)
    {
        config.set("arenas." + configName + ".settings.world", world.getName());
        MAUtils.saveFileConfiguration(config, configfile);
        MAUtils.loadFileConfiguration(config, configfile);
        config.set("arenas." + configName + ".settings.enabled", true);
        MAUtils.saveFileConfiguration(config, configfile);
        MAUtils.loadFileConfiguration(config, configfile);
        config.set("arenas." + configName + ".settings.protect", true);
        MAUtils.saveFileConfiguration(config, configfile);
        MAUtils.loadFileConfiguration(config, configfile);
        config.set("arenas." + configName + ".settings.entry-fee", "");
        MAUtils.saveFileConfiguration(config, configfile);
        MAUtils.loadFileConfiguration(config, configfile);
        config.set("arenas." + configName + ".settings.logging", false);
        config.set("arenas." + configName + ".settings.clear-wave-before-next", false);
        config.set("arenas." + configName + ".settings.detonate-creepers", false);
        config.set("arenas." + configName + ".settings.detonate-damage", false);
        config.set("arenas." + configName + ".settings.lightning", true);
        config.set("arenas." + configName + ".settings.auto-equip-armor", true);
        config.set("arenas." + configName + ".settings.force-restore", false);
        config.set("arenas." + configName + ".settings.soft-restore", false);
        config.set("arenas." + configName + ".settings.soft-restore-drops", false);
        config.set("arenas." + configName + ".settings.require-empty-inv-join", true);
        config.set("arenas." + configName + ".settings.require-empty-inv-spec", true);
        config.set("arenas." + configName + ".settings.hellhounds", false);
        config.set("arenas." + configName + ".settings.pvp-enabled", false);
        config.set("arenas." + configName + ".settings.monster-infight", false);
        config.set("arenas." + configName + ".settings.allow-teleporting", false);
        config.set("arenas." + configName + ".settings.spectate-on-death", true);
        config.set("arenas." + configName + ".settings.share-items-in-arena", true);
        MAUtils.saveFileConfiguration(config, configfile);
        MAUtils.loadFileConfiguration(config, configfile);
        config.set("arenas." + configName + ".settings.player-limit", 0);
        config.set("arenas." + configName + ".settings.max-join-distance", 0);
        MAUtils.saveFileConfiguration(config, configfile);
        MAUtils.loadFileConfiguration(config, configfile);
        config.set("arenas." + configName + ".settings.repair-delay", 5);
        config.set("arenas." + configName + ".settings.first-wave-delay", 5);
        config.set("arenas." + configName + ".settings.wave-interval", 20);
        config.set("arenas." + configName + ".settings.special-modulo", 4);
        config.set("arenas." + configName + ".settings.max-idle-time", 0);
        MAUtils.saveFileConfiguration(config, configfile);
        MAUtils.loadFileConfiguration(config, configfile);

        Arena arena = new Arena(MAUtils.nameConfigToArena(configName), world);
        arena.load(config, configfile);
        return arena;
    }
    
    public void removeArenaNode(String configName)
    {
        config.set("arenas." + configName, null);
        MAUtils.saveFileConfiguration(config, configfile);
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
        
        MAUtils.loadFileConfiguration(config, configfile);
        if (settings)  loadSettings();
        if (classes)   loadClasses();
        if (arenalist) deserializeArenas();
        MAUtils.loadFileConfiguration(config, configfile);
        
        enabled = tmp;
    }
    
    /**
     * Serialize the global settings.
     */
    public void serializeSettings()
    {        
        String settings = "global-settings.";
        config.set(settings + "enabled", enabled);
        MAUtils.saveFileConfiguration(config, configfile);
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
        Set<String> strings;
        try {
            strings = MAUtils.getKeys(config, "arenas");
        } catch (NullPointerException nex) {
            return;
        }
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
