package com.garbagemule.MobArena;

import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class MABlockListener extends BlockListener
{
    private ArenaMaster am;
    
    public MABlockListener(ArenaMaster am)
    {
        this.am = am;
    }

    public void onBlockBreak(BlockBreakEvent event)
    {
        for (Arena arena : am.arenas)
            arena.onBlockBreak(event);
    }

    public void onBlockPlace(BlockPlaceEvent event)
    {
        for (Arena arena : am.arenas)
            arena.onBlockPlace(event);
    }
}