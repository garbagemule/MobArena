package com.garbagemule.MobArena.healthbar;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

class BossHealthBar implements HealthBar {

    private static final double LOW_HEALTH = 0.25;

    private final BossBar bar;

    BossHealthBar(String title) {
        bar = Bukkit.createBossBar(
            title.isEmpty() ? "Boss" : title,
            BarColor.GREEN,
            BarStyle.SOLID,
            BarFlag.PLAY_BOSS_MUSIC,
            BarFlag.DARKEN_SKY
        );
    }

    @Override
    public void setProgress(double progress) {
        if (progress <= LOW_HEALTH && bar.getColor() != BarColor.RED) {
            bar.setColor(BarColor.RED);
        }
        bar.setProgress(progress);
    }

    @Override
    public void addPlayer(Player player) {
        bar.addPlayer(player);
    }

    @Override
    public void removePlayer(Player player) {
        bar.removePlayer(player);
    }

    @Override
    public void removeAll() {
        bar.removeAll();
    }

}
