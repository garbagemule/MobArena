package com.garbagemule.MobArena.metrics;

import com.garbagemule.MobArena.MobArena;
import org.bstats.bukkit.Metrics;

public class FoodRegenChart extends Metrics.SimplePie {

    public FoodRegenChart(MobArena plugin) {
        super("food_regen_pie", () -> usesFoodRegen(plugin) ? "Yes" : "No");
    }

    private static boolean usesFoodRegen(MobArena plugin) {
        return plugin.getArenaMaster().getArenas().stream()
            .anyMatch(arena -> arena.getSettings().getBoolean("food-regen", false));
    }

}
