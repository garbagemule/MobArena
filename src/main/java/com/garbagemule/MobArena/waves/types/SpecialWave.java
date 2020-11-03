package com.garbagemule.MobArena.waves.types;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.AbstractWave;
import com.garbagemule.MobArena.waves.MACreature;
import com.garbagemule.MobArena.waves.Wave;
import com.garbagemule.MobArena.waves.enums.WaveType;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.SortedMap;

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
        Map<MACreature,Integer> result = new HashMap<>();

        for (Map.Entry<Integer,MACreature> entry : monsterMap.entrySet()) {
            if (value > entry.getKey()) {
                continue;
            }

            int amount;
            switch (entry.getValue().getName()) {
                case "poweredcreeper":
                case "zombiepigman":
                case "angrywolf":     amount = playerCount * 2; break;
                case "slime":         amount = playerCount * 4; break;
                case "giant":
                case "ghast":         amount = 2;               break;
                default:              amount = playerCount + 1; break;
            }

            amount = (int) Math.max(1D, amount * super.getAmountMultiplier());
            result.put(entry.getValue(), amount);
            break;
        }

        return result;
    }

    public Wave copy() {
        SpecialWave result = new SpecialWave(monsterMap);

        // From AbstractWave
        result.setAmountMultiplier(getAmountMultiplier());
        result.setHealthMultiplier(getHealthMultiplier());
        result.setName(getName());
        result.setSpawnpoints(getSpawnpoints());
        result.setEffects(getEffects());
        return result;
    }
}
