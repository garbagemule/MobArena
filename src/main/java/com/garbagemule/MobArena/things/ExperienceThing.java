package com.garbagemule.MobArena.things;

import org.bukkit.entity.Player;

public class ExperienceThing implements Thing {

    private final int experience;

    public ExperienceThing(int experience) {
        this.experience = experience;
    }

    @Override
    public boolean giveTo(Player player) {
        player.giveExp(experience);
        return true;
    }

    @Override
    public boolean takeFrom(Player player) {
        int current = player.getTotalExperience();
        if (current < experience) {
            return false;
        }

        /*
         * Unfortunately, Player#giveExp(int) behaves strangely with negative
         * values; if the amount of points into the current level is less than
         * the amount we try to take away, the levels won't decrease, and the
         * progress will simply go into negative values. As a workaround, we
         * can just reset the experience values and then return the previous
         * total experience - minus the amount we're taking away.
         */

        player.setTotalExperience(0);
        player.setLevel(0);
        player.setExp(0);

        player.giveExp(current - experience);

        return true;
    }

    @Override
    public boolean heldBy(Player player) {
        return player.getTotalExperience() > experience;
    }

}
