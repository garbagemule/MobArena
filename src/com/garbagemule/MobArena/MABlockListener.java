package com.garbagemule.MobArena;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
//import org.bukkit.event.block.BlockDamageEvent;

/**
 * This listener serves as a protection class. Blocks within
 * the arena region cannot be destroyed, and blocks can only
 * be placed by a participant in the current arena session.
 * Any placed blocks will be removed by the cleanup method in
 * ArenaManager when the session ends.
 */
public class MABlockListener extends BlockListener
{    
    public MABlockListener(MobArena instance)
    {
    }

    /**
     * Prevents blocks from breaking if block protection is on.
     */
    public void onBlockBreak(BlockBreakEvent event)
    {
        if (!ArenaManager.isSetup || !ArenaManager.isProtected)
            return;
        
        Block b = event.getBlock();
        
        if (ArenaManager.blockSet.remove(b) || b.getType() == Material.TNT)
            return;
        
        if (MAUtils.inRegion(b.getLocation()))
            event.setCancelled(true);
    }
    
    /**
     * Adds player-placed blocks to a set for removal and item
     * drop purposes. If the block is placed within the arena
     * region, cancel the event if protection is on.  
     */
    public void onBlockPlace(BlockPlaceEvent event)
    {
        if (!ArenaManager.isSetup || !ArenaManager.isProtected)
            return;
        
        Block b = event.getBlock();
        
        if (!MAUtils.inRegion(b.getLocation()))
            return;
        
        if (ArenaManager.isRunning && ArenaManager.playerSet.contains(event.getPlayer()))
        {
            ArenaManager.blockSet.add(b);
            Material type = b.getType();
            
            // Make sure to add the top parts of doors.
            if (type == Material.WOODEN_DOOR || type == Material.IRON_DOOR_BLOCK)
                ArenaManager.blockSet.add(b.getRelative(0,1,0));
            
            return;
        }

        event.setCancelled(true);
    }
}