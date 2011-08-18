package com.garbagemule.MobArena.repairable;

import org.bukkit.block.BlockState;
import org.bukkit.block.ContainerBlock;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.garbagemule.MobArena.util.InventoryItem;

public class RepairableContainer extends RepairableBlock
{
    private InventoryItem[] items;
    
    public RepairableContainer(BlockState state, boolean clear)
    {
        super(state);

        // Grab the inventory and its contents
        Inventory inv = ((ContainerBlock) state).getInventory();
        ItemStack[] contents = inv.getContents();
        
        // Initialize the items array
        items = new InventoryItem[contents.length];
        
        // Turn every ItemStack into an InventoryItem
        for (int i = 0; i < items.length; i++)
            items[i] = InventoryItem.parseItemStack(contents[i]);
        
        // Clear the inventory if prompted
        if (clear) inv.clear();
    }
    
    public RepairableContainer(BlockState state)
    {
        this(state, true);
    }
    
    /**
     * Repairs the container block by adding all the contents back in.
     */
    public void repair()
    {
        super.repair();
        
        // Grab the inventory
        ContainerBlock cb = (ContainerBlock) getWorld().getBlockAt(getX(),getY(),getZ()).getState();
        Inventory inv = cb.getInventory();
        
        // Turn every InventoryItem into an ItemStack
        for (int i = 0; i < items.length; i++)
        {
            InventoryItem item = items[i]; 
            inv.setItem(i, item != null ? item.toItemStack() : null);
        }
    }
}
