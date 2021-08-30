package com.garbagemule.MobArena.waves.ability.core;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.MABoss;
import com.garbagemule.MobArena.waves.ability.Ability;
import com.garbagemule.MobArena.waves.ability.AbilityInfo;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@AbilityInfo(
    name = "Throw All",
    aliases = {"throwall"}
)
public class ThrowAll implements Ability
{
    @Override
    public void execute(Arena arena, MABoss boss) {
        Location bLoc = boss.getEntity().getLocation();

        for (Player p : arena.getPlayersInArena()) {
            Location loc = p.getLocation();
            Vector v     = new Vector(loc.getX() - bLoc.getX(), 0, loc.getZ() - bLoc.getZ());
            p.setVelocity(v.normalize().setY(0.8));
        }
    }
}
