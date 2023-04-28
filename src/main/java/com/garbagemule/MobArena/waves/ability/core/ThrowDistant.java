package com.garbagemule.MobArena.waves.ability.core;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.MABoss;
import com.garbagemule.MobArena.waves.ability.Ability;
import com.garbagemule.MobArena.waves.ability.AbilityInfo;
import com.garbagemule.MobArena.waves.ability.AbilityUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@AbilityInfo(
    name = "Throw Distant",
    aliases = {"throwdistant"}
)
public class ThrowDistant implements Ability
{
    /**
     * How far away players must be to be affected by the ability.
     */
    private static final int RADIUS = 8;

    @Override
    public void execute(Arena arena, MABoss boss) {
        Location origin = boss.getEntity().getLocation();

        for (Player p : AbilityUtils.getDistantPlayers(arena, boss.getEntity(), RADIUS)) {
            Tosser.yeet(p, origin);
        }
    }
}
