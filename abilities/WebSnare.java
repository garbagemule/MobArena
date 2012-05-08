import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.MABoss;
import com.garbagemule.MobArena.waves.ability.Ability;
import com.garbagemule.MobArena.waves.ability.AbilityInfo;

/**
 * The web snare ability works like this:
 * The boss will throw a web ball (snow ball) against the enemy.
 * On impact the web ball will turn into a few web blocks.
 * These web blocks will disappear after 5 seconds.
 */
@AbilityInfo(
    name = "Web Snare",
    aliases = {"websnare", "websnares"}
)
public class WebSnare implements Ability, Listener
{
    protected int SNARE_TICKS = 5 * 20; // 20 ticks is one second.
    protected Map<Projectile, Arena> balls = new HashMap<Projectile, Arena>();
    
    public WebSnare() {
        Plugin ma = Bukkit.getPluginManager().getPlugin("MobArena");
        Bukkit.getServer().getPluginManager().registerEvents(this, ma);
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void monitorProjectileHitEvent(ProjectileHitEvent event) {
        // Initial ball tracker fixing
        Projectile ball = event.getEntity();
        Arena arena = this.balls.get(ball);
        if (arena == null) return;
        this.balls.remove(ball);
        
        // What location should we webify?
        Location location = ball.getLocation();
        Vector ballVelocity = ball.getVelocity().clone();
        Vector locationDiff = ballVelocity.clone().normalize().multiply(1.5d);
        location.add(locationDiff);
        
        // Webify! :D
        this.webify(location, arena);
    }
    
    @Override
    public void execute(Arena arena, MABoss boss) {
        Projectile ball = boss.getEntity().launchProjectile(Snowball.class);
        this.balls.put(ball, arena);
        this.fxAt(boss.getEntity().getLocation(), Effect.BOW_FIRE, 0);
    }
    
    // -------------------------------------------- //
    // UTILITIES
    // -------------------------------------------- //
    
    public void webify(Location location, Arena arena) {
        // What blocks should we webify
        List<Block> blocks = new ArrayList<Block>();
        Block center = location.getBlock();
        blocks.add(center);
        blocks.add(center.getRelative(+1, +0, +0));
        blocks.add(center.getRelative(-1, +0, +0));
        blocks.add(center.getRelative(+0, +1, +0));
        blocks.add(center.getRelative(+0, -1, +0));
        blocks.add(center.getRelative(+0, +0, +1));
        blocks.add(center.getRelative(+0, +0, -1));
        Iterator<Block> itr = blocks.iterator();
        while (itr.hasNext()) {
            Block block = itr.next();
            if (block.getType() != Material.AIR) {
                itr.remove();
            }
        }
        
        // Webify them :D
        this.webify(arena, location, blocks);
    }
    
    public void webify(final Arena arena, final Location location, final List<Block> blocks) {
        // Play some FX
        this.fxAt(location, Effect.EXTINGUISH, 0);
        for (int i = 0; i <= 8; i++) {
            this.fxAt(location, Effect.SMOKE, i);
        }
        
        // Set blocks to web
        for (Block block : blocks) {
            block.setType(Material.WEB);
            arena.addBlock(block);
        }
        
        // Schedule web removal
        arena.scheduleTask(new Runnable() {
            @Override
            public void run() {
                for (Block block : blocks) {
                    block.setType(Material.AIR);
                }
            }
        }, this.SNARE_TICKS);
    }
    
    public void fxAt(Location location, Effect effect, int data) {
        World world = location.getWorld();
        world.playEffect(location, effect, data);
    }
}
