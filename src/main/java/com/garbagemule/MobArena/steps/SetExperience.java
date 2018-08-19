package com.garbagemule.MobArena.steps;

import org.bukkit.entity.Player;

class SetExperience extends PlayerStep {
    private int total;
    private int level;
    private float exp;

    private SetExperience(Player player) {
        super(player);
    }

    @Override
    public void run() {
        total = player.getTotalExperience();
        level = player.getLevel();
        exp = player.getExp();

        player.setExp(0);
        player.setLevel(0);
        player.setTotalExperience(0);
    }

    @Override
    public void undo() {
        player.setTotalExperience(total);
        player.setLevel(level);
        player.setExp(exp);
    }

    static StepFactory create() {
        return SetExperience::new;
    }
}
