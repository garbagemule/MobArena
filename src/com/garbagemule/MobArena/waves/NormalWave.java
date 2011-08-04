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
                incTotalProbability(prob);
                getProbabilityMap().put(getTotalProbability(), MACreature.fromString(m));
            }
        }
        else
        {
            if (type == WaveType.DEFAULT)
            {
                getProbabilityMap().put(10, MACreature.ZOMBIES);
                getProbabilityMap().put(10, MACreature.SKELETONS);
                getProbabilityMap().put(10, MACreature.SPIDERS);
                getProbabilityMap().put(10, MACreature.CREEPERS);
                getProbabilityMap().put(10, MACreature.WOLVES);
            }
            else if (type == WaveType.SPECIAL)
            {
                getProbabilityMap().put(10, MACreature.POWERED_CREEPERS);
                getProbabilityMap().put(10, MACreature.ANGRY_WOLVES);
                getProbabilityMap().put(10, MACreature.ZOMBIE_PIGMEN);
                getProbabilityMap().put(10, MACreature.SLIMES);
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
