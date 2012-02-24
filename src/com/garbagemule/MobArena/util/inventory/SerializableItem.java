package com.garbagemule.MobArena.util.inventory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class SerializableItem implements Serializable
{
    private static final long serialVersionUID = -2855528738291283052L;
    
    private int id;
    private int amount;
    private short damage;
    private Byte data; 
    private Map<Integer,Integer> enchantments;
    
    private SerializableItem(ItemStack stack) {
        this.id     = stack.getTypeId();
        this.amount = stack.getAmount();
        this.damage = stack.getDurability();
        
        MaterialData md = stack.getData();
        this.data = (md == null ? null : md.getData());
        
        this.enchantments = new HashMap<Integer,Integer>();
        for (Entry<Enchantment,Integer> entry : stack.getEnchantments().entrySet()) {
            this.enchantments.put(entry.getKey().getId(), entry.getValue());
        }
    }
    
    public ItemStack toItemStack() {
        ItemStack stack = new ItemStack(id, amount, damage, data);
        
        if (!enchantments.isEmpty()) {
            for (Entry<Integer,Integer> entry : this.enchantments.entrySet()) {
                stack.addUnsafeEnchantment(Enchantment.getById(entry.getKey()), entry.getValue());
            }
        }
        
        return stack;
    }
    
    public static SerializableItem parseSerializableItem(ItemStack stack) {
        if (stack == null) {
            return null;
        }
        
        return new SerializableItem(stack);
    }
}
