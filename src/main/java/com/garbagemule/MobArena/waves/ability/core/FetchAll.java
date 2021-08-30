package com.garbagemule.MobArena.waves.ability.core;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.MABoss;
import com.garbagemule.MobArena.waves.ability.Ability;
import com.garbagemule.MobArena.waves.ability.AbilityInfo;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@AbilityInfo(
    name = "Fetch All",
    aliases = {"fetchall"}
)
public class FetchAll implements Ability
{
    @Override
    public void execute(Arena arena, MABoss boss) {
        Location bLoc = boss.getEntity().getLocation();

        for (Player p : arena.getPlayersInArena()) {
            p.teleport(bLoc);
        }
    }
}
