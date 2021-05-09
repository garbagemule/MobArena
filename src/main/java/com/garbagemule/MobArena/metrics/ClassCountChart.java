package com.garbagemule.MobArena.metrics;

import com.garbagemule.MobArena.MobArena;
import org.bstats.charts.SimplePie;

public class ClassCountChart extends SimplePie {

    public ClassCountChart(MobArena plugin) {
        super("class_count", () -> {
            // We don't count "My Items", so subtract 1
            int count = plugin.getArenaMaster().getClasses().size() - 1;
            if (count < 10) {
                return String.valueOf(count);
            }
            return "10+";
        });
    }

}
