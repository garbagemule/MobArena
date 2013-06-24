package com.garbagemule.MobArena.waves.ability.core;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.MABoss;
import com.garbagemule.MobArena.waves.ability.Ability;
import com.garbagemule.MobArena.waves.ability.AbilityInfo;
import com.garbagemule.MobArena.waves.ability.AbilityUtils;
import org.bukkit.entity.LivingEntity;

@AbilityInfo(
    name = "Fetch Target",
    aliases = {"fetchtarget"}
)
public class FetchTarget implements Ability
{
    /**
     * If the boss has no target, should a random player be selected?
     */
    private final boolean RANDOM = true;
    
    @Override
    public void execute(Arena arena, MABoss boss) {
        LivingEntity target = AbilityUtils.getTarget(arena, boss.getEntity(), RANDOM);
        if (target == null) return;
        
        target.teleport(boss.getEntity());
    }
}
