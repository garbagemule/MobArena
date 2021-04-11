package com.garbagemule.MobArena.metrics;

import com.garbagemule.MobArena.MobArena;
import org.bstats.charts.SimplePie;

public class MonsterInfightChart extends SimplePie {

    public MonsterInfightChart(MobArena plugin) {
        super("monster_infight_pie", () -> usesMonsterInfight(plugin) ? "Yes" : "No");
    }

    private static boolean usesMonsterInfight(MobArena plugin) {
        return plugin.getArenaMaster().getArenas().stream()
            .anyMatch(arena -> arena.getSettings().getBoolean("monster-infight", false));
    }

}
