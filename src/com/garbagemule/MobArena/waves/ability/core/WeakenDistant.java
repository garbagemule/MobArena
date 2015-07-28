package com.garbagemule.MobArena.waves.ability.core;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.MABoss;
import com.garbagemule.MobArena.waves.ability.Ability;
import com.garbagemule.MobArena.waves.ability.AbilityInfo;
import com.garbagemule.MobArena.waves.ability.AbilityUtils;

@AbilityInfo(name = "Weaken Distant", aliases = { "weakendistant" })
public class WeakenDistant implements Ability {

    // how far players need to be to be targeted by the ability
    private final int RADIUS = 8;

    // how long to weaken players for, in ticks
    private final int DURATION = 60;

    // amplifier for the weakness affect, 0 means level 1
    private final int AMPLIFIER = 0;

    @Override
    public void execute(Arena arena, MABoss boss) {
        for (Player e : AbilityUtils.getDistantPlayers(arena, boss.getEntity(), RADIUS)) {
            e.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, DURATION, AMPLIFIER));
        }
    }
}
