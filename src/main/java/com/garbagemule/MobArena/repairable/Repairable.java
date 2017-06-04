package com.garbagemule.MobArena.repairable;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;

public interface Repairable
{
    void repair();

    BlockState getState();
    
    Material getType();
    int getId();
    byte getData();
    
    World getWorld();
    int getX();
    int getY();
    int getZ();
}
