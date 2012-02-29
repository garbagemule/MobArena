import org.bukkit.entity.Player;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.MABoss;
import com.garbagemule.MobArena.waves.ability.*;

@AbilityInfo(
    name = "Warp",
    aliases = {"warp","warptoplayer"}
)
public class WarpToPlayer implements Ability
{
    @Override
    public void execute(Arena arena, MABoss boss) {
        Player p = AbilityUtils.getRandomPlayer(arena);
        boss.getEntity().teleport(p);
    }
}
