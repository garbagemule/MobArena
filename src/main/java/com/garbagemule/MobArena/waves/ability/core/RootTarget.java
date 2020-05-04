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

/**
 * DISCLAIMER: The Ability source code is provided as-is, and the creator(s) of
 *             MobArena WILL NOT be held responsible for any damage that may
 *             result from altering the files.
 *              
 * WARNING:    Unless you know exactly what you are doing, i.e. you have a lot
 *             of experience with Java and Bukkit, you should never change any
 *             other values than those of the variables in CAPITAL LETTERS.
 *             
 *             
 * Root Target
 * Freezes the boss' target in place for ~3 seconds (default), by warping the
 * player to the same spot [ITERATIONS] times, with [TICKS] server ticks
 * between each iteration.
 * 
 * @author garbagemule
 */
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
