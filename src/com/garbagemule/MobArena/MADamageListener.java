package com.garbagemule.MobArena;

import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityDeathEvent;

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
    public MADamageListener(MobArena instance)
    {
    }
    
    /**
     * Clears all player/monster drops on death. Also handles
     * player death events.
     */
    public void onEntityDeath(EntityDeathEvent event)
    {        
        // If player, call player death and such.
        if (event.getEntity() instanceof Player)
        {        
            Player p = (Player) event.getEntity();
            
            if (!ArenaManager.playerSet.contains(p))
                return;
            
            event.getDrops().clear();
            ArenaManager.playerDeath(p);
        }
        // If monster, remove from monster set
        else if (event.getEntity() instanceof LivingEntity)
        {
            LivingEntity e = (LivingEntity) event.getEntity();
            
            if (!ArenaManager.monsterSet.contains(e))
                return;
            
            event.getDrops().clear();
            ArenaManager.monsterSet.remove(e);
        }
    }
    
}