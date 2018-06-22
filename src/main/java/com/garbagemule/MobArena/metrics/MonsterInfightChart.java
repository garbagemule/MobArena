package com.garbagemule.MobArena.metrics;

import com.garbagemule.MobArena.MobArena;
import org.bstats.bukkit.Metrics;

public class MonsterInfightChart extends Metrics.SimplePie {

    public MonsterInfightChart(MobArena plugin) {
        super("monster_infight_pie", () -> usesMonsterInfight(plugin) ? "Yes" : "No");
    }

    private static boolean usesMonsterInfight(MobArena plugin) {
        return plugin.getArenaMaster().getArenas().stream()
            .anyMatch(arena -> arena.getSettings().getBoolean("monster-infight", false));
    }

}
