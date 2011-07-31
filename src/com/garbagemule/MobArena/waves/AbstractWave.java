package com.garbagemule.MobArena.waves;

public abstract class AbstractWave implements Wave
{
    private String name;
    private int wave, frequency, priority;
    private WaveType type;
    private WaveGrowth growth;
    
    /**
     * Basic wave constructor.
     * Constructs a wave with an initial wave number, a wave frequency, and
     * a wave priority.
     * @param wave Initial wave number. This is the first wave number this wave can spawn at.
     * @param frequency How often the wave can spawn.
     * @param priority The priority of the wave.
     */
    public AbstractWave(String name, int wave, int frequency, int priority)
    {
        this.name      = name;
        this.wave      = wave;
        this.frequency = frequency;
        this.priority  = priority;
    }

    /**
     * Default wave constructor.
     * Constructs a basic wave with additional information in the type of
     * wave and the wave growth.
     * @param wave Initial wave number. This is the first wave number this wave can spawn at.
     * @param frequency How often the wave can spawn.
     * @param priority The priority of the wave.
     * @param type The type of wave.
     * @param growth The growth rate of the wave.
     */
    public AbstractWave(String name, int wave, int frequency, int priority, WaveType type, WaveGrowth growth)
    {
        this(name, wave, frequency, priority);
        this.type   = type;
        this.growth = growth;
    }

    public WaveType getType()
    {
        return type;
    }
    
    public void setType(WaveType type)
    {
        this.type = type;
    }

    public WaveGrowth getGrowth()
    {
        return growth;
    }
    
    public void setGrowth(WaveGrowth growth)
    {
        this.growth = growth;
    }

    public int getWave()
    {
        return wave;
    }

    public int getFrequency()
    {
        return frequency;
    }

    public int getPriority()
    {
        return priority;
    }
    
    public String getName()
    {
        return name;
    }
    
    public String toString()
    {
        return "[name=" + name +
                ", wave=" + wave +
                ", frequency=" + frequency +
                ", priority=" + priority + "]";
    }
}
