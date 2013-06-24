package com.garbagemule.MobArena.waves.ability.core;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.MABoss;
import com.garbagemule.MobArena.waves.ability.Ability;
import com.garbagemule.MobArena.waves.ability.AbilityInfo;
import com.garbagemule.MobArena.waves.ability.AbilityUtils;
import org.bukkit.entity.Player;

@AbilityInfo(
    name = "Fire Aura",
    aliases = {"fireaura","auraoffire"}
)
public class FireAura implements Ability
{
    /**
     * How close players must be to be affected by the ability.
     */
    private final int RADIUS = 5;
    
    /**
     * How many ticks the players should be on fire for.
     */
    private final int TICKS = 20;
    
    @Override
    public void execute(Arena arena, MABoss boss) {
        for (Player p : AbilityUtils.getNearbyPlayers(arena, boss.getEntity(), RADIUS))
            p.setFireTicks(TICKS);
    }
}
