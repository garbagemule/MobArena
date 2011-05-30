package com.garbagemule.MobArena;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * This listener acts as a type of death-listener.
 * When a player is sufficiently low on health, and the next
 * damaging blow will kill them, they are teleported to the
 * spectator area, they have their hearts replenished, and all
 * their items are stripped from them.
 * By the end of the arena session, the rewards are given.
 */
// TO-DO: Perhaps implement TeamFluff's respawn-packet-code.
public class MADamageListener extends EntityListener
{
    private MobArena plugin;
    
    public MADamageListener(MobArena instance)
    {
        plugin = instance;
    }
    
    public void onEntityDamage(EntityDamageEvent event)
    {
        if (!ArenaManager.isRunning)
            return;
        
        if (!(event.getEntity() instanceof Player))
            return;
        
        Player p = (Player) event.getEntity();
        
        if (!ArenaManager.playerSet.contains(p))
            return;
        
        if (p.getHealth() > event.getDamage())
            return;
        
        event.setCancelled(true);
        ArenaManager.playerDeath(p);
    }
}