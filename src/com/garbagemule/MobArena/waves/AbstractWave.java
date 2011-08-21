package com.garbagemule.MobArena.waves;

import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;

import com.garbagemule.MobArena.Arena;
import com.garbagemule.MobArena.util.WaveUtils;

public abstract class AbstractWave implements Wave
{
    private Arena arena;
    private World world;
    private String waveName;
    private int wave, frequency, priority;
    private WaveBranch branch;
    private WaveType type;
    private WaveGrowth growth;
    
    /**
     * Basic wave constructor.
     * @param name The config-file identifier
     * @param wave Initial wave number. This is the first wave number this wave can spawn at.
     * @param frequency How often the wave can spawn.
     * @param priority The priority of the wave.
     * @param branch The branch type (single, recurrent)
     */    
    public AbstractWave(Arena arena, String waveName, int wave, int frequency, int priority, WaveBranch branch)
    {
        this.arena     = arena;
        this.world     = arena.getWorld();
    	this.waveName  = waveName;
    	this.wave      = wave;
    	this.frequency = frequency;
    	this.priority  = priority;
    	this.branch    = branch;
    }
    
    // Default recurrent wave constructor
    public AbstractWave(Arena arena, String name, int wave, int frequency, int priority)
    {
        this(arena, name, wave, frequency, priority, WaveBranch.RECURRENT);
    }
    
    // Default single wave constructor
    public AbstractWave(Arena arena, String name, int wave)
    {
        this(arena, name, wave, 0, 0, WaveBranch.SINGLE);
    }
    
    /**
     * Check if a wave matches a wave number.
     * SINGLE WAVES match, if their wave number is the same as the
     * parameter.
     * RECURRENT WAVES match, if their wave number subtracted from
     * the parameter divides the frequency. The wave number must be
     * greater than or equal to the parameter.
     * @param wave The wave number to compare
     * @return true, if the wave matches the wave number
     */
    public boolean matches(int wave)
    {
    	if (branch == WaveBranch.SINGLE)
    		return this.wave == wave;
    	
    	if (branch == WaveBranch.RECURRENT && wave >= this.wave)
    		return ((wave - this.wave) % frequency == 0);
    	
    	return false;    		
    }
    
    /**
     * Spawn an MACreature in the given location. The actual LivingEntity
     * spawned is returned.
     * @param creature The MACreature to spawn
     * @param loc The location to spawn the MACreature in
     * @return The resulting LivingEntity
     */
    public LivingEntity spawnMonster(MACreature creature, Location loc)
    {
        // Spawn and add to collection
        LivingEntity e = creature.spawn(getArena(), getWorld(), loc);
        getArena().addMonster(e);

        // Grab a random target.
        if (e instanceof Creature)
        {
            Creature c = (Creature) e;
            c.setTarget(WaveUtils.getClosestPlayer(getArena(), e));
        }

        return e;
    }
    
    /**
     * Helper method for spawning a bunch of monsters over a
     * collection of spawnpoints. Used by wave types DEFAULT,
     * SPECIAL and SWARM.
     * @param monsters A collection of monsters to spawn, and how many of each
     * @param spawnpoints The spawnpoints to spawn the monsters over
     */
    public void spawnAll(Map<MACreature,Integer> monsters, List<Location> spawnpoints)
    {
        Random random = new Random();
        int spawnpointCount = spawnpoints.size();
        int index = random.nextInt(spawnpointCount);
        
        for (Map.Entry<MACreature,Integer> entry : monsters.entrySet())
        {
            for (int i = 0; i < entry.getValue(); i++)
            {
                spawnMonster(entry.getKey(), spawnpoints.get(index % spawnpointCount));
                index++;
            }
        }
    }
        
    // GETTERS
    public Arena getArena()
    {
        return arena;
    }
    
    public World getWorld()
    {
        return world;
    }
    
    public WaveBranch getBranch()
    {
        return branch;
    }

    public WaveType getType()
    {
        return type;
    }

    public WaveGrowth getGrowth()
    {
        return growth;
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
        return waveName;
    }

    // SETTERS
    public void setBranch(WaveBranch branch)
    {
        this.branch = branch;
    }
    
    public void setType(WaveType type)
    {
        this.type = type;
    }
    
    public void setGrowth(WaveGrowth growth)
    {
        this.growth = growth;
    }
    
    // MISC
    public String toString()
    {
        if (branch == WaveBranch.RECURRENT)
            return "[Wave type=" + type +
                    " name=" + waveName +
                    " branch=" + branch.toString().charAt(0) +
                    " freq=" + frequency +
                    " prio=" + priority + "]";
        return "[Wave type=" + type +
                    " name=" + waveName +
                    " branch=" + branch.toString().charAt(0) +
                    " wave=" + wave + "]";
    }
}
