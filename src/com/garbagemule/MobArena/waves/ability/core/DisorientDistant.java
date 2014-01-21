package com.garbagemule.MobArena.waves.ability.core;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.MABoss;
import com.garbagemule.MobArena.waves.ability.Ability;
import com.garbagemule.MobArena.waves.ability.AbilityInfo;
import com.garbagemule.MobArena.waves.ability.AbilityUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@AbilityInfo(
    name = "Disorient Distant",
    aliases = {"disorientdistant"}
)
public class DisorientDistant implements Ability
{
    /**
     * How far away players must be to be affected by the ability.
     */
    private final int RADIUS = 8;
    
    @Override
    public void execute(Arena arena, MABoss boss) {
        for (Player p : AbilityUtils.getDistantPlayers(arena, boss.getEntity(), RADIUS)) {
            Location loc = p.getLocation();
            loc.setYaw(loc.getYaw() + 45 + AbilityUtils.random.nextInt(270));
            p.teleport(loc);
        }
    }
}
