package com.garbagemule.MobArena.waves;

import java.util.Collection;

import org.bukkit.Location;

public class RecurrentWave extends AbstractWave
{
    public RecurrentWave(String name, int wave, int frequency, int priority)
    {
        super(name, wave, frequency, priority);
    }
    
    public RecurrentWave(String name, int wave, int frequency, int priority, WaveType type, WaveGrowth growth)
    {
        super(name, wave, frequency, priority, WaveBranch.RECURRENT, type, growth);
    }

    public void spawn(int wave, Collection<Location> spawnpoints)
    {
        
    }
    
    public void setDefault(boolean def)
    {
        
    }
    
    public boolean matches(int wave)
    {
        if (wave < getWave())
            return false;
        
        return (wave - getWave()) % getFrequency() == 0;
    }

    /**
     * Recurrent waves are sorted by their priorities.
     * If the priorities are equal, the names are compared. This is to
     * ALLOW "duplicates" in the RECURRENT WAVES collection.
     */
    /*
    public int compareTo(Wave w)
    {
        if (getPriority() < w.getPriority())
            return -1;
        else if (getPriority() > w.getPriority())
            return 1;
        else return getName().compareTo(w.getName());
    }
    */
}
