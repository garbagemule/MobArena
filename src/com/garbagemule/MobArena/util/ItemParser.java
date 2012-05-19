package com.garbagemule.MobArena.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import com.garbagemule.MobArena.MobArena;

public class ItemParser
{
    public static String parseString(ItemStack... stacks) {
        String result = "";
        
        // Parse each stack
        for (ItemStack stack : stacks) {
            if (stack == null || stack.getTypeId() == 0) continue;
            
            result += ", " + parseString(stack); 
        }
        
        // Trim off the leading ', ' if it is there
        if (!result.equals("")) {
            result = result.substring(2);
        }
        
        return result;
    }
    
    public static String parseString(ItemStack stack) {
        if (stack.getTypeId() == 0) return null;
        
        // <item> part
        String type = stack.getType().toString().toLowerCase();

        // <data> part
        MaterialData md = stack.getData();
        byte data = (md != null ? md.getData() : 0);
        
        // Take wool into account
        if (stack.getType() == Material.WOOL) {
            data = (byte) (15 - data);
        }
        
        // <amount> part
        int amount = stack.getAmount();
        
        // Enchantments
        String enchantments = "";
        for (Entry<Enchantment,Integer> entry : stack.getEnchantments().entrySet()) {
            int id  = entry.getKey().getId();
            int lvl = entry.getValue();
            
            // <eid>:<level>;
            enchantments += ";" + id + ":" + lvl;
        }
        
        // Trim off the leading ';' if it is there
        if (!enchantments.equals("")) {
            enchantments = enchantments.substring(1);
        }
        
        // <item>
        String result = type;
        
        // <item>(:<data>)
        if (data != 0) {
            result += ":" + data;
        }
        
        // <item>((:<data>):<amount>) - force if there is data
        if (amount > 1 || data != 0) {
            result += ":" + amount;
        }
        
        // <item>((:<data>):<amount>) (<eid>:<level>(;<eid>:<level>(; ... )))
        if (!enchantments.equals("")) {
            result += " " + enchantments;
        }
        
        return result;
    }
    
    public static List<ItemStack> parseItems(String s) {
        if (s == null) {
            return new ArrayList<ItemStack>(1);
        }
        
        String[] items = s.split(",");
        List<ItemStack> result = new ArrayList<ItemStack>(items.length);
        
        for (String item : items) {
            ItemStack stack = parseItem(item.trim());
            if (stack != null) {
                result.add(stack);
            }
        }
        
        return result;
    }
    
    public static ItemStack parseItem(String item) {
        if (item == null || item.equals(""))
            return null;
        
        // Check if the item has enchantments.
        String[] space = item.split(" ");
        String[] parts = (space.length == 2 ? space[0].split(":") : item.split(":"));
        
        ItemStack result = null;
        
        switch (parts.length) {
            case 1:
                result = singleItem(parts[0]);
                break;
            case 2:
                result = withAmount(parts[0], parts[1]);
                break;
            case 3:
                result = withDataAndAmount(parts[0], parts[1], parts[2]);
                break;
        }
        
        if (space.length == 2) {
            addEnchantments(result, space[1]);
        }
        
        return result;
    }
    
    private static ItemStack singleItem(String item) {
        if (item.matches("\\$[1-9][0-9]*")) {
            int amount = Integer.parseInt(item.substring(1));
            return new ItemStack(MobArena.ECONOMY_MONEY_ID, amount);
        }
        Material m = getMaterial(item);
        return m == null ? null : new ItemStack(m,1);
    }
    
    private static ItemStack withAmount(String item, String amount) {
        Material m = getMaterial(item);
        int      a = getAmount(amount);
        return m == null ? null : new ItemStack(m,a);
    }
    
    private static ItemStack withDataAndAmount(String item, String data, String amount) {
        Material m = getMaterial(item);
        short    d = getData(data, m);
        int      a = getAmount(amount);
        
        if (m == null) {
            return null;
        }
        
        if (m.getId() == 35) {
            d = (byte) (15-d);
        }
        
        return new ItemStack(m,a,d);
    }
    
    private static Material getMaterial(String item) {
        if (item.matches("[0-9]*")) {
            return Material.getMaterial(Integer.parseInt(item));
        }
        
        return Material.getMaterial(item.toUpperCase());
    }
    
    private static int getAmount(String amount) {
        if (amount.matches("(-)?[1-9][0-9]*")) {
            return Integer.parseInt(amount);
        }
        
        return 1;
    }
    
    private static short getData(String data, Material m) {
        // If ink sack or wool, parse wisely.
        if (m == Material.INK_SACK || m == Material.WOOL) {
            // Ink sacks must be offset.
            byte offset = (m == Material.INK_SACK ? (byte) 15 : (byte) 0);
            
            DyeColor dye = (data.matches("[0-9]+")) ?
                DyeColor.getByData((byte) Math.abs(offset - Integer.parseInt(data))) :
                DyeColor.valueOf(data.toUpperCase());
            
            return (short) Math.abs(offset - dye.getData());
        }
        
        return Short.parseShort(data);
    }
    
    private static void addEnchantments(ItemStack stack, String list) {
        String[] parts = list.split(";");
        
        for (String ench : parts) {
            addEnchantment(stack, ench.trim());
        }
    }
    
    private static void addEnchantment(ItemStack stack, String ench) {
        String[] parts = ench.split(":");
        if (parts.length != 2 || !(parts[0].matches("[0-9]*") && parts[1].matches("[0-9]*"))) {
            return;
        }
        
        int id  = Integer.parseInt(parts[0]);
        int lvl = Integer.parseInt(parts[1]);
        
        Enchantment e = Enchantment.getById(id);
        if (e == null) {// || !e.canEnchantItem(stack) || lvl > e.getMaxLevel() || lvl < e.getStartLevel()) {
            return;
        }
        //stack.addEnchantment(e, lvl);
        stack.addUnsafeEnchantment(e, lvl);
    }
}
