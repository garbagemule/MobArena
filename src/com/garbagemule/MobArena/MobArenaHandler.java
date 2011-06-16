package com.garbagemule.MobArena;

import java.util.List;
import java.util.LinkedList;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class MobArenaHandler
{
    public MobArenaHandler()
    {
    }

    // Check if there is an active arena session running.
    public boolean isRunning()             { return ArenaManager.isRunning; }
    
    // Check if the specified player is in the arena/lobby.
    public boolean isPlaying(Player p)     { return ArenaManager.playerSet.contains(p); }
    
    // Get a list of all players currently in the arena.
    public List<Player> getPlayers()       { return new LinkedList<Player>(ArenaManager.playerSet); }
    
    // Get the warp locations.
    public Location getArenaLocation()     { return ArenaManager.arenaLoc; }
    public Location getLobbyLocation()     { return ArenaManager.lobbyLoc; }
    public Location getSpectatorLocation() { return ArenaManager.spectatorLoc; }
    
    // Get the current wave number.
    public int getWave()                   { return ArenaManager.wave; }
    
    // Check if a location is in the arena region
    public boolean inRegion(Location l)    { return MAUtils.inRegion(l); }
}
