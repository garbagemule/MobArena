package com.garbagemule.MobArena.steps;

import org.bukkit.entity.Player;

/**
 * StepFactories create closures over {@link Player Players} and return
 * {@link Step Steps} that operate on the given Player.
 */
public interface StepFactory {
    Step create(Player player);
}
