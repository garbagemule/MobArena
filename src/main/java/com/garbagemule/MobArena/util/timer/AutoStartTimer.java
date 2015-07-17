package com.garbagemule.MobArena.util.timer;

import com.garbagemule.MobArena.Messenger;
import com.garbagemule.MobArena.Msg;
import com.garbagemule.MobArena.framework.Arena;

/**
 * The AutoStartTimer is a self-contained CountdownTimer (i.e. it is its own
 * callback), which force starts an arena (if the right conditions are met)
 * when it finishes.
 * <p>
 * The timer is preprogrammed with a series of tick interval triggers, such
 * that the timer only ticks when the time remaining matches one of these
 * triggers. When the timer ticks, the lobby players are informed about the
 * time remaining, and when the timer finishes, the arena is started. If the
 * {@code display-timer-as-level} flag is set, the timer will tick once per
 * second, updating the player levels.
 * <p>
 * The timer realizes a type of Null Object pattern if the duration of the
 * timer (i.e. the value of the auto-start-timer setting) is 0, where calling
 * the timer's methods does nothing.
 */
public class AutoStartTimer extends CountdownTimer implements TimerCallback {
    private Arena arena;
    private TimerCallback internalCallback;

    /**
     * Create an AutoStartTimer for the given arena.
     * <p>
     * The duration is determined from the {@code auto-start-timer} arena
     * setting. If the setting is non-positive, the timer acts as a Null
     * Object, allowing it to be safely used for arenas that do not have
     * an auto-start-timer.
     *
     * @param arena the arena the timer is responsible for
     */
    public AutoStartTimer(Arena arena) {
        super(arena.getPlugin());
        super.setCallback(this);

        this.arena = arena;

        // Set the duration
        long duration = arena.getSettings().getInt("auto-start-timer", 0) * 20l;
        setDuration(Math.max(0l, duration));

        // Choose level- or chat-callback
        boolean level = arena.getSettings().getBoolean("display-timer-as-level", false);
        if (level) {
            internalCallback = new LevelCallback(arena, this);
        } else {
            int[] triggers = {30, 10, 5, 4, 3, 2, 1};
            internalCallback = new ChatCallback(arena, Msg.ARENA_AUTO_START, this, triggers);
        }
    }

    @Override
    public synchronized void start() {
        // Don't start if the arena doesn't actually have a timer
        if (super.getDuration() > 0) {
            super.start();
        }
    }

    @Override
    public void onStart() {
        internalCallback.onStart();
    }

    @Override
    public void onStop() {
        internalCallback.onStop();
    }

    @Override
    public void onTick() {
        // TODO: Remove this if no one reports issues
        if (arena.isRunning() || arena.getPlayersInLobby().isEmpty()) {
            Messenger.severe("AUTO START TIMER WAS NOT STOPPED!");
            Messenger.severe("  Please make a ticket and inform me about this at:");
            Messenger.severe("  http://dev.bukkit.org/bukkit-plugins/mobarena/tickets/");
            stop();
            return;
        }
        internalCallback.onTick();
    }

    @Override
    public void onFinish() {
        arena.forceStart();
    }
}
