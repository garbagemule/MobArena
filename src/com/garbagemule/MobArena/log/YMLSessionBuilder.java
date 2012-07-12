package com.garbagemule.MobArena.log;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.inventory.ItemStack;

import com.garbagemule.MobArena.MobArena;
import com.garbagemule.MobArena.util.MutableInt;
import com.garbagemule.MobArena.util.TimeUtils;
import com.garbagemule.MobArena.util.config.Config;

public class YMLSessionBuilder implements LogSessionBuilder
{
    private final String GENERAL = "general-info";
    private final String PLAYERS = "players";
    private final String CLASSES = "class-distribution";
    
    private Config config;
    private long start, end;
    
    public YMLSessionBuilder(File file) {
        config = new Config(file);
        reset();
    }

    @Override
    public void buildStartTime() {
        start = new Date().getTime();
        config.set(GENERAL + ".start-time", TimeUtils.toDateTime(start));
    }

    @Override
    public void buildEndTime() {
        end = new Date().getTime();
        config.set(GENERAL + ".end-time", TimeUtils.toDateTime(end));
    }

    @Override
    public void buildDuration() {
        String duration = TimeUtils.toTime(end - start);
        config.set(GENERAL + ".duration", duration);
    }
    
    @Override
    public void buildLastWave(int lastWave) {
        config.set(GENERAL + ".last-wave", lastWave);
    }
    
    @Override
    public void buildNumberOfPlayers(int amount) {
        config.set(GENERAL + ".number-of-players", amount);
    }
    
    @Override
    public void buildClassDistribution(Map<String,MutableInt> classDistribution) {
        for (Entry<String,MutableInt> entry : classDistribution.entrySet()) {
            int amount = entry.getValue().value();
            config.set(CLASSES + "." + entry.getKey(), amount);
        }
    }

    @Override
    public void buildPlayerEntry(ArenaLogPlayerEntry entry, List<ItemStack> rewards) {
        String path = PLAYERS + "." + entry.playername + ".";
        
        // Name and class
        config.set(path + "name",  entry.playername);
        config.set(path + "class", entry.classname);
        
        // Stats
        config.set(path + "kills",        entry.kills);
        config.set(path + "damage-done",  entry.dmgDone);
        config.set(path + "damage-taken", entry.dmgTaken);
        config.set(path + "swings",       entry.swings);
        config.set(path + "hits",         entry.hits);
        config.set(path + "last-wave",    entry.lastWave);
        config.set(path + "time-played",  TimeUtils.toTime(entry.leaveTime - start));
        
        // Rewards
        Map<String,MutableInt> summed = new HashMap<String,MutableInt>();
        
        for (ItemStack stack : rewards) {
            if (stack == null) continue;
            
            String type = (stack.getTypeId() == MobArena.ECONOMY_MONEY_ID ? "money" : stack.getType().toString().toLowerCase());
            if (!summed.containsKey(type)) {
                summed.put(type, new MutableInt());
            }
            summed.get(type).add(stack.getAmount());
        }
        
        for (Entry<String,MutableInt> e : summed.entrySet()) {
            config.set(path + "rewards." + e.getKey(), e.getValue().value());
        }
    }
    
    @Override
    public void finalize() {
        config.save();
        reset();
    }
    
    private void reset() {
        if(config.get(GENERAL) != null)
            config.set(GENERAL, null);
        if(config.get(CLASSES) != null)
            config.set(CLASSES, null);
        if(config.get(PLAYERS) != null)
            config.set(PLAYERS, null);
    }
}
