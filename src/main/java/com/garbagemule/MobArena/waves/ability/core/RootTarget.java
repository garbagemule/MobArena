package com.garbagemule.MobArena.waves.ability.core;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.region.ArenaRegion;
import com.garbagemule.MobArena.waves.MABoss;
import com.garbagemule.MobArena.waves.ability.Ability;
import com.garbagemule.MobArena.waves.ability.AbilityInfo;
import com.garbagemule.MobArena.waves.ability.AbilityUtils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * DISCLAIMER: The Ability source code is provided as-is, and the creator(s) of
 *             MobArena WILL NOT be held responsible for any damage that may
 *             result from altering the files.
 *              
 * WARNING:    Unless you know exactly what you are doing, i.e. you have a lot
 *             of experience with Java and Bukkit, you should never change any
 *             other values than those of the variables in CAPITAL LETTERS.
 *             
 *             
 * Root Target
 * Freezes the boss' target in place for ~3 seconds (default), by warping the
 * player to the same spot [ITERATIONS] times, with [TICKS] server ticks
 * between each iteration.
 * 
 * @author garbagemule
 */
@AbilityInfo(
    name = "Root Target",
    aliases = {"roottarget", "freezetarget"}
)
public class RootTarget implements Ability
{
    /**
     * How many times the player will be warped back to his original position.
     * Must be greater than 0.
     */
    private static final int ITERATIONS = 30;
    
    /**
     * How many server ticks between each iteration of
     * Must be greater than 0.
     */
    private static final int TICKS = 1;
    
    @Override
    public void execute(Arena arena, MABoss boss) {
        final LivingEntity target = AbilityUtils.getTarget(arena, boss.getEntity(), true);
        if (target == null || !(target instanceof Player))
            return;

        Player player = (Player) target;
        ArenaRegion region = arena.getRegion();

        Block block = player.getLocation().getBlock();
        Block solid = findSolidBlockBelow(region, block);

        Location location = player.getLocation();
        location.setY(solid.getLocation().getY() + 1);

        rootTarget(arena, player, location, ITERATIONS);
    }

    private Block findSolidBlockBelow(ArenaRegion region, Block block) {
        Block below = block.getRelative(BlockFace.DOWN);

        // If the block below is not in the region, return the block itself
        if (!region.contains(below.getLocation())) {
            return block;
        }

        // If the block below is solid, return it
        if (below.getType().isSolid()) {
            return below;
        }

        // Otherwise, recurse downwards
        return findSolidBlockBelow(region, below);
    }
    
    private void rootTarget(final Arena arena, final Player p, final Location loc, final int counter) {
        // If the counter is 0, we're done.
        if (counter <= 0) {
            return;
        }
        
        arena.scheduleTask(new Runnable() {
            public void run() {
                if (!arena.isRunning() || !arena.inArena(p)) {
                    return;
                }
                
                p.teleport(loc);
                rootTarget(arena, p, loc, counter - 1);
            }
        }, TICKS);
    }
}
