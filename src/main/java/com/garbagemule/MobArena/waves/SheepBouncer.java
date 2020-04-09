package com.garbagemule.MobArena.waves;

import com.garbagemule.MobArena.framework.Arena;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.Set;

public class SheepBouncer implements Runnable
{
    public static final int BOUNCE_INTERVAL = 20;
    private Arena arena;

    private BukkitTask task;
    
    public SheepBouncer(Arena arena) {
        this.arena = arena;
    }
    
    public void start() {
        if (task != null) {
            arena.getPlugin().getLogger().warning("Starting sheep bouncer in arena " + arena.configName() + " with existing bouncer still running. This should never happen.");
            task.cancel();
            task = null;
        }

        int delay = arena.getSettings().getInt("first-wave-delay", 5) * 20;
        task = Bukkit.getScheduler().runTaskLater(arena.getPlugin(), this, delay);
    }

    public void stop() {
        if (task == null) {
            arena.getPlugin().getLogger().warning("Can't stop non-existent sheep bouncer in arena " + arena.configName() + ". This should never happen.");
            return;
        }

        task.cancel();
        task = null;
    }

    @Override
    public void run() {
        // If the arena isn't running or has no players, bail out
        if (!arena.isRunning() || arena.getPlayersInArena().isEmpty()) {
            return;
        }
        
        // Put all the sheep in a new collection for iteration purposes.
        Set<LivingEntity> sheep = new HashSet<>(arena.getMonsterManager().getExplodingSheep());
        
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
        task = Bukkit.getScheduler().runTaskLater(arena.getPlugin(), this, BOUNCE_INTERVAL);
    }
}
