package com.garbagemule.MobArena.metrics;

import com.garbagemule.MobArena.MobArena;
import org.bstats.bukkit.Metrics;

public class ClassChestsChart extends Metrics.SimplePie {

    public ClassChestsChart(MobArena plugin) {
        super("class_chests_pie", () -> usesClassChests(plugin) ? "Yes" : "No");
    }

    private static boolean usesClassChests(MobArena plugin) {
        return plugin.getArenaMaster().getArenas().stream()
            .anyMatch(arena -> arena.getSettings().getBoolean("use-class-chests", false));
    }

}
