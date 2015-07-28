package com.garbagemule.MobArena.waves.ability.core;

import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.MABoss;
import com.garbagemule.MobArena.waves.ability.Ability;
import com.garbagemule.MobArena.waves.ability.AbilityInfo;
import com.garbagemule.MobArena.waves.ability.AbilityUtils;

@AbilityInfo(name = "Slow Target", aliases = { "slowtarget" })
public class SlowTarget implements Ability {

    // how long to slow the target for, in ticks
    public static final int DURATION = 60;

    // amplifier for the slowness affect, 0 means level 1
    public static final int AMPLIFIER = 0;

    // should a random player be selected if no target is found?
    public static final boolean RANDOM = false;

    @Override
    public void execute(Arena arena, MABoss boss) {
        LivingEntity target = AbilityUtils.getTarget(arena, boss.getEntity(), RANDOM);
        if (target == null) {
            // don't slow null
            return;
        }
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, DURATION, AMPLIFIER));
    }
}
