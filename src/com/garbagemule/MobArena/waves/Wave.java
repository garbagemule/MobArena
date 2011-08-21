package com.garbagemule.MobArena.waves;

import com.garbagemule.MobArena.Arena;
import com.garbagemule.MobArena.util.WaveUtils;

public interface Wave
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
            return WaveUtils.getEnumFromString(WaveType.class, string);
        }
    }
    
    public enum WaveGrowth
    {
        OLD(0), SLOW(0.5), MEDIUM(0.65), FAST(0.8), PSYCHO(1.2);
        private double exp;
        
        private WaveGrowth(double exp)
        {
            this.exp = exp;
        }
        
        public static WaveGrowth fromString(String string)
        {
            return WaveUtils.getEnumFromString(WaveGrowth.class, string, OLD); 
        }
        
        public int getAmount(int wave, int playerCount)
        {
            if (this == OLD) return wave + playerCount;
            
            double pc = (double) playerCount;
            double w  = (double) wave;
            
            double base = Math.min(Math.ceil(pc/2) + 1, 13);
            return (int) ( base * Math.pow(w, exp) );
        }
    }

    public enum BossHealth
    {
        LOW(8), MEDIUM(15), HIGH(25), PSYCHO(40);
        private int multiplier;
        
        private BossHealth(int multiplier)
        {
            this.multiplier = multiplier;
        }
        
        public int getAmount(int playerCount)
        {
            return (playerCount + 1) * 20 * multiplier;
        }
        
        public static BossHealth fromString(String string)
        {
            return WaveUtils.getEnumFromString(BossHealth.class, string);
        }
    }
    
    public enum SwarmAmount
    {
        LOW(10), MEDIUM(20), HIGH(30), PSYCHO(60);
        private int multiplier;
        
        private SwarmAmount(int multiplier)
        {
            this.multiplier = multiplier;
        }
        
        public int getAmount(int playerCount)
        {
            return Math.max(1, playerCount / 2) * multiplier;
        }
        
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
    public void spawn(int wave);
    
    /**
     * Get the type of wave.
     * @return The WaveType of this Wave.
     */
    public WaveType getType();

    /**
     * Get the branch type.
     * @return The WaveBranch of this Wave.
     */
    public WaveBranch getBranch();
    
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
     * Get the arena of this wave.
     * @return The arena
     */
    public Arena getArena();

    /**
     * Set the wave's growth
     * @param growth How fast the wave will grow
     */
    public void setGrowth(WaveGrowth growth);
    
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