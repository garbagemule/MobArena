package com.garbagemule.MobArena.waves.types;

import java.util.*;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.AbstractWave;
import com.garbagemule.MobArena.waves.MACreature;
import com.garbagemule.MobArena.waves.enums.WaveType;

public class UpgradeWave extends AbstractWave
{
    private Map<String,List<ItemStack>> classMap;
    private boolean giveAll;
    
    public UpgradeWave(Map<String,List<ItemStack>> classMap) {
        this.classMap = classMap;
        this.setType(WaveType.UPGRADE);
    }
    
    @Override
    public Map<MACreature,Integer> getMonstersToSpawn(int wave, int playerCount, Arena arena) {
        return new HashMap<MACreature,Integer>();
    }
    
    public void grantItems(Player p, String className) {
        List<ItemStack> stacks = classMap.get(className);
        if (stacks == null || stacks.isEmpty()) return;

        PlayerInventory inv = p.getInventory();
        
        // Check if we need to add all of the items or just one.
        if (giveAll) {
            for (ItemStack stack : stacks) {
                inv.addItem(stack);
            }
        }
        // Get a random item from the list.
        else {
            int index = new Random().nextInt(stacks.size());
            inv.addItem(stacks.get(index));
        }
    }
    
    public void setGiveAll(boolean giveAll) {
        this.giveAll = giveAll;
    }
}
