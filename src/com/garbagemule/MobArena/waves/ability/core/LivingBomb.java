package com.garbagemule.MobArena.waves.ability.core;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.MABoss;
import com.garbagemule.MobArena.waves.ability.Ability;
import com.garbagemule.MobArena.waves.ability.AbilityInfo;
import com.garbagemule.MobArena.waves.ability.AbilityUtils;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

@AbilityInfo(
    name = "Living Bomb",
    aliases = {"livingbomb"}
)
public class LivingBomb implements Ability
{
    /**
     * How many ticks before the bomb goes off.
     */
    private final int FUSE = 60;
    
    /**
     * How close players must be to be affected by the bomb.
     */
    private final int RADIUS = 3;
    
    /**
     * How many ticks players affected by the bomb should burn.
     */
    private final int AFTERBURN = 40;
    
    @Override
    public void execute(final Arena arena, MABoss boss) {
        // Grab the target, or a random player.
        LivingEntity target = AbilityUtils.getTarget(arena, boss.getEntity(), true);
        
        // We only want players.
        if (target == null || !(target instanceof Player))
            return;

        final Player p = (Player) target;
        p.setFireTicks(FUSE + 5);
        
        // Create an explosion after 4 seconds
        arena.scheduleTask(new Runnable() {
            public void run() {
                // If the player died, or if they put out the fire.
                if (!arena.isRunning() || !arena.inArena(p) || p.getFireTicks() <= 0) {
                    return;
                }
                
                // Explode!
                arena.getWorld().createExplosion(p.getLocation(), 1F);
                
                // And set every nearby player on fire!
                for (Player nearby : AbilityUtils.getNearbyPlayers(arena, p, RADIUS)) {
                    nearby.setFireTicks(AFTERBURN);
                }
            }
        }, FUSE);
    }
}
