package com.garbagemule.MobArena.util;

import org.bukkit.Material;

import java.util.Arrays;

public class Materials {

    public static final Material SIGN = resolve("OAK_SIGN", "SIGN");

    private static Material resolve(String... names) {
        for (String name : names) {
            try {
                return Material.valueOf(name);
            } catch (IllegalArgumentException e) {
                // Swallow
            }
        }
        throw new IllegalStateException("Unknown material: " + Arrays.toString(names));
    }

}
