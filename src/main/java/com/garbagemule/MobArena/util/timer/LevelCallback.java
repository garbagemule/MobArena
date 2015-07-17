package com.garbagemule.MobArena.util.timer;

import static com.garbagemule.MobArena.util.timer.Common.*;

import com.garbagemule.MobArena.framework.Arena;
import org.bukkit.entity.Player;

/**
 * The LevelCallback will display the countdown timer as the player level for
 * all lobby players every second.
 * <p>
 * The primary purpose of the class is to encapsulate the logic needed by e.g.
 * the {@link AutoStartTimer}, when {@code display-timer-as-level: true}.
 */
public class LevelCallback extends TimerCallbackAdapter {
    private Arena arena;
    private CountdownTimer timer;

    public LevelCallback(Arena arena, CountdownTimer timer) {
        this.arena = arena;
        this.timer = timer;
    }

    public void onStart() {
        timer.setInterval(20);
    }

    public void onTick() {
        int remaining = toSeconds(timer.getRemaining());

        for (Player p : arena.getPlayersInLobby()) {
            p.setLevel(remaining);
        }
    }
}
