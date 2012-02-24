package com.garbagemule.MobArena.util.inventory;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;

public class InventoryUtils
{
    public static SerializableItem parseItemStack(ItemStack stack) {
        return SerializableItem.parseSerializableItem(stack);
    }
    
    public static SerializableItem[] parseItemStacks(ItemStack... stacks) {
        SerializableItem[] items = new SerializableItem[stacks.length];
        
        for (int i = 0; i < items.length; i++) {
            items[i] = parseItemStack(stacks[i]);
        }
        
        return items;
    }

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
