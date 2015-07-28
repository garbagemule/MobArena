package com.garbagemule.MobArena.waves.ability.core;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.MABoss;
import com.garbagemule.MobArena.waves.ability.Ability;
import com.garbagemule.MobArena.waves.ability.AbilityInfo;
import com.garbagemule.MobArena.waves.ability.AbilityUtils;

@AbilityInfo(name = "Confuse Nearby", aliases = { "confusenearby" })
public class ConfuseNearby implements Ability {

    // how close player need to be to be targeted
    private final int RADIUS = 5;

    // how long players should get nausea for, in ticks
    private final int DURATION = 120;

    @Override
    public void execute(Arena arena, MABoss boss) {
        // get the nearby players and apply nausea
        for (Player e : AbilityUtils.getNearbyPlayers(arena, boss.getEntity(), RADIUS)) {
            e.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, DURATION, 0));
        }
    }
}
