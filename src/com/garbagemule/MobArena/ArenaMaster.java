package com.garbagemule.MobArena;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
//import org.bukkit.util.config.Configuration;

//import com.garbagemule.ArenaPlugin.Master;

public abstract class ArenaMaster //implements Master
{
    protected Arena selectedArena;
    
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
    
    
    
    /*/////////////////////////////////////////////////////////////////////////
    //
    //      Arena getters
    //
    /////////////////////////////////////////////////////////////////////////*/
    

    public abstract List<Arena> getEnabledArenas();
    
    public abstract List<Arena> getPermittedArenas(Player p);
    
    public abstract Arena getArenaAtLocation(Location loc);
    
    public abstract List<Arena> getArenasInWorld(World world);
    
    public abstract List<Player> getAllPlayers();
    
    public abstract List<Player> getAllPlayersInArena(String arenaName);
    
    public abstract List<Player> getAllLivingPlayers();
    
    public abstract List<Player> getLivingPlayersInArena(String arenaName);
    
    public abstract Arena getArenaWithPlayer(Player p);
    
    public abstract Arena getArenaWithPlayer(String playerName);
    
    public abstract Arena getArenaWithSpectator(Player p);
    
    public abstract Arena getArenaWithMonster(Entity e);
    
    public abstract Arena getArenaWithPet(Entity e);
    
    public abstract Arena getArenaWithName(String configName);
    
    
    
    /*/////////////////////////////////////////////////////////////////////////
    //
    //      Initialization
    //
    /////////////////////////////////////////////////////////////////////////*/
    
    public abstract void initialize();

    /**
     * Load the global settings.
     */
    public abstract void loadSettings();
    
    /**
     * Load all class-related stuff.
     */
    public abstract void loadClasses();
    
    /**
     * Load all arena-related stuff.
     */
    public abstract void loadArenas();
    
    public abstract Arena createArenaNode(String configName, World world);
    
    public abstract void removeArenaNode(String configName);
    
    
    
    /*/////////////////////////////////////////////////////////////////////////
    //
    //      Update and serialization methods
    //
    /////////////////////////////////////////////////////////////////////////*/
    
    /**
     * Update one, two or all three of global settings, classes
     * and arenas (arenas with deserialization).
     */
    public abstract void update(boolean settings, boolean classes, boolean arenalist);
    
    /**
     * Serialize the global settings.
     */
    public abstract void serializeSettings();
    
    /**
     * Serialize all arena configs.
     */
    public abstract void serializeArenas();
    
    /**
     * Deserialize all arena configs. Updates the arena list to
     * include only the current arenas (not ones added in the
     * actual file) that are also in the config-file.
     */
    public abstract void deserializeArenas();
    
    public abstract void updateSettings();
    public abstract void updateClasses();
    public abstract void updateArenas();
    public abstract void updateAll();
}
