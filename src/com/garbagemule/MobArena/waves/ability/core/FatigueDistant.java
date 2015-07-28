package com.garbagemule.MobArena.waves.ability.core;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.MABoss;
import com.garbagemule.MobArena.waves.ability.Ability;
import com.garbagemule.MobArena.waves.ability.AbilityInfo;
import com.garbagemule.MobArena.waves.ability.AbilityUtils;

@AbilityInfo(name = "Fatigue Distant", aliases = { "fatiguedistant" })
public class FatigueDistant implements Ability {

    // how far players have to be to be targeted by this ability
    private final int RADIUS = 8;

    // duration to apply mining fatigue for, in ticks
    private final int DURATION = 60;

    // amplifier for the mining fatigue affect, 0 means level 1
    private final int AMPLIFIER = 0;

    @Override
    public void execute(Arena arena, MABoss boss) {
        for (Player e : AbilityUtils.getDistantPlayers(arena, boss.getEntity(), RADIUS)) {
            e.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, DURATION, AMPLIFIER));
        }
    }
}
