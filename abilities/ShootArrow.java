import org.bukkit.entity.Arrow;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.MABoss;
import com.garbagemule.MobArena.waves.ability.*;

@AbilityInfo(
    name = "Shoot Arrow",
    aliases = {"arrow","arrows"}
)
public class ShootArrow implements Ability
{
    @Override
    public void execute(Arena arena, MABoss boss) {
        //boss.getEntity().launchProjectile(Arrow.class);
        boss.getEntity().shootArrow();
    }
}
