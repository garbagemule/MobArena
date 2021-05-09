package com.garbagemule.MobArena.metrics;

import com.garbagemule.MobArena.MobArena;
import com.garbagemule.MobArena.framework.Arena;
import org.bstats.charts.SimplePie;

public class IsolatedChatChart extends SimplePie {

    public IsolatedChatChart(MobArena plugin) {
        super("isolated_chat_pie", () -> usesIsolatedChat(plugin) ? "Yes" : "No");
    }

    private static boolean usesIsolatedChat(MobArena plugin) {
        return plugin.getArenaMaster().getArenas().stream()
            .anyMatch(Arena::hasIsolatedChat);
    }

}
