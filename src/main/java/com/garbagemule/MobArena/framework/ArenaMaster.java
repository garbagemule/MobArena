package com.garbagemule.MobArena.framework;

import com.garbagemule.MobArena.ArenaClass;
import com.garbagemule.MobArena.Messenger;
import com.garbagemule.MobArena.MobArena;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ArenaMaster
{
    /*/////////////////////////////////////////////////////////////////////////
    //
    //      NEW METHODS IN REFACTORING
    //
    /////////////////////////////////////////////////////////////////////////*/
    
    
    MobArena getPlugin();

    Messenger getGlobalMessenger();
    
    boolean isEnabled();
    
    void setEnabled(boolean value);
    
    boolean notifyOnUpdates();
    
    List<Arena> getArenas();
    
    Map<String,ArenaClass> getClasses();
    
    void addPlayer(Player p, Arena arena);
    
    Arena removePlayer(Player p);
    
    void resetArenaMap();
    
    
    
    /*/////////////////////////////////////////////////////////////////////////
    //
    //      Getters
    //
    /////////////////////////////////////////////////////////////////////////*/
    

    List<Arena> getEnabledArenas();
    
    List<Arena> getEnabledArenas(List<Arena> arenas);
    
    List<Arena> getPermittedArenas(Player p);
    
    List<Arena> getEnabledAndPermittedArenas(Player p);
    
    Arena getArenaAtLocation(Location loc);
    
    List<Arena> getArenasInWorld(World world);
    
    List<Player> getAllPlayers();
    
    List<Player> getAllPlayersInArena(String arenaName);
    
    List<Player> getAllLivingPlayers();
    
    List<Player> getLivingPlayersInArena(String arenaName);
    
    Arena getArenaWithPlayer(Player p);
    
    Arena getArenaWithPlayer(String playerName);
    
    Arena getArenaWithSpectator(Player p);
    
    Arena getArenaWithMonster(Entity e);
    
    Arena getArenaWithPet(Entity e);
    
    Arena getArenaWithName(String configName);
    
    Arena getArenaWithName(Collection<Arena> arenas, String configName);
    
    boolean isAllowed(String command);
    
    
    
    /*/////////////////////////////////////////////////////////////////////////
    //
    //      Initialization
    //
    /////////////////////////////////////////////////////////////////////////*/
    
    void initialize();

    /**
     * Load the global settings.
     */
    void loadSettings();
    
    /**
     * Load all class-related stuff.
     */
    void loadClasses();
    
    /**
     * Load all arena-related stuff.
     */
    void loadArenas();
    
    void loadArenasInWorld(String worldName);
    
    void unloadArenasInWorld(String worldName);

    boolean reloadArena(String name);
    
    Arena createArenaNode(String configName, World world);
    
    void removeArenaNode(Arena arena);
    
    
    
    /*/////////////////////////////////////////////////////////////////////////
    //
    //      Update and serialization methods
    //
    /////////////////////////////////////////////////////////////////////////*/
    
    void reloadConfig();
    
    void saveConfig();
}
