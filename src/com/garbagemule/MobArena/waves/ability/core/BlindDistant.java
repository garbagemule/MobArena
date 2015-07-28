package com.garbagemule.MobArena.waves.ability.core;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.MABoss;
import com.garbagemule.MobArena.waves.ability.Ability;
import com.garbagemule.MobArena.waves.ability.AbilityInfo;
import com.garbagemule.MobArena.waves.ability.AbilityUtils;

@AbilityInfo(name = "Blind Distant", aliases = { "blinddistant" })
public class BlindDistant implements Ability {

    // how far players have to be in order to be affected by the ability
    private final int RADIUS = 8;

    // duration to blind players for, in ticks
    private final int DURATION = 60;

    @Override
    public void execute(Arena arena, MABoss boss) {
        // get the players that are far enough from the boss
        for (Player e : AbilityUtils.getDistantPlayers(arena, boss.getEntity(), RADIUS)) {
            // and blind them
            e.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, DURATION, 0));
        }
    }
}
