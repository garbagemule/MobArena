package com.garbagemule.MobArena.healthbar;

import org.bukkit.entity.Player;

public interface HealthBar {

    void setProgress(double progress);

    void addPlayer(Player player);

    void removePlayer(Player player);

    void removeAll();

}
