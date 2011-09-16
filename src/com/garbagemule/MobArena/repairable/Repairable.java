package com.garbagemule.MobArena.repairable;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;

public interface Repairable
{
    public void repair();

    public BlockState getState();
    
    public Material getType();
    public int getId();
    public byte getData();
    
    public World getWorld();
    public int getX();
    public int getY();
    public int getZ();
}
