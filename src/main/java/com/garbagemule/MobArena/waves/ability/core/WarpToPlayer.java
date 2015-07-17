package com.garbagemule.MobArena.waves.ability.core;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.MABoss;
import com.garbagemule.MobArena.waves.ability.Ability;
import com.garbagemule.MobArena.waves.ability.AbilityInfo;
import com.garbagemule.MobArena.waves.ability.AbilityUtils;
import org.bukkit.entity.Player;

@AbilityInfo(
    name = "Warp",
    aliases = {"warp","warptoplayer"}
)
public class WarpToPlayer implements Ability
{
    @Override
    public void execute(Arena arena, MABoss boss) {
        Player p = AbilityUtils.getRandomPlayer(arena);
        boss.getEntity().teleport(p);
    }
}
