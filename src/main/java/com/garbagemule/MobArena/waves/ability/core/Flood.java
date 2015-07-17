package com.garbagemule.MobArena.waves.ability.core;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.MABoss;
import com.garbagemule.MobArena.waves.ability.Ability;
import com.garbagemule.MobArena.waves.ability.AbilityInfo;
import com.garbagemule.MobArena.waves.ability.AbilityUtils;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

@AbilityInfo(
    name = "Flood",
    aliases = {"flood"}
)
public class Flood implements Ability
{
    @Override
    public void execute(Arena arena, MABoss boss) {
        Player p = AbilityUtils.getRandomPlayer(arena);
        Block block = p.getLocation().getBlock();
        
        if (block.getTypeId() == 0) {
            block.setTypeId(8);
            arena.addBlock(block);
        }
    }
}
