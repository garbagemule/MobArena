package com.garbagemule.MobArena.repairable;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.material.Door;

public class RepairableDoor extends RepairableAttachable//RepairableBlock
{
    private BlockState other;
    private int x, y, z;

    public RepairableDoor(BlockState state)
    {
        super(state);
        other = state.getBlock().getRelative(BlockFace.UP).getState();

        BlockState attached = state.getBlock().getRelative(BlockFace.DOWN).getState();
        x = attached.getX();
        y = attached.getY();
        z = attached.getZ();
    }

    public void repair()
    {
        if (getWorld().getBlockAt(getX(), getY(), getZ()).getState().getData() instanceof Door)
            return;

        Block b = getWorld().getBlockAt(x,y,z);
        if (b.getType() == Material.AIR)
            b.setType(Material.STONE);

        super.repair();
        other.getBlock().setBlockData(other.getBlockData());
    }
}
