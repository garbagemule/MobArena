package com.garbagemule.MobArena.waves.ability.core;

import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.MABoss;
import com.garbagemule.MobArena.waves.ability.Ability;
import com.garbagemule.MobArena.waves.ability.AbilityInfo;
import com.garbagemule.MobArena.waves.ability.AbilityUtils;

@AbilityInfo(name = "Weaken Target", aliases = { "weakentarget" })
public class WeakenTarget implements Ability {

    // how long to weaken the target for, in ticks
    private final int DURATION = 60;

    // the amplifier for the weakness affect, 0 means level 1
    private final int AMPLIFIER = 0;

    // should a random player be selected if no target is found?
    private final boolean RANDOM = false;

    @Override
    public void execute(Arena arena, MABoss boss) {
        LivingEntity target = AbilityUtils.getTarget(arena, boss.getEntity(), RANDOM);
        if (target == null) {
            return;
        }
        target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, DURATION, AMPLIFIER));
    }
}
