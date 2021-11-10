package com.garbagemule.MobArena.waves.ability.core;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.MABoss;
import com.garbagemule.MobArena.waves.ability.Ability;
import com.garbagemule.MobArena.waves.ability.AbilityInfo;
import com.garbagemule.MobArena.waves.ability.AbilityUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@AbilityInfo(
    name = "Disorient All",
    aliases = {"disorientall"}
)
public class DisorientAll implements Ability
{
    @Override
    public void execute(Arena arena, MABoss boss) {
        arena.getPlayersInArena().forEach(player -> {
            Location loc = player.getLocation();
            loc.setYaw(loc.getYaw() + 45 + AbilityUtils.random.nextInt(270));
            player.teleport(loc);
        });
    }
}
