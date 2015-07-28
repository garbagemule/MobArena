package com.garbagemule.MobArena.waves.ability.core;

import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.MABoss;
import com.garbagemule.MobArena.waves.ability.Ability;
import com.garbagemule.MobArena.waves.ability.AbilityInfo;
import com.garbagemule.MobArena.waves.ability.AbilityUtils;

@AbilityInfo(name = "Fatigue Target", aliases = { "fatiguetarget" })
public class FatigueTarget implements Ability {

    // how long to apply mining fatigue for, in ticks
    private final int DURATION = 60;

    // the amplifier for the mining fatigue affect, 0 means level 1
    private final int AMPLIFIER = 0;

    // should a random player be used if a target isn't found?
    private final boolean RANDOM = false;

    @Override
    public void execute(Arena arena, MABoss boss) {
        LivingEntity target = AbilityUtils.getTarget(arena, boss.getEntity(), RANDOM);
        if (target == null) {
            return;
        }
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, DURATION, AMPLIFIER));
    }
}
