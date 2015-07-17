package com.garbagemule.MobArena.waves;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.garbagemule.MobArena.framework.Arena;

public class SheepBouncer implements Runnable
{
    public static final int BOUNCE_INTERVAL = 20;
    private Arena arena;
    private Set<LivingEntity> sheep;
    
    public SheepBouncer(Arena arena) {
        this.arena = arena;
    }
    
    @Override
    public void run() {
        // If the arena isn't running or has no players, bail out
        if (!arena.isRunning() || arena.getPlayersInArena().isEmpty()) {
            return;
        }
        
        // Put all the sheep in a new collection for iteration purposes.
        sheep = new HashSet<LivingEntity>(arena.getMonsterManager().getExplodingSheep());
        
        // If there are no sheep, reschedule and return.
        if (sheep.isEmpty()) {
            arena.scheduleTask(this, BOUNCE_INTERVAL);
            return;
        }
        
        for (LivingEntity e : sheep) {
            // If an entity is null just ignore it.
            if (e == null) {
                continue;
            }
            
            // If the sheep is dead, remove it.
            if (e.isDead()) {
                arena.getMonsterManager().removeMonster(e);
                arena.getMonsterManager().removeExplodingSheep(e);
                continue;
            }
            
            // Create an explosion if there's a player amongst the nearby entities.
            for (Entity entity : e.getNearbyEntities(2D, 2D, 2D)) {
                if (entity instanceof Player) {
                    e.getWorld().createExplosion(e.getLocation(), 2f);
                    e.remove();
                    
                    break;
                }
            }
            
            // Otherwise, if it's not already bouncing, BOUNCE!
            if (Math.abs(e.getVelocity().getY()) < 1)
                e.setVelocity(e.getVelocity().setY(0.5));
        }
        
        // Reschedule for more bouncy madness!
        arena.scheduleTask(this, BOUNCE_INTERVAL);
    }
    
    /*private boolean isTargetNearby(Creature c, LivingEntity t) {
        // Null or dead, return false
        if (t == null || c == null || c.isDead()) {
            return false;
        }
        
        // If the subjects are in different worlds, return false.
        if (c.getWorld() != t.getWorld()) {
            return false;
        }
        
        // If distance is more than 3 blocks, not close enough.
        if (c.getLocation().distanceSquared(t.getLocation()) > 8D) {
            return false;
        }
        
        return true;
    }*/
}
