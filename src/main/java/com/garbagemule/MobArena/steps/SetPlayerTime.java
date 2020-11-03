package com.garbagemule.MobArena.steps;

import com.garbagemule.MobArena.time.Time;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

class SetPlayerTime extends PlayerStep {
    private final Time time;

    private SetPlayerTime(Player player, Time time) {
        super(player);
        this.time = time;
    }

    @Override
    public void run() {
        if (time != null) {
            player.setPlayerTime(time.getTime(), false);
        }
    }

    @Override
    public void undo() {
        if (time != null) {
            player.resetPlayerTime();
        }
    }

    static StepFactory create(ConfigurationSection settings) {
        Time time = parseTime(settings);
        return player -> new SetPlayerTime(player, time);
    }

    private static Time parseTime(ConfigurationSection settings) {
        String value = settings.getString("player-time-in-arena", "world");
        try {
            return Time.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
