package com.garbagemule.MobArena.util;

public final class Slugs {

    /**
     * Create a slug version
     * @param input
     * @return
     */
    public static String create(String input) {
        return input
            .toLowerCase()
            .replaceAll("[.,:;'\"]", "")
            .replaceAll("[<>(){}\\[\\]]", "")
            .replaceAll("[ _']", "-");
    }

}
