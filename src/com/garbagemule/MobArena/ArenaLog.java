package com.garbagemule.MobArena;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import com.garbagemule.MobArena.util.data.PlainText;
import com.garbagemule.MobArena.util.data.Totals;

public class ArenaLog
{
    public MobArena  plugin;
    public Arena     arena;
    public Timestamp startTime, endTime;
    public int       lastWave;

    public Map<Player,ArenaPlayer> players;
    public Map<String,Integer>     distribution;
    
    public ArenaLog(MobArena plugin, Arena arena)
    {
        this.plugin       = plugin;
        this.arena        = arena;
        this.players      = new HashMap<Player,ArenaPlayer>(arena.arenaPlayers.size());
        this.distribution = new HashMap<String,Integer>(arena.classes.size());
    }
    
    public void start()
    {
        // Populate the data maps
        populatePlayerMap();
        populateDistributionMap();
        
        // Grab the current timestamp
        startTime    = new Timestamp((new Date()).getTime());
    }
    
    public void end()
    {
        lastWave = arena.spawnThread.getWave() - 1;
        endTime = new Timestamp((new Date()).getTime());
    }
    
    /**
     * Map players to ArenaPlayer objects.
     */
    private void populatePlayerMap()
    {
        for (Player p : arena.arenaPlayers)
            players.put(p, new ArenaPlayer(p, arena, plugin));
    }
    
    /**
     * Map classes to amounts of players playing as the classes.
     */
    private void populateDistributionMap()
    {        
        // Initialize the map
        for (String c : arena.classes)
            distribution.put(c,0);

        // Count occurrences
        for (String c : arena.classMap.values())
            distribution.put(c,distribution.get(c) + 1);
    }
    
    /**
     * Save the data of the current session according to the logging type.
     */
    public void saveSessionData()
    {
        /*
         * Call saveSessionData on the correct utility class
         */
        //XML.saveSessionData(this, plugin);
        //CSV.saveSessionData(this, plugin);
        //YAML.saveSessionData(this, plugin);
        PlainText.saveSessionData(this);
    }
    
    public void updateArenaTotals()
    {
        Totals.updateArenaTotals(this);
        PlainText.updateArenaTotals(this);
    }
    
    /**
     * Clear maps.
     */
    public void clearSessionData()
    {
        players.clear();
        distribution.clear();
    }
    
    public Arena getArena()         { return arena; }
    public Timestamp getStartTime() { return startTime; }
    public Timestamp getEndTime()   { return endTime; }
    public String getDuration()     { return MAUtils.getDuration(endTime.getTime() - startTime.getTime()); }
    public long getDurationLong()   { return endTime.getTime() - startTime.getTime(); }
    public int getLastWave()        { return lastWave; }
    
    public void playerKill(Player p)
    {
        players.get(p).kills++;
    }
    
    public void playerDamager(Player p, int damage)
    {
        players.get(p).dmgDone += damage;
    }
    
    public void playerDamagee(Player p, int damage)
    {
        players.get(p).dmgTaken += damage;
    }
}
