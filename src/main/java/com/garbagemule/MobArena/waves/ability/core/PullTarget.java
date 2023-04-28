package com.garbagemule.MobArena.waves.ability.core;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.MABoss;
import com.garbagemule.MobArena.waves.ability.Ability;
import com.garbagemule.MobArena.waves.ability.AbilityInfo;
import com.garbagemule.MobArena.waves.ability.AbilityUtils;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

@AbilityInfo(
    name = "Pull Target",
    aliases = {"pulltarget"}
)
public class PullTarget implements Ability
{
    /**
     * If the boss has no target, should a random player be selected?
     */
    private static final boolean RANDOM = false;

    @Override
    public void execute(Arena arena, MABoss boss) {
        LivingEntity target = AbilityUtils.getTarget(arena, boss.getEntity(), RANDOM);
        if (target == null) return;

        Location destination = boss.getEntity().getLocation();
        Tosser.yoink(target, destination);
    }
}
