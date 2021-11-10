package com.garbagemule.MobArena.util.timer;

import static com.garbagemule.MobArena.util.timer.Common.toSeconds;

import com.garbagemule.MobArena.framework.Arena;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

/**
 * The LevelCallback will display the countdown timer as the player level for
 * all lobby players every second.
 * <p>
 * The primary purpose of the class is to encapsulate the logic needed by e.g.
 * the {@link AutoStartTimer}, when {@code display-timer-as-level: true}.
 */
@RequiredArgsConstructor
public class LevelCallback extends TimerCallbackAdapter {
    private final Arena arena;
    private final CountdownTimer timer;

    public void onStart() {
        timer.setInterval(20);
    }

    public void onTick() {
        int remaining = toSeconds(timer.getRemaining());

        arena.getPlayersInLobby().forEach(player -> player.setLevel(remaining));
    }
}
