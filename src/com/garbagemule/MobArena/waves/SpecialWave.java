package com.garbagemule.MobArena.waves;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.util.config.Configuration;

import com.garbagemule.MobArena.Arena;
import com.garbagemule.MobArena.MAUtils;
import com.garbagemule.MobArena.MAMessages.Msg;
import com.garbagemule.MobArena.util.WaveUtils;

public class SpecialWave extends NormalWave
{    
	// Recurrent
    public SpecialWave(Arena arena, String name, int wave, int frequency, int priority, Configuration config, String path)
	{
		super(arena, name, wave, frequency, priority, config, path);
        load(config, path, WaveType.SPECIAL);
	}
	
	// Single
    public SpecialWave(Arena arena, String name, int wave, Configuration config, String path)
	{
		super(arena, name, wave, config, path);
        load(config, path, WaveType.SPECIAL);
	}

	public void spawn(int wave)
	{
        // Announce spawning
        MAUtils.tellAll(getArena(), Msg.WAVE_SPECIAL, ""+wave);
        
        // Get the valid spawnpoints, and initialize counter
        List<Location> validSpawnpoints = WaveUtils.getValidSpawnpoints(getArena(), getArena().getLivingPlayers());
        
        // Strike some lightning!
        for (Location loc : validSpawnpoints)
            getWorld().strikeLightningEffect(loc);
        
        // Spawn all the monsters
        spawnAll(getMonstersToSpawn(getArena().getPlayerCount()), validSpawnpoints);
	}
    
    private Map<MACreature,Integer> getMonstersToSpawn(int playerCount)
    {
        Map<MACreature,Integer> result = new HashMap<MACreature,Integer>();
        
        Random random = new Random();
        int randomNumber = random.nextInt(getTotalProbability()); 
        for (Map.Entry<Integer,MACreature> entry : getProbabilityMap().entrySet())
        {
            if (randomNumber > entry.getKey()) continue;
            
            int amount;
            switch (entry.getValue())
            {
                case POWEREDCREEPERS:
                case ZOMBIEPIGMEN:
                case ANGRYWOLVES:       amount = playerCount * 2; break;
                case SLIMES:            amount = playerCount * 4; break;
                case HUMANS:            amount = playerCount + 2; break;
                case GIANTS:
                case GHASTS:            amount = 2;
                default:                amount = playerCount + 1; break;
            }
            
            result.put(entry.getValue(), amount);
            break;
        }
        
        return result;
    }
}
