package com.garbagemule.MobArena.steps;

import com.garbagemule.MobArena.framework.Arena;
import org.bukkit.entity.Player;

class MoveToLobby extends MovePlayerStep {
    private MoveToLobby(Player player, Arena arena) {
        super(player, () -> arena.getRegion().getLobbyWarp());
    }

    static StepFactory create(Arena arena) {
        return player -> new MoveToLobby(player, arena);
    }
}
