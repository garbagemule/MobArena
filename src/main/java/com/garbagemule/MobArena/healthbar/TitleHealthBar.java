package com.garbagemule.MobArena.healthbar;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

class TitleHealthBar implements HealthBar {

    private final String title;
    private final CreatesHealthString createsHealthString;
    private final Set<Player> players;

    TitleHealthBar(String title, CreatesHealthString createsHealthString) {
        this.title = title;
        this.createsHealthString = createsHealthString;
        this.players = new HashSet<>();
    }

    @Override
    public void setProgress(double progress) {
        String health = createsHealthString.create(progress);
        String message = title.isEmpty() ? health : title + " " + health;

        players.forEach(player -> player.sendTitle("", message));
    }

    @Override
    public void addPlayer(Player player) {
        players.add(player);
    }

    @Override
    public void removePlayer(Player player) {
        players.remove(player);
    }

    @Override
    public void removeAll() {
        players.clear();
    }

}
