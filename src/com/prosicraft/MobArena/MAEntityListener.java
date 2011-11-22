package com.prosicraft.MobArena;

import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EndermanPickupEvent;
import org.bukkit.event.entity.EndermanPlaceEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityTargetEvent;

import com.prosicraft.MobArena.Arena;
import com.prosicraft.MobArena.ArenaMaster;

public class MAEntityListener extends EntityListener
{
    private ArenaMaster am;
    
    public MAEntityListener(ArenaMaster am)
    {
        this.am = am;
    }
    
    public void onEntityRegainHealth(EntityRegainHealthEvent event)
    {
        for (Arena arena : am.arenas)
            arena.eventListener.onEntityRegainHealth(event);
    }
    
    public void onEntityDeath(EntityDeathEvent event)
    {
        for (Arena arena : am.arenas)
            arena.eventListener.onEntityDeath(event);
    }
    
    public void onEntityDamage(EntityDamageEvent event)
    {
        for (Arena arena : am.arenas)
            arena.eventListener.onEntityDamage(event);
    }

    public void onCreatureSpawn(CreatureSpawnEvent event)
    {
        for (Arena arena : am.arenas)
            arena.eventListener.onCreatureSpawn(event);
    }

    public void onEntityExplode(EntityExplodeEvent event)
    {
        for (Arena arena : am.arenas)
            arena.eventListener.onEntityExplode(event);
    }

    public void onEntityCombust(EntityCombustEvent event)
    {
        for (Arena arena : am.arenas)
            arena.eventListener.onEntityCombust(event);
    }
    
    public void onEntityTarget(EntityTargetEvent event)
    {
        for (Arena arena : am.arenas)
            arena.eventListener.onEntityTarget(event);
    }
    
    public void onEndermanPickup(EndermanPickupEvent event)
    {
        for (Arena arena : am.arenas)
            arena.eventListener.onEndermanPickup(event);
    }
    
    public void onEndermanPickup(EndermanPlaceEvent event)
    {
        for (Arena arena : am.arenas)
            arena.eventListener.onEndermanPlace(event);
    }
}