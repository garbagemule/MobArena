package com.garbagemule.MobArena.waves.ability.core;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.MABoss;
import com.garbagemule.MobArena.waves.ability.Ability;
import com.garbagemule.MobArena.waves.ability.AbilityInfo;
import com.garbagemule.MobArena.waves.ability.AbilityUtils;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

@AbilityInfo(
    name = "Pull Target",
    aliases = {"pulltarget"}
)
public class PullTarget implements Ability
{
    /**
     * If the boss has no target, should a random player be selected?
     */
    private final boolean RANDOM = false;
    
    @Override
    public void execute(Arena arena, MABoss boss) {
        LivingEntity target = AbilityUtils.getTarget(arena, boss.getEntity(), RANDOM);
        if (target == null) return;
        
        Location loc  = target.getLocation();
        Location bLoc = boss.getEntity().getLocation();
        Vector v      = new Vector(bLoc.getX() - loc.getX(), 0, bLoc.getZ() - loc.getZ());
        
        double a = Math.abs(bLoc.getX() - loc.getX());
        double b = Math.abs(bLoc.getZ() - loc.getZ());
        double c = Math.sqrt((a*a + b*b));
        
        target.setVelocity(v.normalize().multiply(c*0.3).setY(0.8));
    }
}
