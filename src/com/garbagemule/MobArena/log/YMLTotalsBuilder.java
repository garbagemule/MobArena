package com.garbagemule.MobArena.log;

import java.io.File;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import com.garbagemule.MobArena.util.MutableInt;
import com.garbagemule.MobArena.util.TimeUtils;
import com.garbagemule.MobArena.util.config.Config;

public class YMLTotalsBuilder implements LogTotalsBuilder
{
    private final String GENERAL = "general-info.";
    private final String PLAYERS = "players.";
    private final String CLASSES = "class-distribution.";
    
    private Config config;
    private long start;
    
    public YMLTotalsBuilder(File file) {
        config = new Config(file);
        if (file.exists()) {
            config.load();
        }
    }
    
    @Override
    public void recordStartTime() {
        start = new Date().getTime();
    }
    
    @Override
    public void updateSessionsPlayed() {
        String path = GENERAL + "sessions-played";
        int games = config.getInt(path, 0);
        config.set(path, games + 1);
    }
    
    @Override
    public void updateTimePlayed() {
        long end = new Date().getTime();
        String duration = TimeUtils.toTime(end - start);
        
        String path = GENERAL + "time-played";
        String old = config.getString(path, "00:00:00");
        config.set(path, TimeUtils.addTimes(old, duration));
    }

    @Override
    public void updateLastWave(int lastWave) {
        String path = GENERAL + "last-wave";
        int old = config.getInt(path, 0);
        config.set(path, Math.max(old, lastWave));
    }

    @Override
    public void updateClassDistribution(Map<String, MutableInt> classDistribution) {
        for (Entry<String,MutableInt> entry : classDistribution.entrySet()) {
            String path = CLASSES + entry.getKey();
            int old = config.getInt(path, 0);
            int amount = entry.getValue().value();
            config.set(path, old + amount);
        }
    }

    @Override
    public void updatePlayerEntry(ArenaLogPlayerEntry entry) {
        String path = PLAYERS + entry.playername + ".";
        
        // Name and class
        config.set(path + "name",  entry.playername);
        int old = config.getInt(path + "classes." + entry.classname, 0);
        config.set(path + "classes." + entry.classname, old + 1);
        
        // All stats
        old = config.getInt(path + "kills", 0);
        config.set(path + "kills", old + entry.kills);
        
        old = config.getInt(path + "damage-done", 0);
        config.set(path + "damage-done", old + entry.dmgDone);

        old = config.getInt(path + "damage-taken", 0);
        config.set(path + "damage-taken", old + entry.dmgTaken);

        old = config.getInt(path + "swings", 0);
        config.set(path + "swings", old + entry.swings);

        old = config.getInt(path + "hits", 0);
        config.set(path + "hits", old + entry.hits);
        
        old = config.getInt(path + "games-played", 0);
        config.set(path + "games-played", old + 1);
        
        old = config.getInt(path + "last-wave", 0);
        config.set(path + "last-wave", Math.max(old, entry.lastWave));
        
        // Time played
        long end = new Date().getTime();
        String session = TimeUtils.toTime(end - start);
        String played = config.getString(path + "time-played", "00:00:00");
        config.set(path + "time-played", TimeUtils.addTimes(played, session));
    }

    @Override
    public void finish() {
        config.save();
    }
}
