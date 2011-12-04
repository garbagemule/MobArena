package com.garbagemule.MobArena;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class MAClass
{
    private String name;
    private List<ItemStack> items, armor;
    private Map<String,Boolean> perms;
    
    public MAClass(String name)
    {
        this.name = name;
        this.items = new LinkedList<ItemStack>();
        this.armor = new LinkedList<ItemStack>();
        this.perms = new HashMap<String,Boolean>();
    }
    
    public MAClass(String name, List<ItemStack> items, List<ItemStack> armor)
    {
        this.name  = name;
        this.items = items;
        this.armor = armor;
        this.perms = new HashMap<String,Boolean>();
        sortArmor();
        immunizeWeapons();
    }
    
    public String getName()
    {
        return name;
    }
    
    public List<ItemStack> getItems()
    {
        return items;
    }
    
    public void setItems(List<ItemStack> items)
    {
        this.items = items;
        immunizeWeapons();
    }
    
    public void setArmor(List<ItemStack> armor)
    {
        this.armor = armor;
        sortArmor();
    }
    
    public void setPerms(Map<String,Boolean> perms)
    {
        this.perms = perms;
    }
    
    public void giveItems(Player p)
    {
        PlayerInventory inv = p.getInventory();
        
        for (ItemStack stack : items)
        {
            
            inv.addItem(stack.clone());
        }
    }
    
    public void giveArmor(Player p)
    {
        PlayerInventory inv = p.getInventory();

        inv.setHelmet    (armor.get(0));
        inv.setChestplate(armor.get(1));
        inv.setLeggings  (armor.get(2));
        inv.setBoots     (armor.get(3));
    }
    
    private void sortArmor()
    {
        ItemStack s0 = null, s1 = null, s2 = null, s3 = null;
        for (ItemStack stack : armor)
        {
            if (isHelmet(stack))
                s0 = stack;
            else if (isChestplate(stack))
                s1 = stack;
            else if (isLeggings(stack))
                s2 = stack;
            else if (isBoots(stack))
                s3 = stack;
        }

        armor.set(0, s0);
        armor.set(1, s1);
        armor.set(2, s2);
        armor.set(3, s3);
    }
    
    private void immunizeWeapons()
    {
        for (ItemStack stack : items)
        {
        }
    }
    
    private boolean isHelmet(ItemStack stack)
    {
        return (!isChestplate(stack) && !isLeggings(stack) && !isBoots(stack));
    }
    
    private boolean isChestplate(ItemStack stack)
    {
        return matches(stack, 299, 303, 307, 311, 315);
    }
    
    private boolean isLeggings(ItemStack stack)
    {
        return matches(stack, 300, 304, 308, 312, 316);
    }
    
    private boolean isBoots(ItemStack stack)
    {
        return matches(stack, 301, 305, 309, 313, 317);
    }
    
    private boolean isWeapon(ItemStack stack)
    {
        return matches(stack, 256, 257, 258, 267, 268, 269, 270, 271, 272, 273,
                              274, 275, 276, 277, 278, 279, 283, 284, 285, 286,
                              290, 291, 292, 293, 294, 346);
    }
    
    private boolean matches(ItemStack stack, int... ids)
    {
        int id = stack.getTypeId();
        
        for (int i : ids)
            if (id == i)
                return true;
        
        return false;
    }
}
