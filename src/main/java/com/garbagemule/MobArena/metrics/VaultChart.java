package com.garbagemule.MobArena.metrics;

import com.garbagemule.MobArena.MobArena;
import org.bstats.charts.SimplePie;

public class VaultChart extends SimplePie {

    public VaultChart(MobArena plugin) {
        super("uses_vault", () -> plugin.getEconomy() != null ? "Yes" : "No");
    }

}
