package com.garbagemule.MobArena.waves.ability.core;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.MABoss;
import com.garbagemule.MobArena.waves.ability.Ability;
import com.garbagemule.MobArena.waves.ability.AbilityInfo;
import org.bukkit.entity.Fireball;

@AbilityInfo(
    name = "Shoot Fireball",
    aliases = {"fireball","fireballs"}
)
public class ShootFireball implements Ability
{
    @Override
    public void execute(Arena arena, MABoss boss) {
        boss.getEntity().launchProjectile(Fireball.class);
    }
}
