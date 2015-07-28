package com.garbagemule.MobArena.waves.ability.core;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.MABoss;
import com.garbagemule.MobArena.waves.ability.Ability;
import com.garbagemule.MobArena.waves.ability.AbilityInfo;
import com.garbagemule.MobArena.waves.ability.AbilityUtils;

@AbilityInfo(name = "Confuse Distant", aliases = { "confusedistant" })
public class ConfuseDistant implements Ability {

    // how far players need to be to be targeted by the ability
    private final int RADIUS = 8;

    // the duration to apply nausea for, in ticks
    private final int DURATION = 120;

    @Override
    public void execute(Arena arena, MABoss boss) {
        // find the distant players
        for (Player e : AbilityUtils.getDistantPlayers(arena, boss.getEntity(), RADIUS)) {
            // play with their display
            e.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, DURATION, 0));
        }
    }
}
