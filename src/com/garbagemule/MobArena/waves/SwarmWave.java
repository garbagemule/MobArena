package com.garbagemule.MobArena.waves;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.config.Configuration;

import com.garbagemule.MobArena.Arena;
import com.garbagemule.MobArena.MAUtils;
import com.garbagemule.MobArena.MAMessages.Msg;
import com.garbagemule.MobArena.util.WaveUtils;

public class SwarmWave extends AbstractWave
{
    private MACreature monster;
    private SwarmAmount amount;
    
    // Recurrent
    public SwarmWave(Arena arena, String name, int wave, int frequency, int priority, Configuration config, String path)
    {
        super(arena, name, wave, frequency, priority);
        load(config, path);
    }
    
    // Single
    public SwarmWave(Arena arena, String name, int wave, Configuration config, String path)
    {
        super(arena, name, wave);
        load(config, path);
    }
    
    private void load(Configuration config, String path)
    {
        // Set the wave type
        setType(WaveType.SWARM);
        
        // Get the monster type
        monster = MACreature.fromString(config.getString(path + "monster"));
        
        // And the amount
        amount = WaveUtils.getEnumFromString(SwarmAmount.class, config.getString(path + "amount"), SwarmAmount.LOW);
        
        // Load multipliers
        setHealthMultiplier(MAUtils.getDouble(config, path + "health-multiplier", 1D));
        setAmountMultiplier(MAUtils.getDouble(config, path + "amount-multiplier", 1D));
    }

    public void spawn(int wave)
    {
        // Announce spawning
        MAUtils.tellAll(getArena(), Msg.WAVE_SWARM, ""+wave);
        
        // Get the valid spawnpoints, and initialize counter
        List<Location> validSpawnpoints = WaveUtils.getValidSpawnpoints(getArena(), getArena().getLivingPlayers());
        
        // Spawn the hellians!
        int toSpawn = (int) (amount.getAmount(getArena().getPlayerCount()) * getAmountMultiplier());
        spawnAll(monster, toSpawn, validSpawnpoints);
    }
    
    public void spawnAll(MACreature monster, int amount, List<Location> spawnpoints)
    {
        int spawnpointCount = spawnpoints.size();
        for (int i = 0; i < amount; i++)
        {
            LivingEntity e = spawnMonster(monster, spawnpoints.get(i % spawnpointCount));
            
            // Boost health
            int health = (int) Math.min(150D, 1 * getHealthMultiplier());
            e.setHealth(Math.max(1, health));
        }
    }
}
