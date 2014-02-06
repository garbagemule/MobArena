package com.garbagemule.MobArena.util.inventory;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;

public class InventoryUtils
{
    public static List<ItemStack> extractAll(int id, List<ItemStack> items) {
        List<ItemStack> result = new ArrayList<ItemStack>();

        for (ItemStack stack : items) {
            if (stack.getTypeId() == id) {
                result.add(stack);
            }
        }
        
        return result;
    }
}
