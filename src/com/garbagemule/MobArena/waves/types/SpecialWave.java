package com.garbagemule.MobArena.waves.types;

import java.util.*;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.AbstractWave;
import com.garbagemule.MobArena.waves.MACreature;
import com.garbagemule.MobArena.waves.enums.WaveType;

public class SpecialWave extends AbstractWave
{
    private SortedMap<Integer,MACreature> monsterMap;
    
    public SpecialWave(SortedMap<Integer,MACreature> monsterMap) {
        this.monsterMap = monsterMap;
        this.setType(WaveType.SPECIAL);
    }
    
    @Override
    public Map<MACreature,Integer> getMonstersToSpawn(int wave, int playerCount, Arena arena) {
        // Random number.
        Random random = new Random();
        int value = random.nextInt(monsterMap.lastKey()); 

        // Prepare the monster map.
        Map<MACreature,Integer> result = new HashMap<MACreature,Integer>();
        
        for (Map.Entry<Integer,MACreature> entry : monsterMap.entrySet()) {
            if (value > entry.getKey()) {
                continue;
            }
            
            int amount;
            switch (entry.getValue()) {
                case POWEREDCREEPERS:
                case ZOMBIEPIGMEN:
                case ANGRYWOLVES:   amount = playerCount * 2; break;
                case SLIMES:        amount = playerCount * 4; break;
                case GIANTS:
                case GHASTS:        amount = 2;
                default:            amount = playerCount + 1; break;
            }
            
            amount = (int) Math.max(1D, amount * super.getAmountMultiplier());
            result.put(entry.getValue(), amount);
            break;
        }
        
        return result;
    }
}
