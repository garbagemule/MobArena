package com.garbagemule.MobArena.steps;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

class SetHealth extends PlayerStep {
    private static final int NORMAL_FIRE = -20;
    private static final int NORMAL_AIR = 300;

    private double health;
    private int fire;
    private int air;

    private SetHealth(Player player) {
        super(player);
    }

    @Override
    public void run() {
        health = player.getHealth();
        fire = player.getFireTicks();
        air = player.getRemainingAir();

        player.setRemainingAir(NORMAL_AIR);
        player.setFireTicks(NORMAL_FIRE);

        double full = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
        player.setHealth(full);
    }

    @Override
    public void undo() {
        player.setHealth(health);
        player.setFireTicks(fire);
        player.setRemainingAir(air);
    }

    static StepFactory create() {
        return SetHealth::new;
    }
}
