import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.MABoss;
import com.garbagemule.MobArena.waves.ability.Ability;
import com.garbagemule.MobArena.waves.ability.AbilityInfo;
import com.garbagemule.MobArena.waves.ability.AbilityUtils;

@AbilityInfo(name = "Fatigue Nearby", aliases = { "fatiguenearby" })
public class FatigueNearby implements Ability {

	public static final int RADIUS = 5;
	public static final int AMPLIFIER = 0;
	public static final int DURATION = 60;

	@Override
	public void execute(Arena arena, MABoss boss) {

		if (AbilityUtils.getTarget(arena, boss.getEntity(), false) == null) {
			return;
		}

		for (Player e : AbilityUtils.getNearbyPlayers(arena, boss.getEntity(),
				RADIUS)) {
			e.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING,
					DURATION, AMPLIFIER));
		}

	}

}
