package com.garbagemule.MobArena.steps;

import org.bukkit.entity.Player;

class SetHunger extends PlayerStep {
    private static final int FULL_FOOD = 20;
    private static final float NORMAL_SATURATION = 5f;
    private static final float NORMAL_EXHAUSTION = 0f;

    private int food;
    private float saturation;
    private float exhaustion;

    private SetHunger(Player player) {
        super(player);
    }

    @Override
    public void run() {
        food = player.getFoodLevel();
        saturation = player.getSaturation();
        exhaustion = player.getExhaustion();

        player.setExhaustion(NORMAL_EXHAUSTION);
        player.setSaturation(NORMAL_SATURATION);
        player.setFoodLevel(FULL_FOOD);
    }

    @Override
    public void undo() {
        player.setFoodLevel(food);
        player.setSaturation(saturation);
        player.setExhaustion(exhaustion);
    }

    static StepFactory create() {
        return SetHunger::new;
    }
}
