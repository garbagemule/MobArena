package com.garbagemule.MobArena.repairable;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;

public interface Repairable
{
    void repair();

    BlockState getState();

    Material getType();
    BlockData getData();

    World getWorld();
    int getX();
    int getY();
    int getZ();
}
