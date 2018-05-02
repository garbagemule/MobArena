package com.garbagemule.MobArena.steps;

import com.garbagemule.MobArena.framework.Arena;
import org.bukkit.entity.Player;

class MoveToExit extends MovePlayerStep {
    private MoveToExit(Player player, Arena arena) {
        super(player, () -> arena.getRegion().getExitWarp());
    }

    static StepFactory create(Arena arena) {
        return player -> new MoveToExit(player, arena);
    }
}
