package com.garbagemule.MobArena.util.timer;

import com.garbagemule.MobArena.Msg;
import com.garbagemule.MobArena.framework.Arena;

import static com.garbagemule.MobArena.util.timer.Common.toSeconds;
import static com.garbagemule.MobArena.util.timer.Common.toTicks;

/**
 * The ChatCallback will periodically announce a message to all players in an
 * arena, based on the tick interval triggers provided in the constructor.
 * <p>
 * The primary purpose of the class is to encapsulate the logic needed by e.g.
 * the {@link AutoStartTimer}, when {@code display-timer-as-level: false}.
 */
public class ChatCallback extends TimerCallbackAdapter {
    private Arena arena;
    private Msg msg;
    private CountdownTimer timer;

    private int[] triggers;
    private int index;

    public ChatCallback(Arena arena, Msg msg, CountdownTimer timer, int[] triggers) {
        this.arena = arena;
        this.msg   = msg;
        this.timer = timer;

        this.triggers = triggers;
        this.index = 0;
    }

    @Override
    public void onStart() {
        long duration = timer.getDuration();

        for (index = 0; index < triggers.length; index++) {
            long trigger = toTicks(triggers[index]);
            if (trigger < duration) {
                timer.setInterval(duration - trigger);
                break;
            }
        }
    }

    @Override
    public void onTick() {
        // Announce remaining seconds
        long ticks  = timer.getRemaining();
        int seconds = toSeconds(ticks);
        arena.announce(msg, String.valueOf(seconds));

        // Calculate the new interval
        index++;
        if (index < triggers.length) {
            long trigger = toTicks(triggers[index]);
            timer.setInterval(ticks - trigger);
        }
    }
}
