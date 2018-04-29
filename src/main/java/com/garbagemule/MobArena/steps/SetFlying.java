package com.garbagemule.MobArena.steps;

import org.bukkit.entity.Player;

class SetFlying extends PlayerStep {
    private boolean allow;
    private boolean flying;
    private float speed;

    private SetFlying(Player player) {
        super(player);
    }

    @Override
    public void run() {
        allow = player.getAllowFlight();
        flying = player.isFlying();
        speed = player.getFlySpeed();

        player.setFlySpeed(0);
        player.setFlying(false);
        player.setAllowFlight(false);
    }

    @Override
    public void undo() {
        player.setAllowFlight(allow);
        player.setFlying(flying);
        player.setFlySpeed(speed);
    }

    static StepFactory create() {
        return SetFlying::new;
    }
}
