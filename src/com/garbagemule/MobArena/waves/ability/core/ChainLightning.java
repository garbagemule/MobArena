package com.garbagemule.MobArena.waves.ability.core;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.MABoss;
import com.garbagemule.MobArena.waves.ability.Ability;
import com.garbagemule.MobArena.waves.ability.AbilityInfo;
import com.garbagemule.MobArena.waves.ability.AbilityUtils;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@AbilityInfo(
    name = "Chain Lightning",
    aliases = {"chainlightning"}
)
public class ChainLightning implements Ability
{
    /**
     * How many blocks the chain lightning can spread over. 
     * Must be greater than 0.
     */
    private final int RADIUS = 4;
    
    /**
     * How many server ticks between each lightning strike.
     * Must be greater than 0.
     */
    private final int TICKS = 10;
    
    @Override
    public void execute(Arena arena, MABoss boss) {
        final LivingEntity target = AbilityUtils.getTarget(arena, boss.getEntity(), true);
        if (target == null || !(target instanceof Player))
            return;
        
        strikeLightning(arena, (Player) target, new ArrayList<Player>());
    }
    
    private void strikeLightning(final Arena arena, final Player p, final List<Player> done) {
        arena.scheduleTask(new Runnable() {
            public void run() {
                if (!arena.isRunning() || !arena.inArena(p))
                    return;
                
                // Smite the target
                arena.getWorld().strikeLightning(p.getLocation());
                done.add(p);
                
                // Grab all nearby players
                List<Player> nearby = AbilityUtils.getNearbyPlayers(arena, p, RADIUS);
                
                // Remove all that are "done", and return if empty
                nearby.removeAll(done);
                if (nearby.isEmpty()) return;
                
                // Otherwise, smite the next target!
                strikeLightning(arena, nearby.get(0), done);
            }
        }, TICKS);
    }
}
