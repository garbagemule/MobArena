package com.garbagemule.MobArena.steps;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.function.Supplier;

abstract class MovePlayerStep extends PlayerStep {
    private final Supplier<Location> destination;

    private Location location;

    MovePlayerStep(Player player, Supplier<Location> destination) {
        super(player);
        this.destination = destination;
    }

    @Override
    public void run() {
        location = player.getLocation();

        player.setFallDistance(0);
        player.teleport(destination.get());
    }

    @Override
    public void undo() {
        player.setFallDistance(0);
        player.teleport(location);
    }
}
