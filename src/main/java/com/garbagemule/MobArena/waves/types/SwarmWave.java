package com.garbagemule.MobArena.waves.types;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.AbstractWave;
import com.garbagemule.MobArena.waves.MACreature;
import com.garbagemule.MobArena.waves.Wave;
import com.garbagemule.MobArena.waves.enums.SwarmAmount;
import com.garbagemule.MobArena.waves.enums.WaveType;

import java.util.HashMap;
import java.util.Map;

public class SwarmWave extends AbstractWave
{
    private MACreature monster;
    private SwarmAmount amount;
    
    public SwarmWave(MACreature monster) {
        this.monster = monster;
        this.amount  = SwarmAmount.LOW;
        this.setType(WaveType.SWARM);
    }
    
    @Override
    public Map<MACreature,Integer> getMonstersToSpawn(int wave, int playerCount, Arena arena) {
        // Prepare the monster map.
        Map<MACreature,Integer> result = new HashMap<>();
        
        // Add the monster and the swarm amount.
        int toSpawn = (int) Math.max(1D, amount.getAmount(playerCount) * super.getAmountMultiplier());
        result.put(monster, toSpawn);
        
        return result;
    }
    
    public SwarmAmount getAmount() {
        return amount;
    }
    
    public void setAmount(SwarmAmount amount) {
        this.amount = amount;
    }

    public Wave copy() {
        SwarmWave result = new SwarmWave(monster);
        result.amount = this.amount;

        // From AbstractWave
        result.setAmountMultiplier(getAmountMultiplier());
        result.setHealthMultiplier(getHealthMultiplier());
        result.setName(getName());
        result.setSpawnpoints(getSpawnpoints());
        result.setEffects(getEffects());
        return result;
    }
}
