package com.garbagemule.MobArena.waves.ability.core;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.MABoss;
import com.garbagemule.MobArena.waves.ability.Ability;
import com.garbagemule.MobArena.waves.ability.AbilityInfo;
import com.garbagemule.MobArena.waves.ability.AbilityUtils;

@AbilityInfo(name = "Blind Nearby", aliases = { "blindnearby" })
public class BlindNearby implements Ability {

    // how close players have to be to the boss to be targeted by the ability
    private final int RADIUS = 5;

    // duration that players will be blinded for, in ticks
    private final int DURATION = 60;

    @Override
    public void execute(Arena arena, MABoss boss) {
        // get all nearby players
        for (Player e : AbilityUtils.getNearbyPlayers(arena, boss.getEntity(), RADIUS)) {
            // and blind them
            e.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, DURATION, 0));
        }
    }
}
