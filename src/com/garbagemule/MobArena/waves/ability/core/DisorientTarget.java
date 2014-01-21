package com.garbagemule.MobArena.waves.ability.core;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.MABoss;
import com.garbagemule.MobArena.waves.ability.Ability;
import com.garbagemule.MobArena.waves.ability.AbilityInfo;
import com.garbagemule.MobArena.waves.ability.AbilityUtils;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

@AbilityInfo(
    name = "Disorient Target",
    aliases = {"disorienttarget"}
)
public class DisorientTarget implements Ability
{
    /**
     * If the boss has no target, should a random player be selected?
     */
    private final boolean RANDOM = false;
    
    @Override
    public void execute(Arena arena, MABoss boss) {
        LivingEntity target = AbilityUtils.getTarget(arena, boss.getEntity(), RANDOM);
        if (target == null) return;
        
        Location loc = target.getLocation();
        loc.setYaw(loc.getYaw() + 45 + AbilityUtils.random.nextInt(270));
        target.teleport(loc);
    }
}
