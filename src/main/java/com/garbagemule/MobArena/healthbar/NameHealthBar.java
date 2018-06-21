package com.garbagemule.MobArena.healthbar;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

class NameHealthBar implements HealthBar {

    private final Entity entity;
    private final String title;
    private final CreatesHealthString createsHealthString;

    NameHealthBar(Entity entity, String title, CreatesHealthString createsHealthString) {
        this.entity = entity;
        this.title = title;
        this.createsHealthString = createsHealthString;

        entity.setCustomNameVisible(true);
    }

    @Override
    public void setProgress(double progress) {
        String health = createsHealthString.create(progress);
        String name = title.isEmpty() ? health : title + " " + health;

        entity.setCustomName(name);
    }

    @Override
    public void addPlayer(Player player) {
        // OK BOSS
    }

    @Override
    public void removePlayer(Player player) {
        // OK BOSS
    }

    @Override
    public void removeAll() {
        // OK BOSS
    }

}
