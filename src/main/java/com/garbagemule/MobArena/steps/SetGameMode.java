package com.garbagemule.MobArena.steps;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

class SetGameMode extends PlayerStep {
    private GameMode mode;

    private SetGameMode(Player player) {
        super(player);
    }

    @Override
    public void run() {
        mode = player.getGameMode();

        player.setGameMode(GameMode.SURVIVAL);
    }

    @Override
    public void undo() {
        player.setGameMode(mode);
    }

    static StepFactory create() {
        return SetGameMode::new;
    }
}
