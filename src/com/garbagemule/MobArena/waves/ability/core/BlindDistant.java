import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.MABoss;
import com.garbagemule.MobArena.waves.ability.Ability;
import com.garbagemule.MobArena.waves.ability.AbilityInfo;
import com.garbagemule.MobArena.waves.ability.AbilityUtils;

@AbilityInfo(name = "Blind Distant", aliases = { "blinddistant" })
public class BlindDistant implements Ability {

	public static final int RADIUS = 8;
	public static final int DURATION = 60;

	@Override
	public void execute(Arena arena, MABoss boss) {

		for (Player e : AbilityUtils.getDistantPlayers(arena, boss.getEntity(),
				RADIUS)) {
			e.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,
					DURATION, 0));
		}
	}

}
