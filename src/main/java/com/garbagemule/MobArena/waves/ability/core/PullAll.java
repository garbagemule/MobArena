package com.garbagemule.MobArena.waves.ability.core;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.MABoss;
import com.garbagemule.MobArena.waves.ability.Ability;
import com.garbagemule.MobArena.waves.ability.AbilityInfo;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@AbilityInfo(
    name = "Pull All",
    aliases = {"pullall"}
)
public class PullAll implements Ability
{
    @Override
    public void execute(Arena arena, MABoss boss) {
        Location bLoc = boss.getEntity().getLocation();

        for (Player p : arena.getPlayersInArena()) {
            Location loc = p.getLocation();
            Vector v     = new Vector(bLoc.getX() - loc.getX(), 0, bLoc.getZ() - loc.getZ());

            double a = Math.abs(bLoc.getX() - loc.getX());
            double b = Math.abs(bLoc.getZ() - loc.getZ());
            double c = Math.sqrt((a*a + b*b));

            p.setVelocity(v.normalize().multiply(c*0.3).setY(0.8));
        }
    }
}
