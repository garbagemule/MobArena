package com.garbagemule.MobArena.repairable;

import org.bukkit.Material;
import org.bukkit.block.data.Attachable;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.RedstoneWire;

import java.util.Comparator;

public class RepairableComparator implements Comparator<Repairable>
{    
    public int compare(Repairable r1, Repairable r2)
    {
        if (restoreLast(r1))
        {
            if (restoreLast(r2))
                return 0;
            return 1;
        }
        else if (restoreLast(r2))
            return -1;
        
        return 0;
    }
    
    private boolean restoreLast(Repairable r)
    {
        Material t = r.getType();
        BlockData data = r.getData();
        
        return (data instanceof Attachable || data instanceof RedstoneWire || data instanceof Door || data instanceof Bed || t == Material.LAVA || t == Material.WATER || t == Material.FIRE);
    }
}
