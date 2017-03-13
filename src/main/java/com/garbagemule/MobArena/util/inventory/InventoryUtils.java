package com.garbagemule.MobArena.util.inventory;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class InventoryUtils
{
    public static List<ItemStack> extractAll(int id, List<ItemStack> items) {
        List<ItemStack> result = new ArrayList<>();

        for (ItemStack stack : items) {
            if (stack.getTypeId() == id) {
                result.add(stack);
            }
        }
        
        return result;
    }
}
