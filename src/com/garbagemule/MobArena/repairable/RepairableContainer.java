package com.garbagemule.MobArena.repairable;

import org.bukkit.block.BlockState;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import org.bukkit.inventory.ItemStack;

public class RepairableContainer extends RepairableBlock
{
    private ItemStack[] contents;
    
    public RepairableContainer(BlockState state, boolean clear) {
        super(state);

        // Grab the inventory of the block
        Inventory inv = ((InventoryHolder) state).getInventory();
        contents = inv.getContents();
        
        // Clear the inventory if prompted
        if (clear) inv.clear();
    }
    
    public RepairableContainer(BlockState state) {
        this(state, true);
    }
    
    /**
     * Repairs the container block by adding all the contents back in.
     */
    public void repair() {
        super.repair();
        
        // Grab the inventory
        InventoryHolder cb = (InventoryHolder) getWorld().getBlockAt(getX(),getY(),getZ()).getState();
        Inventory chestInv = cb.getInventory();

        chestInv.setContents(contents);
    }
}
