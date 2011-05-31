package com.garbagemule.MobArena;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockDamageEvent;

/**
 * This listener serves as a protection class. Blocks within
 * the arena region cannot be destroyed, and blocks can only
 * be placed by a participant in the current arena session.
 * Any placed blocks will be removed by the cleanup method in
 * ArenaManager when the session ends.
 */
public class MABlockListener extends BlockListener
{
    private MobArena plugin;
    
    public MABlockListener(MobArena instance)
    {
        plugin = instance;
    }
    
    public void onBlockDamage(BlockDamageEvent event)
    {
        if (!ArenaManager.isSetup || !ArenaManager.isProtected)
            return;
        
        Block b = event.getBlock();
        
        if (ArenaManager.blockSet.contains(b))
            return;
        
        if (MAUtils.inRegion(b.getLocation()))
            event.setCancelled(true);
    }
    
    public void onBlockBreak(BlockBreakEvent event)
    {        
        if (!ArenaManager.isSetup || !ArenaManager.isProtected)
            return;
        
        Block b = event.getBlock();
        
        if (ArenaManager.blockSet.contains(b))
            return;
        
        if (MAUtils.inRegion(b.getLocation()))
            event.setCancelled(true);
    }
    
    public void onBlockPlace(BlockPlaceEvent event)
    {
        if (!ArenaManager.isSetup || !ArenaManager.isProtected)
            return;
        
        Block b = event.getBlock();
        
        if (MAUtils.inRegion(b.getLocation()))
        {
            if (ArenaManager.isRunning && ArenaManager.playerSet.contains(event.getPlayer()))
            {
                ArenaManager.blockSet.add(b);
                return;
            }

            event.setCancelled(true);
        }
    }
}