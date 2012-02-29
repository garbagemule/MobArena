import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.MABoss;
import com.garbagemule.MobArena.waves.ability.*;

@AbilityInfo(
    name = "Throw Nearby",
    aliases = {"thrownearby"}
)
public class ThrowNearby implements Ability
{
    /**
     * How close players must be to be affected by the ability.
     */
    private final int RADIUS = 5;
    
    @Override
    public void execute(Arena arena, MABoss boss) {
        Location bLoc = boss.getEntity().getLocation();
        
        for (Player p : AbilityUtils.getNearbyPlayers(arena, boss.getEntity(), RADIUS)) {
            Location loc = p.getLocation();
            Vector v     = new Vector(loc.getX() - bLoc.getX(), 0, loc.getZ() - bLoc.getZ());
            p.setVelocity(v.normalize().setY(0.8));
        }
    }
}
