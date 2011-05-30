package com.garbagemule.MobArena;

import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityExplodeEvent;

/**
 * Very simple listener for preventing Creeper explosions from
 * damaging the blocks of the arena.
 */
public class MACreeperListener extends EntityListener
{
    private MobArena plugin;
    
    public MACreeperListener(MobArena instance)
    {
        plugin = instance;
    }
    
    public void onEntityExplode(EntityExplodeEvent event)
    {
        if (MAUtils.inRegion(event.getLocation()))
            event.setCancelled(true);
    }
    public void onEntityCombust(EntityCombustEvent event) 
    {
        if (MAUtils.inRegion(event.getLocation()))
            event.setCancelled(true);

    }
}