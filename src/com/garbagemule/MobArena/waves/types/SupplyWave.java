package com.garbagemule.MobArena.waves.types;

import java.util.*;

import org.bukkit.inventory.ItemStack;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.AbstractWave;
import com.garbagemule.MobArena.waves.MACreature;
import com.garbagemule.MobArena.waves.enums.WaveType;

public class SupplyWave extends AbstractWave
{
    private SortedMap<Integer,MACreature> monsterMap;
    private List<ItemStack> drops;
    
    public SupplyWave(SortedMap<Integer,MACreature> monsterMap) {
        this.monsterMap = monsterMap;
        this.setType(WaveType.SUPPLY);
    }
    
    @Override
    public Map<MACreature,Integer> getMonstersToSpawn(int wave, int playerCount, Arena arena) {
        // Grab the total probability sum.
        int total = monsterMap.lastKey();
        
        // Random number generator.
        Random random = new Random();
        
        // Prepare the monster map.
        Map<MACreature,Integer> monsters = new HashMap<MACreature,Integer>();
        
        int toSpawn = (int) Math.max(1D, playerCount * super.getAmountMultiplier());
        
        // Spawn a monster for each player.
        for (int i = 0; i < toSpawn; i++) {
            int value = random.nextInt(total) + 1;
            
            for (Map.Entry<Integer,MACreature> entry : monsterMap.entrySet()) {
                if (value > entry.getKey()) {
                    continue;
                }
                
                Integer current = monsters.get(entry.getValue());
                monsters.put(entry.getValue(), (current == null ? 1 : current + 1));
                break;
            }
        }
        
        // Return the map.
        return monsters;
    }
    
    public List<ItemStack> getDropList() {
        return drops;
    }
    
    public void setDropList(List<ItemStack> drops) {
        this.drops = drops;
    }
}
