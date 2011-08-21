package com.garbagemule.MobArena.repairable;

import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;

public class RepairableSign extends RepairableAttachable
{
    private String[] lines = new String[4];
    
    public RepairableSign(BlockState state)
    {
        super(state);
        
        Sign s = (Sign) state;
        lines  = s.getLines();
    }

    /**
     * Repairs the sign block by restoring all the lines
     */
    public void repair()
    {
        super.repair();
        
        Sign s = (Sign) getWorld().getBlockAt(getX(),getY(),getZ()).getState();
        s.setLine(0, lines[0]);
        s.setLine(1, lines[1]);
        s.setLine(2, lines[2]);
        s.setLine(3, lines[3]);
    }

}
