package com.garbagemule.MobArena.waves.ability.core;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.MABoss;
import com.garbagemule.MobArena.waves.ability.Ability;
import com.garbagemule.MobArena.waves.ability.AbilityInfo;
import com.garbagemule.MobArena.waves.ability.AbilityUtils;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@AbilityInfo(
    name = "Root Target",
    aliases = {"roottarget", "freezetarget"}
)
public class RootTarget implements Ability
{
    /**
     * How long the the potions last (in ticks).
     */
    private static final int DURATION = 30;
    
    /**
     * The amplifier for the potions.
     */
    private static final int AMPLIFIER = 100;
    
    @Override
    public void execute(Arena arena, MABoss boss) {
        final LivingEntity target = AbilityUtils.getTarget(arena, boss.getEntity(), true);
        if (target == null || !(target instanceof Player))
            return;

        Player player = (Player) target;
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, DURATION, AMPLIFIER));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, DURATION, AMPLIFIER));
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, DURATION, -AMPLIFIER));
    }
}
