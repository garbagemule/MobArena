import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.MABoss;
import com.garbagemule.MobArena.waves.ability.Ability;
import com.garbagemule.MobArena.waves.ability.AbilityInfo;
import com.garbagemule.MobArena.waves.ability.AbilityUtils;

@AbilityInfo(name = "Blind Target", aliases = { "blindtarget" })
public class BlindTarget implements Ability {

	public static final int DURATION = 60;

	public static final boolean RANDOM = false;

	@Override
	public void execute(Arena arena, MABoss boss) {

		LivingEntity target = AbilityUtils.getTarget(arena, boss.getEntity(),
				RANDOM);
		if (target == null) {
			return;
		}

		target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,
				DURATION, 0));
	}

}
