package com.garbagemule.MobArena.steps;

import com.garbagemule.MobArena.framework.Arena;
import org.bukkit.entity.Player;

class GrantRewards extends PlayerStep {
    private final Arena arena;

    private GrantRewards(Player player, Arena arena) {
        super(player);
        this.arena = arena;
    }

    @Override
    public void run() {
        arena.getRewardManager().grantRewards(player);
    }

    @Override
    public void undo() {
        // OK BOSS
    }

    static StepFactory create(Arena arena) {
        return player -> new GrantRewards(player, arena);
    }
}
