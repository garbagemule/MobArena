import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.MABoss;
import com.garbagemule.MobArena.waves.ability.*;

@AbilityInfo(
    name = "Obsidian Bomb",
    aliases = {"obsidianbomb"}
)
public class ObsidianBomb implements Ability
{
    /**
     * How many ticks before the bomb goes off.
     */
    private final int FUSE = 80;
    
    @Override
    public void execute(final Arena arena, MABoss boss) {
        // Grab the target, or a random player.
        LivingEntity target = AbilityUtils.getTarget(arena, boss.getEntity(), true);
        
        final World world = arena.getWorld();
        final Location loc = target.getLocation();
        
        Block b = world.getBlockAt(target.getLocation()); 
        b.setType(Material.OBSIDIAN);
        arena.addBlock(b);
        
        arena.scheduleTask(new Runnable() {
            public void run() {
                if (!arena.isRunning())
                    return;
                
                world.getBlockAt(loc).breakNaturally();
                world.createExplosion(loc, 3F);
            }
        }, FUSE);
    }
}
