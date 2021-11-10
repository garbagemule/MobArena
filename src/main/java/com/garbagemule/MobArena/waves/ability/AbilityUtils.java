package com.garbagemule.MobArena.waves.ability;

import com.garbagemule.MobArena.MAUtils;
import com.garbagemule.MobArena.framework.Arena;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AbilityUtils
{
    public static Random random = new Random();

    /**
     * Get the target player of the LivingEntity if possible.
     * @param arena the arena
     * @param entity The entity whose target to get
     * @param random Grab a random player if no target was found
     * @return The target player, or null
     */
    public static LivingEntity getTarget(Arena arena, LivingEntity entity, boolean random) {
        if (entity instanceof Creature) {
            LivingEntity target = ((Creature) entity).getTarget();

            if (target instanceof Player && arena.inArena((Player) target)) {
                return target;
            }
        }

        if (random) {
            return getRandomPlayer(arena);
        }
        return null;
    }

    /**
     * Get a random arena player.
     * @param arena the arena
     * @return a random arena player, or null if none were found
     */
    public static Player getRandomPlayer(Arena arena) {
        List<Player> list = new ArrayList<>(arena.getPlayersInArena());
        if (list.isEmpty()) return null;

        return list.get(random.nextInt(list.size()));
    }

    /**
     * Get a list of nearby players
     * @param arena the arena
     * @param boss the boss
     * @param x the 'radius' in which to grab players
     * @return a list of nearby players
     */
    public static List<Player> getNearbyPlayers(Arena arena, Entity boss, int x) {
        List<Player> result = new ArrayList<>();
        for (Entity e : boss.getNearbyEntities(x, x, x)) {
            if (arena.getPlayersInArena().contains(e)) {
                result.add((Player) e);
            }
        }
        return result;
    }

    /**
     * Get a list of distant players
     * @param arena the arena
     * @param boss the boss
     * @param x the 'radius' in which to exclude players
     * @return a list of distant players
     */
    public static List<Player> getDistantPlayers(Arena arena, Entity boss, int x) {
        List<Player> result = new ArrayList<>();
        for (Player p : arena.getPlayersInArena()) {
            if (MAUtils.distanceSquared(arena.getPlugin(), p, boss.getLocation()) > (double) (x*x)) {
                result.add(p);
            }
        }
        return result;
    }
}
