package com.garbagemule.MobArena.waves.types;

import java.util.*;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.AbstractWave;
import com.garbagemule.MobArena.waves.MACreature;
import com.garbagemule.MobArena.waves.enums.*;

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
        Map<MACreature,Integer> result = new HashMap<MACreature,Integer>();
        
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
}
