package com.garbagemule.MobArena.repairable;

import java.util.Comparator;

import org.bukkit.material.Attachable;
import org.bukkit.material.Door;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Redstone;

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
        MaterialData m = r.getState().getData();
        return (m instanceof Attachable || m instanceof Redstone || m instanceof Door);
    }
}
