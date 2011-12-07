package com.garbagemule.MobArena.util;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemParser
{
    public static List<ItemStack> parseItems(String s)
    {
        List<ItemStack> result = new LinkedList<ItemStack>();
        
        String[] items = s.split(",");
        for (String item : items)
        {
            ItemStack stack = parseItem(item.trim());
            if (stack != null)
                result.add(stack);
        }
        
        return result;
    }
    
    public static ItemStack parseItem(String item)
    {
        if (item == null || item.equals(""))
            return null;
        
        String[] parts = item.split(":");
        if (parts.length == 1)
            return singleItem(parts[0]);
        if (parts.length == 2)
            return withAmount(parts[0], parts[1]);
        if (parts.length == 3)
            return withDataAndAmount(parts[0], parts[1], parts[2]);
        
        return null;
    }
    
    private static ItemStack singleItem(String item)
    {
        Material m = getMaterial(item);
        return m == null ? null : new ItemStack(m);
    }
    
    private static ItemStack withAmount(String item, String amount)
    {
        Material m = getMaterial(item);
        int      a = getAmount(amount);
        return m == null ? null : new ItemStack(m,a);
    }
    
    private static ItemStack withDataAndAmount(String item, String data, String amount)
    {
        Material m = getMaterial(item.toUpperCase());
        byte     d = getData(data); //***Needs to be an Integer so that the Potions ID numbers, which are over 255 in some cases, can be included.
        int      a = getAmount(amount);
        
        if (m == null)
            return null;
        
        if (m.getId() == 35)
            d = (byte) (15-d);//Maybe turn the forced byte into a forced Integer. Up 15 to 16472.
        
        return new ItemStack(m,a,(short)0,d);//Might need to change the short variable here.
    }
    
    private static Material getMaterial(String item)
    {
        if (item.matches("[0-9]*"))//Might need to up here ("[0-16471]*")
            return Material.getMaterial(Integer.parseInt(item));
        
        return Material.getMaterial(item.toUpperCase());
    }
    
    private static int getAmount(String amount)
    {
        if (amount.matches("[1-9][0-9]*"))//Might need to up here ("[1-16471][0-16471]*")
            return Integer.parseInt(amount);
        
        return 1;
    }
    
    private static byte getData(String data)//Probably will need to include else if code so that it can recognize potions.
    {
        DyeColor dye = null;
        
        if (data.matches("[0-9]+"))
        {
            dye = DyeColor.getByData(Byte.parseByte(data));
            return dye == null ? 0 : dye.getData();
        }
        
        else if(data.matches("[0-16471]+"))//16,471 is the highest data id assigned to 373:16471 (potions) 
        {//might be able to stick with dye variable and just up the data amount. Could also move this into else statement.
            dye = Enums.getEnumFromString(DyeColor.class, data);
            return dye == null ? 0 : (int) (16472-dye.getData());
        }
        else 
        {
            dye = Enums.getEnumFromString(DyeColor.class, data);
            return dye == null ? 0 : (byte) (15-dye.getData());
        }
    }
}
