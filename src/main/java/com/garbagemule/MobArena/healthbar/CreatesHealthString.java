package com.garbagemule.MobArena.healthbar;

import org.bukkit.ChatColor;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

class CreatesHealthString {

    private static final int TOTAL_BARS = 20;
    private static final int LOW_BARS = TOTAL_BARS / 4;

    String create(double progress) {
        int bars = (int) (progress * TOTAL_BARS);

        String current = IntStream.range(0, bars)
            .mapToObj(i -> "|")
            .collect(Collectors.joining());

        String lost = IntStream.range(bars, TOTAL_BARS)
            .mapToObj(i -> "|")
            .collect(Collectors.joining());

        ChatColor color = (bars <= LOW_BARS) ? ChatColor.RED : ChatColor.GREEN;

        return color + current + ChatColor.GRAY + lost;
    }

}
