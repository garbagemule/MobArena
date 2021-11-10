package com.garbagemule.MobArena.repairable;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;

public class RepairableBlock implements Repairable {
    private final BlockState state;
    private final World world;
    private final BlockData data;
    private final int x;
    private final int y;
    private final int z;
    private final Material type;

    public RepairableBlock(BlockState state) {
        this.state = state;

        world = state.getWorld();

        x = state.getX();
        y = state.getY();
        z = state.getZ();

        data = state.getBlockData();
        type = state.getType();
    }

    /**
     * Repairs the block by setting the type and data
     */
    public void repair()
    {
        world.getBlockAt(x,y,z).setBlockData(data);
    }

    public BlockState getState()
    {
        return state;
    }

    public World getWorld()
    {
        return world;
    }

    public Material getType()
    {
        return type;
    }

    public BlockData getData()
    {
        return data;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public int getZ()
    {
        return z;
    }
}
