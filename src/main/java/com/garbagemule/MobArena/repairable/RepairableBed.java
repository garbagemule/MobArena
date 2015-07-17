package com.garbagemule.MobArena.repairable;

import org.bukkit.block.BlockState;
import org.bukkit.material.Bed;

public class RepairableBed extends RepairableBlock
{
    private BlockState other;
    
    public RepairableBed(BlockState state)
    {
        super(state);        
        other = state.getBlock().getRelative(((Bed) state.getData()).getFacing()).getState();
    }
    
    public void repair()
    {
        if (getWorld().getBlockAt(getX(), getY(), getZ()).getState().getData() instanceof Bed)
            return;
        
        super.repair();
        other.getBlock().setTypeIdAndData(getId(), (byte) (getData() + 8), false);
    }
}
