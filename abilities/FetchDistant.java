import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.MABoss;
import com.garbagemule.MobArena.waves.ability.*;

@AbilityInfo(
    name = "Fetch Distant",
    aliases = {"fetchdistant"}
)
public class FetchDistant implements Ability
{
    /**
     * How far away players must be to be affected by the ability.
     */
    private final int RADIUS = 8;
    
    @Override
    public void execute(Arena arena, MABoss boss) {
        Location bLoc = boss.getEntity().getLocation();
        
        for (Player p : AbilityUtils.getDistantPlayers(arena, boss.getEntity(), RADIUS)) {
            p.teleport(bLoc);
        }
    }
}
