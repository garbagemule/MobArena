package com.garbagemule.MobArena.waves.ability;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;;

@Retention(RetentionPolicy.RUNTIME)
public @interface AbilityInfo
{
    /**
     * The "pretty print" name of the ability.
     * This value is printed when a boss executes the ability in the arena.
     */
    public String name();
    
    /**
     * The config aliases for the ability.
     * This is used by MobArena to parse ability names from the config-file.
     */
    public String[] aliases();
}
