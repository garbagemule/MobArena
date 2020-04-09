package com.garbagemule.MobArena.waves.ability.core;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.MABoss;
import com.garbagemule.MobArena.waves.ability.Ability;
import com.garbagemule.MobArena.waves.ability.AbilityInfo;
import com.garbagemule.MobArena.waves.ability.AbilityUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;

@AbilityInfo(
    name = "Obsidian Bomb",
    aliases = {"obsidianbomb"}
)
public class ObsidianBomb implements Ability
{
    /**
     * How many ticks before the bomb goes off.
     */
    private static final int FUSE = 80;
    
    @Override
    public void execute(final Arena arena, MABoss boss) {
        // Grab the target, or a random player.
        LivingEntity target = AbilityUtils.getTarget(arena, boss.getEntity(), true);
        
        final World world = arena.getWorld();
        final Location loc;

        Block b = world.getBlockAt(target.getLocation());
        for (int i = 0; i < 3; i++) {
            if (b.getType() == Material.AIR) {
                break;
            }
            b = b.getRelative(BlockFace.UP);
        }
        loc = b.getLocation();

        if (b.getType() != Material.AIR) {
            Bukkit.getLogger().warning("[MobArena] Failed to place Obsidian Bomb at: " + target.getLocation());
            return;
        }

        b.setType(Material.OBSIDIAN);
        arena.addBlock(b);

        arena.scheduleTask(new Runnable() {
            public void run() {
                if (!arena.isRunning())
                    return;
                
                world.getBlockAt(loc).setType(Material.AIR);
                world.createExplosion(loc, 3F);
            }
        }, FUSE);
    }
}
