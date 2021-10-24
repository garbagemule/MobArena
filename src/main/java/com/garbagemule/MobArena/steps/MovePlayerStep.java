package com.garbagemule.MobArena.steps;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.function.Supplier;

abstract class MovePlayerStep extends PlayerStep {
    private static final Vector NULL_VECTOR = new Vector(0, 0, 0);

    private final Supplier<Location> destination;

    private Location location;

    MovePlayerStep(Player player, Supplier<Location> destination) {
        super(player);
        this.destination = destination;
    }

    @Override
    public void run() {
        location = player.getLocation();

        player.setVelocity(NULL_VECTOR);
        player.teleport(destination.get());
    }

    @Override
    public void undo() {
        player.setVelocity(NULL_VECTOR);
        player.teleport(location);
    }
}
