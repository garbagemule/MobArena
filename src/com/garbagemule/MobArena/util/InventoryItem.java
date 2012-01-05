package com.garbagemule.MobArena.util;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class InventoryItem implements Serializable
{
    private static final long serialVersionUID = 739709220350581510L;
    private int id;
    private int amount;
    private Byte data;
    private short durability;
    private HashMap<Integer, Integer> enchantments;
    
    /**
     * Default constructor.
     * @param id The data value/type id of the ItemStack
     * @param amount The amount of the stack
     * @param data The MaterialData (possibly null) of the stack
     * @param durability The durability of the stack
     */
    public InventoryItem(int id, int amount, Byte data, short durability)
    {
        this.id         = id;
        this.amount     = amount;
        this.data       = data;
        this.durability = durability;
    }
    
    /**
     * ItemStack constructor.
     * @param stack The ItemStack to base the InventoryItem off of.
     */
    public InventoryItem(ItemStack stack)
    {
        if (stack == null)
            id = -1;
        
        id = stack.getTypeId();
        amount = stack.getAmount();
        
        // In case of "odd" items, don't attempt to get data and durability
        if (id < 0) return;
        
        data = stack.getData() == null ? null : stack.getData().getData();
        durability = stack.getDurability();
        
        // Enchantments
        enchantments = new HashMap<Integer, Integer>();
        Map<Enchantment, Integer> stack_ench = stack.getEnchantments();
        for(Enchantment ench : stack_ench.keySet()) {
        	enchantments.put(ench.getId(), stack_ench.get(ench));
        }
    }
    
    /**
     * Static method for turning a (possibly null) InventoryItem into an ItemStack.
     * The method is useful if it is unknown whether an InventoryItem object is
     * null or not.
     * @param item The Inventory item to convert
     * @return An ItemStack representation of the InventoryItem, or null
     */
    public static ItemStack toItemStack(InventoryItem item)
    {
        if (item == null)
            return null;
        
        return item.toItemStack();
    }
    
    public static ItemStack[] toItemStacks(InventoryItem[] items)
    {
        ItemStack[] result = new ItemStack[items.length];
        
        for (int i = 0; i < items.length; i++)
            result[i] = items[i].toItemStack();
        
        return result;
    }
    
    /**
     * Static method for converting an ItemStack to an InventoryItems.
     * @param stack The ItemStack to convert
     * @return An InventoryItem representation of the ItemStack, or null
     */
    public static InventoryItem parseItemStack(ItemStack stack)
    {
        if (stack == null)
            return new InventoryItem(-1, -1, null, (short) 0);
        
        return new InventoryItem(stack);
    }
    
    /**
     * Static method for converting an ItemStack array to an InventoryItem array.
     * @param stacks The ItemStack array
     * @return An InventoryItem array
     */
    public static InventoryItem[] parseItemStacks(ItemStack[] stacks)
    {
        InventoryItem[] items = new InventoryItem[stacks.length];
        
        for (int i = 0; i < items.length; i++)
            items[i] = parseItemStack(stacks[i]);
        
        return items;
    }
    
    /**
     * Convert a list of ItemStacks into an array of InventoryItems
     * @param stacks List of ItemStacks to convert
     * @return An InventoryItem array
     */
    public static InventoryItem[] parseItemStacks(List<ItemStack> stacks)
    {
        InventoryItem[] items = new InventoryItem[stacks.size()];
        
        for (int i = 0; i < items.length; i++)
            items[i] = parseItemStack(stacks.get(i));
        
        return items;
    }
    
    /**
     * Static method for extracting all InventoryItems from an InventoryItem array.
     * @param id The type id of the items to extract
     * @param items The InventoryItem array
     * @return A list of all InventoryItems removed
     */
    public static List<InventoryItem> extractAllFromArray(int id, InventoryItem[] items)
    {
        List<InventoryItem> list = new LinkedList<InventoryItem>();

        for (int i = 0; i < items.length; i++)
        {
            if (items[i].getTypeId() == id)
            {
                list.add(items[i]);
                items[i].setTypeId(-1);
            }
        }
        
        return list;
    }
    
    /**
     * Static method for removing an InventoryItem from an InventoryItem array.
     * @param item The InventoryItem to remove
     * @param items The InventoryItem array to remove from
     * @return true, if the item was removed successfully, false otherwise
     */
    public static boolean removeItemFromArray(InventoryItem item, InventoryItem[] items)
    {
        // Grab the total amount to remove
        int leftToRemove = item.getAmount();
        
        for (int i = 0; i < items.length; i++)
        {
            if (items[i].getTypeId() != item.getTypeId())
                continue;
            
            // Grab the amount
            int amount = items[i].getAmount();
            
            // Reduce amount/nullify item
            if (amount > leftToRemove)
            {
                items[i].setAmount(amount - leftToRemove);
                leftToRemove = 0;
            }
            else
            {
                items[i].setTypeId(-1);
                leftToRemove -= amount;
            }
            
            // If nothing left to remove, return true.
            if (leftToRemove == 0)
                return true;
        }
        
        return false;
    }

    /**
     * Get the data value/type id of the item
     * @return A type id
     */
    public int getTypeId()
    {
        return id;
    }
    
    /**
     * Set the data value/type id of the item
     * @param id A type id
     */
    public void setTypeId(int id)
    {
        this.id = id;
    }
    
    /**
     * Get the amount of the item
     * @return An amount
     */
    public int getAmount()
    {
        return amount;
    }
    
    /**
     * Set the amount of the item
     * @param amount An amount
     */
    public void setAmount(int amount)
    {
        this.amount = amount;
    }
    
    /**
     * Get the MaterialData of the item
     * @return A MaterialData
     */
    public MaterialData getData()
    {
        return new MaterialData(id, data == null ? (byte) 0 : data);
    }
    
    /**
     * Set the MaterialData of the item
     * @param data A MaterialData
     */
    public void setData(MaterialData data)
    {
        this.data = data.getData();
    }
    
    /**
     * Get the durability of the item
     * @return The item durability
     */
    public short getDurability()
    {
        return durability;
    }
    
    /**
     * Set the durability of the item
     * @param durability An item durability
     */
    public void setDurability(short durability)
    {
        this.durability = durability;
    }
    
    /**
     * Convert this InventoryItem to an ItemStack representation
     * @return An ItemStack representation of this InventoryItem, or null if the type id is -1
     */
    public ItemStack toItemStack()
    {
        if (id == -1)
            return null;
        
        ItemStack stack = new ItemStack(id, amount, durability);
        
        if (data != null)
            stack.setData(getData());
        
        for(int e : enchantments.keySet()) {
        	stack.addEnchantment(Enchantment.getById(e), enchantments.get(e));
        }
        
        return stack;
    }
}
