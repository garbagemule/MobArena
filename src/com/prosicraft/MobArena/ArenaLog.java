package com.prosicraft.MobArena;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import com.garbagemule.MobArena.util.data.PlainText;
import com.garbagemule.MobArena.util.data.Totals;
import com.garbagemule.MobArena.util.data.XML;
import com.garbagemule.MobArena.util.data.YAML;

public class ArenaLog
{
    public MobArena  plugin;
    public Arena     arena;
    public Timestamp startTime, endTime;
    public int       lastWave;

    public Map<Player,ArenaPlayer> players;
    public Map<String,Integer>     distribution;
    
    /**
     * Create a new ArenaLog.
     * @param plugin MobArena instance
     * @param arena The arena
     */
    public ArenaLog(MobArena plugin, Arena arena)
    {
        this.plugin       = plugin;
        this.arena        = arena;
        this.players      = new HashMap<Player,ArenaPlayer>(arena.arenaPlayers.size());
        this.distribution = new HashMap<String,Integer>(arena.classes.size());
    }
    
    /**
     * Start logging by creating ArenaPlayer objects and recording
     * the class distributions and the start time.
     */
    public void start()
    {
        // Populate the data maps
        populatePlayerMap();
        populateDistributionMap();
        
        // Grab the current timestamp
        startTime = new Timestamp((new Date()).getTime());
    }
    
    /**
     * End logging by recording the last wave and the end time.
     */
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
        if (arena.logging.equals("xml"))
            XML.saveSessionData(this);
        else if (arena.logging.equals("yml") || arena.logging.equals("yaml"))
            YAML.saveSessionData(this);
        else
            PlainText.saveSessionData(this);
        //CSV.saveSessionData(this, plugin);
        //YAML.saveSessionData(this);
    }
    
    /**
     * Update the totals-file
     */
    public void updateArenaTotals()
    {
        Totals.updateArenaTotals(this);
        
        if (arena.logging.equals("xml"))
            XML.updateArenaTotals(this);
        //PlainText.updateArenaTotals(this);
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
        players.get(p).getStats().kills++;
    }
    
    public void playerDamager(Player p, int damage)
    {
        players.get(p).getStats().dmgDone += damage;
    }
    
    public void playerDamagee(Player p, int damage)
    {
        players.get(p).getStats().dmgTaken += damage;
    }
}
