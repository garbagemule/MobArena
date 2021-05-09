package com.garbagemule.MobArena.metrics;

import com.garbagemule.MobArena.MobArena;
import org.bstats.charts.SimplePie;

public class ArenaCountChart extends SimplePie {

    public ArenaCountChart(MobArena plugin) {
        super("arena_count", () -> {
            int count = plugin.getArenaMaster().getArenas().size();
            if (count < 10) {
                return String.valueOf(count);
            }
            return "10+";
        });
    }

}
