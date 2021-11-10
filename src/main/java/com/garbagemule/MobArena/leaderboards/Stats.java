package com.garbagemule.MobArena.leaderboards;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
public enum Stats {
    PLAYER_NAME("Players", "playerName"),
    CLASS_NAME("Class", "class"),
    KILLS("Kills", "kills"),
    DAMAGE_DONE("Damage Done", "dmgDone"),
    DAMAGE_TAKEN("Damage Taken", "dmgTaken"),
    SWINGS("Swings", "swings"),
    HITS("Hits", "hits"),
    LAST_WAVE("Last Wave", "lastWave");

    private final String name, shortName;

    public String getShortName() {
        return shortName;
    }

    public String getFullName() {
        return name;
    }

    public static Stats getByFullName(String name) {
        return Arrays.stream(values()).filter(stats -> stats.getFullName().equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }

    public static Stats getByShortName(String name) {
        return Arrays.stream(values()).filter(stats -> stats.getShortName().equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }
}
