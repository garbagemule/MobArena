package com.garbagemule.MobArena.waves.ability;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.MABoss;

public interface Ability
{
    /**
     * Execute the boss ability.
     * The ability should be concise and not start any unnecessary threading,
     * or schedule any unnecessary tasks.
     * The MABoss object can be used to get the LivingEntity that is the boss,
     * and to damage, heal or just set the boss health directly.
     * @param arena the Arena of the boss
     * @param boss the MABoss object
     */
    public void execute(Arena arena, MABoss boss);
}
