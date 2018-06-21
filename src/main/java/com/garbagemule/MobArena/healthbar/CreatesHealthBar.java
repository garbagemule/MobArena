package com.garbagemule.MobArena.healthbar;

import org.bukkit.entity.Entity;

public class CreatesHealthBar {

    private final String type;
    private final CreatesHealthString createsHealthString;

    public CreatesHealthBar(String type) {
        this.type = type;
        this.createsHealthString = new CreatesHealthString();
    }

    public HealthBar create(Entity entity, String title) {
        String name = (title != null) ? title : "";

        switch (type) {
            case "title":
                return new TitleHealthBar(name, createsHealthString);
            case "name":
                return new NameHealthBar(entity, name, createsHealthString);
            default:
                return new NullHealthBar();
        }
    }

}
