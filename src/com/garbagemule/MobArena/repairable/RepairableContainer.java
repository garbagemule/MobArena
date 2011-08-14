package com.garbagemule.MobArena.repairable;

import org.bukkit.block.BlockState;
import org.bukkit.block.ContainerBlock;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class RepairableContainer extends RepairableBlock
{
    private ItemStack[] contents;
    
    public RepairableContainer(BlockState state)
    {
        super(state);
        
        Inventory inv = ((ContainerBlock) state).getInventory();
        contents = inv.getContents();
        inv.clear();
    }
    
    /**
     * Repairs the container block by adding all the contents back in.
     */
    public void repair()
    {
        super.repair();
        
        ContainerBlock cb = (ContainerBlock) getWorld().getBlockAt(getX(),getY(),getZ()).getState();
        cb.getInventory().setContents(contents);
    }
}
