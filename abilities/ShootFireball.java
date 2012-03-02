import org.bukkit.entity.Fireball;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.MABoss;
import com.garbagemule.MobArena.waves.ability.*;

@AbilityInfo(
    name = "Shoot Fireball",
    aliases = {"fireball","fireballs"}
)
public class ShootFireball implements Ability
{
    @Override
    public void execute(Arena arena, MABoss boss) {
        boss.getEntity().launchProjectile(Fireball.class);
    }
}
