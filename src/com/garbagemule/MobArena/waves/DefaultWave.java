package com.garbagemule.MobArena.waves;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.config.Configuration;

import com.garbagemule.MobArena.Arena;
import com.garbagemule.MobArena.util.WaveUtils;

public class DefaultWave extends AbstractWave
{
    private int totalProbability = 0;
    private Map<Integer,MACreature> probabilities = new TreeMap<Integer,MACreature>();
    
	// Recurrent
	public DefaultWave(Arena arena, String name, int wave, int frequency, int priority, Configuration config, String path)
	{
		super(arena, name, wave, frequency, priority);
		load(config, path);
	}
	
	// Single
	public DefaultWave(Arena arena, String name, int wave, Configuration config, String path)
	{
		super(arena, name, wave);
        load(config, path);
	}
	
	/**
	 * Prepare the wave for spawning by initializing the variables and
	 * populating the collections needed.
	 * @param config The config-file
	 * @param path The absolute path of the wave
	 */
	public void load(Configuration config, String path)
	{	    
	    // Extract the monster probabilities and calculate the sum
        totalProbability = 0;
        int prob;
	    for (String m : config.getKeys(path + "monsters"))
	    {
	        prob = config.getInt(path + "monsters." + m, 1);
            totalProbability += prob;
            probabilities.put(totalProbability, MACreature.fromString(m));
	    }
	}
    
    public void spawn(int wave)
    {
        // Get the valid spawnpoints, and initialize counter
        List<Location> validSpawnpoints = WaveUtils.getValidSpawnpoints(getArena().getSpawnpoints(), getArena().getLivingPlayers());
        int noOfSpawnpoints = validSpawnpoints.size();
        
        // Initialize the total amount of mobs to spawn
        int totalToSpawn = getGrowth().getAmount(wave, getArena().getPlayerCount());
        
        // Allocate some variables
        Random random = new Random();
        int randomNumber;
        Location loc;
        
        // Spawn <totalToSpawn> monsters
        for (int i = 0; i < totalToSpawn; i++)
        {
            // Grab the next location.
            loc = validSpawnpoints.get(i % noOfSpawnpoints);
            
            // Grab a random number.
            randomNumber = random.nextInt(totalProbability);
            
            // Find the monster that corresponds to the random number, and spawn it
            for (Map.Entry<Integer,MACreature> entry : probabilities.entrySet())
            {
                if (randomNumber > entry.getKey()) continue;
                
                // Spawn and add to collection
                LivingEntity e = entry.getValue().spawn(getWorld(), loc);
                getArena().addMonster(e);

                // Grab a random target.
                if (e instanceof Creature)
                {
                    Creature c = (Creature) e;
                    c.setTarget(WaveUtils.getClosestPlayer(getArena(), e));
                }
                
                break;
            }
        }
        
        System.out.println("WAVE SPAWN! Wave: " + wave + ", name: " + getName() + ", type: " + getType());
    }
}
