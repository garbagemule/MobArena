package com.garbagemule.MobArena.waves.ability.core;

import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.MABoss;
import com.garbagemule.MobArena.waves.ability.Ability;
import com.garbagemule.MobArena.waves.ability.AbilityInfo;
import com.garbagemule.MobArena.waves.ability.AbilityUtils;

@AbilityInfo(name = "Blind Target", aliases = { "blindtarget" })
public class BlindTarget implements Ability {

    // duration that the target should be blinded for, in ticks
    private final int DURATION = 60;

    // should a random player be selected if no target is found?
    private final boolean RANDOM = false;

    @Override
    public void execute(Arena arena, MABoss boss) {
        // get a target
        LivingEntity target = AbilityUtils.getTarget(arena, boss.getEntity(), RANDOM);
        if (target == null) {
            // if boss is targeting null, abandon ship
            return;
        }

        // blind the target!
        target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, DURATION, 0));
    }
}
