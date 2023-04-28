package com.garbagemule.MobArena.waves.ability.core;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.MABoss;
import com.garbagemule.MobArena.waves.ability.Ability;
import com.garbagemule.MobArena.waves.ability.AbilityInfo;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@AbilityInfo(
    name = "Throw All",
    aliases = {"throwall"}
)
public class ThrowAll implements Ability
{
    @Override
    public void execute(Arena arena, MABoss boss) {
        Location origin = boss.getEntity().getLocation();

        for (Player p : arena.getPlayersInArena()) {
            Tosser.yeet(p, origin);
        }
    }
}
