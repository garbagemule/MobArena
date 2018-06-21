package com.garbagemule.MobArena.metrics;

import com.garbagemule.MobArena.MobArena;
import org.bstats.bukkit.Metrics;

public class VaultChart extends Metrics.SimplePie {

    public VaultChart(MobArena plugin) {
        super("uses_vault", () -> plugin.getEconomy() != null ? "Yes" : "No");
    }

}
