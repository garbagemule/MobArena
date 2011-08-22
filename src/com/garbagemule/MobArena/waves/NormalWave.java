package com.garbagemule.MobArena.waves;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.bukkit.util.config.Configuration;

import com.garbagemule.MobArena.Arena;
import com.garbagemule.MobArena.util.WaveUtils;

public abstract class NormalWave extends AbstractWave
{
    private int totalProbability = 0;
    private Map<Integer,MACreature> probabilities = new TreeMap<Integer,MACreature>();
    
    // Recurrent
    public NormalWave(Arena arena, String name, int wave, int frequency, int priority, Configuration config, String path)
    {
        super(arena, name, wave, frequency, priority);
    }
    
    // Single
    public NormalWave(Arena arena, String name, int wave, Configuration config, String path)
    {
        super(arena, name, wave);
    }
    
    /**
     * Prepare the wave for spawning by initializing the variables and
     * populating the collections needed.
     * @param config The config-file
     * @param path The absolute path of the wave
     * @param type DEFAULT or SPECIAL
     */
    public void load(Configuration config, String path, WaveType type)
    {
        // Set type and (for DEFAULT) growth.
        setType(type);
        if (type == WaveType.DEFAULT)
            setGrowth(WaveUtils.getEnumFromString(WaveGrowth.class, config.getString(path + "growth"), WaveGrowth.OLD));
        
        // Load monsters
        int prob;
        List<String> monsters = config.getKeys(path + "monsters");
        if (monsters != null && !monsters.isEmpty())
        {
            for (String m : config.getKeys(path + "monsters"))
            {
                prob = config.getInt(path + "monsters." + m, 1);
                if (prob == 0) continue;
                
                incTotalProbability(prob);
                probabilities.put(totalProbability, MACreature.fromString(m));
            }
        }
        else
        {
            if (type == WaveType.DEFAULT)
            {
                probabilities.put(10, MACreature.ZOMBIES);
                probabilities.put(20, MACreature.SKELETONS);
                probabilities.put(30, MACreature.SPIDERS);
                probabilities.put(40, MACreature.CREEPERS);
                probabilities.put(50, MACreature.WOLVES);
                totalProbability = 50;
            }
            else if (type == WaveType.SPECIAL)
            {
                probabilities.put(10, MACreature.POWEREDCREEPERS);
                probabilities.put(20, MACreature.ANGRYWOLVES);
                probabilities.put(30, MACreature.ZOMBIEPIGMEN);
                probabilities.put(40, MACreature.SLIMES);
                probabilities.put(50, MACreature.HUMANS);
                totalProbability = 50;
            }
        }
    }
    
    public int getTotalProbability()
    {
        return totalProbability;
    }
    
    public void incTotalProbability(int value)
    {
        totalProbability += value;
    }
    
    public Map<Integer,MACreature> getProbabilityMap()
    {
        return probabilities;
    }
    
    public abstract void spawn(int wave);
}
