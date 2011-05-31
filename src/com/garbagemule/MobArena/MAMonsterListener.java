package com.garbagemule.MobArena;

import java.util.HashMap;
import org.bukkit.block.Block;
import org.bukkit.entity.Spider;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;

/**
 * Prevents Creeper explosions from damaging the blocks of the
 * arena, zombies and skeletons from burning in the sun, and
 * monsters (mostly spiders) from losing their targets.
 */
public class MAMonsterListener extends EntityListener
{
    private MobArena plugin;
    
    public MAMonsterListener(MobArena instance)
    {
        plugin = instance;
    }
    
    // Creeper explosions
    public void onEntityExplode(EntityExplodeEvent event)
    {
        /* This could be done by simply cancelling the event, but that
         * also cancels the explosion animation. This is a workaround. */
        if (MAUtils.inRegion(event.getLocation()))
        {
            // Don't drop any blocks.
            event.setYield(0);
            
            // Store the blocks and their values.
            final HashMap<Block,Integer> blockMap = new HashMap<Block,Integer>();
            for (Block b : event.blockList())
            {
                blockMap.put(b, b.getTypeId());
            }
            
            // Wait a couple of ticks, then rebuild the blocks.
            ArenaManager.server.getScheduler().scheduleSyncDelayedTask(plugin,
                new Runnable()
                {
                    public void run()
                    {
                        for (Block b : blockMap.keySet())
                        {
                            b.getLocation().getBlock().setTypeId(blockMap.get(b));
                        }
                    }
                }, 3);
        }
    }
    
    // Zombie/skeleton combustion from the sun.
    public void onEntityCombust(EntityCombustEvent event)
    {
        if (ArenaManager.monsterSet.contains(event.getEntity()))
            event.setCancelled(true);
    }
    
    // Monsters losing their targets.
    public void onEntityTarget(EntityTargetEvent event)
    {
        if (!ArenaManager.isRunning)
            return;
        
        if (!ArenaManager.monsterSet.contains(event.getEntity()))
            return;
        
        if (event.getReason() == TargetReason.FORGOT_TARGET)
            event.setCancelled(true);
    }
}