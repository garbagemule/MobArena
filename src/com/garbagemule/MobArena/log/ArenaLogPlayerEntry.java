package com.garbagemule.MobArena.log;

import java.util.Date;
import java.util.Map;

import org.bukkit.Material;

import com.garbagemule.MobArena.ArenaPlayer;
import com.garbagemule.MobArena.ArenaPlayerStatistics;

public class ArenaLogPlayerEntry
{
    /**
     * Player name is used as a unique ID.
     */
    protected String playername, classname;
    
    /**
     * All recordable statistics.
     */
    protected int kills, dmgDone, dmgTaken, swings, hits, lastWave;
    
    /**
     * The time at which the player left or died.
     */
    protected long leaveTime;
    
    /**
     * Total amounts of each item rewarded.
     */
    protected Map<Material,Integer> rewards;
    
    private ArenaLogPlayerEntry() {}
    
    public static ArenaLogPlayerEntry create(ArenaPlayer ap) {
        ArenaLogPlayerEntry entry = new ArenaLogPlayerEntry();

        entry.playername = ap.getPlayer().getName();
        entry.classname  = ap.getArenaClass().getLowercaseName();
        
        ArenaPlayerStatistics stats = ap.getStats();
        entry.kills     = stats.getInt("kills");
        entry.dmgDone   = stats.getInt("dmgDone");
        entry.dmgTaken  = stats.getInt("dmgTaken");
        entry.swings    = stats.getInt("swings");
        entry.hits      = stats.getInt("hits");
        entry.lastWave  = stats.getInt("lastWave");
        
        entry.leaveTime = (new Date()).getTime();
        
        return entry;
    }
}
