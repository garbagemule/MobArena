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
    name = "Throw Target",
    aliases = {"throwtarget"}
)
public class ThrowTarget implements Ability
{
    /**
     * If the boss has no target, should a random player be selected?
     */
    private final boolean RANDOM = false;
    
    @Override
    public void execute(Arena arena, MABoss boss) {
        LivingEntity target = AbilityUtils.getTarget(arena, boss.getEntity(), RANDOM);
        if (target == null) return;

        Location bLoc = boss.getEntity().getLocation();
        Location loc  = target.getLocation();
        Vector v      = new Vector(loc.getX() - bLoc.getX(), 0, loc.getZ() - bLoc.getZ());
        
        target.setVelocity(v.normalize().setY(0.8));
    }
}
