package com.garbagemule.MobArena.repairable;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.material.Attachable;

public class RepairableAttachable extends RepairableBlock
{
    private int x, y, z;
    
    public RepairableAttachable(BlockState state)
    {
        super(state);
        
        BlockState attached;
        if (state.getData() instanceof Attachable)
            attached = state.getBlock().getRelative(((Attachable) state.getData()).getAttachedFace()).getState();
        else
            attached = state.getBlock().getRelative(BlockFace.DOWN).getState();
        
        x = attached.getX();
        y = attached.getY();
        z = attached.getZ();
        
        state.getBlock().setTypeId(1);
    }

    public void repair()
    {
        Block b = getWorld().getBlockAt(x,y,z);
        if (b.getTypeId() == 0)
            b.setTypeId(1);
        
        super.repair();
    }
}
