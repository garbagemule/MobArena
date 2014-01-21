package com.garbagemule.MobArena.waves.ability.core;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.MABoss;
import com.garbagemule.MobArena.waves.ability.Ability;
import com.garbagemule.MobArena.waves.ability.AbilityInfo;
import com.garbagemule.MobArena.waves.ability.AbilityUtils;
import org.bukkit.World;
import org.bukkit.entity.Player;

@AbilityInfo(
    name = "Lightning Aura",
    aliases = {"lightningaura","auraoflightning"}
)
public class LightningAura implements Ability
{
    /**
     * How close players must be to be affected by the ability.
     */
    private final int RADIUS = 5;
    
    @Override
    public void execute(Arena arena, MABoss boss) {
        World world = arena.getWorld();
        for (Player p : AbilityUtils.getNearbyPlayers(arena, boss.getEntity(), RADIUS)) {
            world.strikeLightning(p.getLocation());
        }
    }
}
