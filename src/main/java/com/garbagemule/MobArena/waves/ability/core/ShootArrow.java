package com.garbagemule.MobArena.waves.ability.core;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.MABoss;
import com.garbagemule.MobArena.waves.ability.Ability;
import com.garbagemule.MobArena.waves.ability.AbilityInfo;
import org.bukkit.entity.Arrow;

@AbilityInfo(
    name = "Shoot Arrow",
    aliases = {"arrow","arrows"}
)
public class ShootArrow implements Ability
{
    @Override
    public void execute(Arena arena, MABoss boss) {
        boss.getEntity().launchProjectile(Arrow.class);
    }
}
