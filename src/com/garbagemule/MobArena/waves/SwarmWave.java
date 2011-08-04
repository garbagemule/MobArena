package com.garbagemule.MobArena.waves;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.config.Configuration;

import com.garbagemule.MobArena.Arena;
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
        // Get the monster type
        monster = MACreature.fromString(config.getString(path + "monster"));
        
        // And the amount
        amount = WaveUtils.getEnumFromString(SwarmAmount.class, config.getString(path + "amount"), SwarmAmount.LOW);
    }

    public void spawn(int wave)
    {
        // Get the valid spawnpoints, and initialize counter
        List<Location> validSpawnpoints = WaveUtils.getValidSpawnpoints(getArena().getSpawnpoints(), getArena().getLivingPlayers());
        
        // Spawn the hellians!
        spawnAll(monster, amount.getAmount(getArena().getPlayerCount()), validSpawnpoints);
    }
    
    public void spawnAll(MACreature monster, int amount, List<Location> spawnpoints)
    {
        int spawnpointCount = spawnpoints.size();
        for (int i = 0; i < amount; i++)
        {
            LivingEntity e = spawnMonster(monster, spawnpoints.get(i % spawnpointCount));
            e.setHealth(1);
        }
    }
}
