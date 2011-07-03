package com.garbagemule.MobArena;

import java.util.List;
import java.util.LinkedList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class MobArenaHandler
{
    MobArena plugin;
    
    public MobArenaHandler()
    {
        plugin = (MobArena) Bukkit.getServer().getPluginManager().getPlugin("MobArena");
    }

    // Check if there is an active arena session running.
    public boolean isRunning(String arenaName)
    {
        Arena arena = plugin.getAM().getArenaWithName(arenaName);
        if (arena == null)
            throw new NullPointerException("Arena with name '" + arenaName + "' does not exist!");
        
        return arena.running;
    }
    
    // Check if the specified player is in an arena.
    public boolean isPlaying(Player p)               { return (plugin.getAM().getArenaWithPlayer(p) != null); }
    
    // Arena getters
    public Arena getArenaWithName(String arenaName)  { return plugin.getAM().getArenaWithName(arenaName); }
    public Arena getArenaWithPlayer(Player p)        { return plugin.getAM().getArenaWithPlayer(p); }
    public Arena getArenaWithPlayer(String player)   { return plugin.getAM().getArenaWithPlayer(player); }
    public Arena getArenaWithPet(Entity wolf)        { return plugin.getAM().getArenaWithPet(wolf); }
    public Arena getArenaWithMonster(Entity monster) { return plugin.getAM().getArenaWithMonster(monster); }
    public Arena getArenaInLocation(Location l)      { return plugin.getAM().getArenaInLocation(l); }
    
    // Player lists
    public List<Player> getAllPlayers()                 { return new LinkedList<Player>(plugin.getAM().arenaMap.keySet()); }
    public List<Player> getAllPlayers(Arena arena)      { return arena.getAllPlayers(); }
    public List<Player> getAllPlayers(String arenaName) { return getAllPlayers(plugin.getAM().getArenaWithName(arenaName)); }
    
    public List<Player> getLivingPlayers()
    {
        List<Player> result = new LinkedList<Player>();
        for (Arena arena : plugin.getAM().arenas)
            result.addAll(arena.getLivingPlayers());
        return result;
    }
    
    public List<Player> getLivingPlayers(String arenaName)
    {
        Arena arena = plugin.getAM().getArenaWithName(arenaName);
        if (arena == null)
            throw new NullPointerException("Arena with name '" + arenaName + "' does not exist!");
        
        return new LinkedList<Player>(arena.livePlayers);
    }
    
    // Warp locations.
    public Location getArenaLocation(String arenaName)
    {
        Arena arena = plugin.getAM().getArenaWithName(arenaName);
        if (arena == null)
            throw new NullPointerException("Arena with name '" + arenaName + "' does not exist!");
        
        return arena.arenaLoc;
    }
    public Location getLobbyLocation(String arenaName)
    {
        Arena arena = plugin.getAM().getArenaWithName(arenaName);
        if (arena == null)
            throw new NullPointerException("Arena with name '" + arenaName + "' does not exist!");
        
        return arena.lobbyLoc;
    }
    public Location getSpectatorLocation(String arenaName)
    {
        Arena arena = plugin.getAM().getArenaWithName(arenaName);
        if (arena == null)
            throw new NullPointerException("Arena with name '" + arenaName + "' does not exist!");
        
        return arena.spectatorLoc;
    }
    
    // Get the current wave number.
    public int getWave(Arena arena) { return arena.spawnThread.wave; }
    
    public int getWave(String arenaName)
    {
        Arena arena = plugin.getAM().getArenaWithName(arenaName);
        if (arena == null)
            throw new NullPointerException("Arena with name '" + arenaName + "' does not exist!");
        
        if (arena.spawnThread == null)
            throw new NullPointerException("Arena with name '" + arenaName + "' has not started!");
        
        return arena.spawnThread.wave;
    }
    
    // Check if a location is within any arena regions.

    public boolean inRegion(Location l, Arena arena) { return arena.inRegion(l); }
    
    public boolean inRegion(Location l, String arenaName)
    {
        Arena arena = plugin.getAM().getArenaWithName(arenaName);
        if (arena == null)
            throw new NullPointerException("Arena with name '" + arenaName + "' does not exist!");
        
        return arena.inRegion(l);
    }
    public boolean inRegion(Location l)
    {
        for (Arena arena : plugin.getAM().arenas)
            if (arena.inRegion(l))
                return true;
        return false;
    }
}
