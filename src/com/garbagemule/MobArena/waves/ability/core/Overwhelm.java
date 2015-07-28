package com.garbagemule.MobArena.waves.ability.core;

import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.MABoss;
import com.garbagemule.MobArena.waves.ability.Ability;
import com.garbagemule.MobArena.waves.ability.AbilityInfo;

@AbilityInfo(name = "Overwhelm", aliases = { "overwhelm" })
public class Overwhelm implements Ability {

    @Override
    public void execute(Arena arena, MABoss boss) {
        // get the monsters
        Set<LivingEntity> monsters = arena.getMonsterManager().getMonsters();

        // get the players
        Set<Player> players = arena.getPlayersInArena();

        // get the player spawn point
        Location spawn = arena.getRegion().getArenaWarp();

        // teleport all the monsters to spawn
        for (LivingEntity e : monsters) {
            e.teleport(spawn);
        }

        // have all the players join the monsters!
        for (Player p : players) {
            p.teleport(spawn);
        }
    }
}
