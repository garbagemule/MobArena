package com.garbagemule.MobArena;

import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
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
            arena.eventListener.onBlockBreak(event);
    }
    
    public void onBlockBurn(BlockBurnEvent event)
    {
        for (Arena arena : am.arenas)
            arena.eventListener.onBlockBurn(event);
    }

    public void onBlockPlace(BlockPlaceEvent event)
    {
        for (Arena arena : am.arenas)
            arena.eventListener.onBlockPlace(event);
    }
    
    public void onBlockIgnite(BlockIgniteEvent event)
    {
        for (Arena arena : am.arenas)
            arena.eventListener.onBlockIgnite(event);
    }
    
    /*
    public void onBlockPhysics(BlockPhysicsEvent event)
    {
        for (Arena arena : am.arenas)
            arena.eventListener.onBlockPhysics(event);
    }
    */
}