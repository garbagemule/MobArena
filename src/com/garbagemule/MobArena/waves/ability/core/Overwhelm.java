import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.MABoss;
import com.garbagemule.MobArena.waves.ability.Ability;
import com.garbagemule.MobArena.waves.ability.AbilityInfo;

@AbilityInfo(name="Overwhelm", aliases={"overwhelm"})
public class Overwhelm implements Ability{
	
	@Override
	public void execute(Arena arena, MABoss boss) {
		Set<LivingEntity> monsters = arena.getMonsterManager().getMonsters();
		Set<Player> players = arena.getPlayersInArena();
		Location spawn = arena.getRegion().getArenaWarp();
		
		for (LivingEntity e: monsters) {
			e.teleport(spawn);
		}
		
		for (Player p: players) {
			p.teleport(spawn);
		}
		
	}

}
