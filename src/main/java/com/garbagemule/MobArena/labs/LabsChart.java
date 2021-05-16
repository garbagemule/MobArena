package com.garbagemule.MobArena.labs;

import com.garbagemule.MobArena.MobArena;
import org.bstats.charts.SimplePie;

import java.util.function.Function;

public class LabsChart extends SimplePie {

    public LabsChart(
        MobArena plugin,
        String chartId,
        Function<LabsConfig, LabsConfigSection> getter
    ) {
        super(chartId, () -> usesFeature(plugin, getter) ? "Yes" : "No");
    }

    private static boolean usesFeature(
        MobArena plugin,
        Function<LabsConfig, LabsConfigSection> getter
    ) {
        Labs labs = plugin.getLabs();
        if (labs == null) {
            return false;
        }

        LabsConfigSection section = getter.apply(labs.config);
        if (section == null) {
            return false;
        }

        return section.enabled;
    }

}
