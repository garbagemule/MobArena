package com.garbagemule.MobArena.steps;

import com.garbagemule.MobArena.framework.Arena;
import org.bukkit.entity.Player;

class MoveToSpec extends MovePlayerStep {
    private MoveToSpec(Player player, Arena arena) {
        super(player, () -> arena.getRegion().getSpecWarp());
    }

    static StepFactory create(Arena arena) {
        return player -> new MoveToSpec(player, arena);
    }
}
