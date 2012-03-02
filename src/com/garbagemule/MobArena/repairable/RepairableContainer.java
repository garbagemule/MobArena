package com.garbagemule.MobArena.repairable;

import org.bukkit.block.BlockState;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import com.garbagemule.MobArena.util.inventory.SerializableInventory;

public class RepairableContainer extends RepairableBlock
{
    private SerializableInventory inv;
    
    public RepairableContainer(BlockState state, boolean clear) {
        super(state);

        // Grab the inventory of the block
        Inventory inv = ((InventoryHolder) state).getInventory();
        
        // Make a SerializableInventory
        this.inv = new SerializableInventory(inv);
        
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
        
        SerializableInventory.loadContents(chestInv, this.inv);
    }
}
