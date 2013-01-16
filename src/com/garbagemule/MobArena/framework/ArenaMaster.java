package com.garbagemule.MobArena.framework;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import com.garbagemule.MobArena.ArenaClass;
import com.garbagemule.MobArena.MobArena;
import com.garbagemule.MobArena.framework.Arena;

public interface ArenaMaster
{
    /*/////////////////////////////////////////////////////////////////////////
    //
    //      NEW METHODS IN REFACTORING
    //
    /////////////////////////////////////////////////////////////////////////*/
    
    
    public MobArena getPlugin();
    
    public boolean isEnabled();
    
    public void setEnabled(boolean value);
    
    public boolean notifyOnUpdates();

    public Arena getSelectedArena();

    public void setSelectedArena(Arena arena);
    
    public List<Arena> getArenas();
    
    public Map<String,ArenaClass> getClasses();
    
    public void addPlayer(Player p, Arena arena);
    
    public Arena removePlayer(Player p);
    
    public void resetArenaMap();
    
    
    
    /*/////////////////////////////////////////////////////////////////////////
    //
    //      Getters
    //
    /////////////////////////////////////////////////////////////////////////*/
    

    public List<Arena> getEnabledArenas();
    
    public List<Arena> getEnabledArenas(List<Arena> arenas);
    
    public List<Arena> getPermittedArenas(Player p);
    
    public List<Arena> getEnabledAndPermittedArenas(Player p);
    
    public Arena getArenaAtLocation(Location loc);
    
    public List<Arena> getArenasInWorld(World world);
    
    public List<Player> getAllPlayers();
    
    public List<Player> getAllPlayersInArena(String arenaName);
    
    public List<Player> getAllLivingPlayers();
    
    public List<Player> getLivingPlayersInArena(String arenaName);
    
    public Arena getArenaWithPlayer(Player p);
    
    public Arena getArenaWithPlayer(String playerName);
    
    public Arena getArenaWithSpectator(Player p);
    
    public Arena getArenaWithMonster(Entity e);
    
    public Arena getArenaWithPet(Entity e);
    
    public Arena getArenaWithName(String configName);
    
    public Arena getArenaWithName(Collection<Arena> arenas, String configName);
    
    public boolean isAllowed(String command);
    
    
    
    /*/////////////////////////////////////////////////////////////////////////
    //
    //      Initialization
    //
    /////////////////////////////////////////////////////////////////////////*/
    
    public void initialize();

    /**
     * Load the global settings.
     */
    public void loadSettings();
    
    /**
     * Load all class-related stuff.
     */
    public void loadClasses();
    
    public ArenaClass createClassNode(String className, PlayerInventory inv, boolean safe);
    
    public void removeClassNode(String className);
    
    public boolean addClassPermission(String className, String perm);
    
    public boolean removeClassPermission(String className, String perm);
    
    /**
     * Load all arena-related stuff.
     */
    public void loadArenas();
    
    public void loadArenasInWorld(String worldName);
    
    public void unloadArenasInWorld(String worldName);
    
    public Arena createArenaNode(String configName, World world);
    
    public void removeArenaNode(Arena arena);
    
    
    
    /*/////////////////////////////////////////////////////////////////////////
    //
    //      Update and serialization methods
    //
    /////////////////////////////////////////////////////////////////////////*/
    
    public void reloadConfig();
    
    public void saveConfig();
}
