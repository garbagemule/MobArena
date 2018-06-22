package com.garbagemule.MobArena.metrics;

import com.garbagemule.MobArena.MobArena;
import org.bstats.bukkit.Metrics;

public class PvpEnabledChart extends Metrics.SimplePie {

    public PvpEnabledChart(MobArena plugin) {
        super("pvp_enabled_pie", () -> hasPvpEnabled(plugin) ? "Yes" : "No");
    }

    private static boolean hasPvpEnabled(MobArena plugin) {
        return plugin.getArenaMaster().getArenas().stream()
            .anyMatch(arena -> arena.getSettings().getBoolean("pvp-enabled", false));
    }

}
