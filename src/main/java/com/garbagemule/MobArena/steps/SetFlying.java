package com.garbagemule.MobArena.steps;

import org.bukkit.entity.Player;

class SetFlying extends PlayerStep {
    private boolean allow;
    private boolean flying;

    private SetFlying(Player player) {
        super(player);
    }

    @Override
    public void run() {
        allow = player.getAllowFlight();
        flying = player.isFlying();

        player.setFlying(false);
        player.setAllowFlight(false);
    }

    @Override
    public void undo() {
        player.setAllowFlight(allow);
        player.setFlying(flying);
    }

    static StepFactory create() {
        return SetFlying::new;
    }
}
