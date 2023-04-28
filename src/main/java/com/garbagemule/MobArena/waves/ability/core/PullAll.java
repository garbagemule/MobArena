package com.garbagemule.MobArena.waves.ability.core;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.MABoss;
import com.garbagemule.MobArena.waves.ability.Ability;
import com.garbagemule.MobArena.waves.ability.AbilityInfo;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@AbilityInfo(
    name = "Pull All",
    aliases = {"pullall"}
)
public class PullAll implements Ability
{
    @Override
    public void execute(Arena arena, MABoss boss) {
        Location destination = boss.getEntity().getLocation();

        for (Player p : arena.getPlayersInArena()) {
            Tosser.yoink(p, destination);
        }
    }
}
