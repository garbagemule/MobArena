package com.garbagemule.MobArena.waves;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.util.config.Configuration;

import com.garbagemule.MobArena.Arena;
import com.garbagemule.MobArena.util.WaveUtils;

public class DefaultWave extends NormalWave
{    
    // Recurrent
    public DefaultWave(Arena arena, String name, int wave, int frequency, int priority, Configuration config, String path)
    {
        super(arena, name, wave, frequency, priority, config, path);
        load(config, path, WaveType.DEFAULT);
    }
    
    // Single
    public DefaultWave(Arena arena, String name, int wave, Configuration config, String path)
    {
        super(arena, name, wave, config, path);
        load(config, path, WaveType.DEFAULT);
    }
    
    /**
     * Default waves spawn an amount of random monsters, picked from a
     * map of probabilities. The amount to spawn depends on the wave
     * number and player count.
     */
    public void spawn(int wave)
    {
        // Get the valid spawnpoints, and initialize counter
        List<Location> validSpawnpoints = WaveUtils.getValidSpawnpoints(getArena().getSpawnpoints(), getArena().getLivingPlayers());

        // Initialize the total amount of mobs to spawn
        int totalToSpawn = getGrowth().getAmount(wave, getArena().getPlayerCount());
        
        // Spawn all the monsters
        spawnAll(getMonstersToSpawn(totalToSpawn), validSpawnpoints);
        System.out.println("WAVE SPAWN! Wave: " + wave + ", name: " + getName() + ", type: " + getType());
    }
    
    private Map<MACreature,Integer> getMonstersToSpawn(int totalToSpawn)
    {
        Map<MACreature,Integer> result = new HashMap<MACreature,Integer>();
        Random random = new Random();
        int randomNumber;
        MACreature creature;
        
        for (int i = 0; i < totalToSpawn; i++)
        {
            randomNumber = random.nextInt(getTotalProbability());
            
            // Find the monster that corresponds to the random number, and increment its value 
            for (Map.Entry<Integer,MACreature> entry : getProbabilityMap().entrySet())
            {
                if (randomNumber > entry.getKey()) continue;
                
                creature = entry.getValue();                
                if (result.containsKey(entry.getValue()))
                    result.put(creature, result.get(creature) + 1);
                else
                    result.put(creature, 1);
                break;
            }
        }
        
        return result;
    }
}
