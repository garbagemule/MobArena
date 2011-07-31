package com.garbagemule.MobArena.waves;

import java.util.Collection;

import org.bukkit.Location;

public class SingleWave extends AbstractWave
{
    public SingleWave(String name, int wave)
    {
        super(name, wave, 0, 0);
    }

    public void spawn(int wave, Collection<Location> spawnpoints)
    {
        
    }

    public boolean matches(int wave)
    {
        return getWave() == wave;
    }
    
    /**
     * Single waves are compared by their wave numbers.
     * If the wave numbers are equal, the waves are equal. This is to
     * DISALLOW "duplicates" in the SINGLE WAVES collection.
     */
    public int compareTo(Wave w)
    {
        if (this.getWave() < w.getWave())
            return -1;
        else if (this.getWave() > w.getWave())
            return 1;
        else return 0;
    }
}
