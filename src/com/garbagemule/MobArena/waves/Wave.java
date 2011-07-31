package com.garbagemule.MobArena.waves;

import java.util.Collection;

import org.bukkit.Location;

import com.garbagemule.MobArena.util.WaveUtils;

public interface Wave extends Comparable<Wave>
{
    public enum WaveBranch
    {
        SINGLE, RECURRENT;
    }
    
    public enum WaveType
    {
        DEFAULT, SPECIAL, SWARM, BOSS;

        public static WaveType fromString(String string)
        {
            return WaveUtils.getEnumFromString(WaveType.class, string, DEFAULT);
        }
    }
    
    public enum WaveGrowth
    {
        SLOW, MEDIUM, FAST;

        public static WaveGrowth fromString(String string)
        {
            return WaveUtils.getEnumFromString(WaveGrowth.class, string, MEDIUM); 
        }
    }
    
    public enum BossAbility
    {
        ARROWS, FIREBALLS, RING_OF_FIRE;
        
        public static BossAbility fromString(String string)
        {
            return WaveUtils.getEnumFromString(BossAbility.class, string);
        }
    }
    
    public enum BossHealth
    {
        LOW, MEDIUM, HIGH;
        
        public static BossHealth fromString(String string)
        {
            return WaveUtils.getEnumFromString(BossHealth.class, string);
        }
    }
    
    public enum SwarmAmount
    {
        LOW, MEDIUM, HIGH, PSYCHO;
        
        public static SwarmAmount fromString(String string)
        {
            return WaveUtils.getEnumFromString(SwarmAmount.class, string);
        }
    }

    /**
     * The spawn() method must spawn one or more monsters in
     * the arena. The monster count, damage, health, etc. can
     * be modified by the wave parameter.
     * @param wave Wave number
     */
    public void spawn(int wave, Collection<Location> spawnpoints);
    
    /**
     * Get the type of wave.
     * @return The WaveType of this Wave.
     */
    public WaveType getType();
    
    /**
     * Get the growth rate of this wave.
     * @return The growth rate
     */
    public WaveGrowth getGrowth();
    
    /**
     * Get the first wave number for this wave
     * @return The wave number
     */
    public int getWave();
    
    /**
     * Get the wave's frequency, i.e. wave number "modulo"
     * @return The wave's frequency
     */
    public int getFrequency();
    
    /**
     * Get the wave's priority value.
     * @return The priority
     */
    public int getPriority();
    
    /**
     * Get the wave's name.
     * @return The name
     */
    public String getName();
    
    /**
     * Check if this wave matches the wave number.
     * The SingleWave class does a simple check if its wave == the parameter.
     * The RecurrentWave class is more complex in that it needs to do some
     * calculations based on the initial wave and the frequency.
     * @param wave The current wave number
     * @return true, if the wave should spawn, false otherwise
     */
    public boolean matches(int wave);
}