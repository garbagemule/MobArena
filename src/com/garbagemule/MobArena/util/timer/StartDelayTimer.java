package com.garbagemule.MobArena.util.timer;

import com.garbagemule.MobArena.Messenger;
import com.garbagemule.MobArena.Msg;
import com.garbagemule.MobArena.framework.Arena;

/**
 * The StartDelayTimer is a self-contained CountdownTimer (i.e. it is its own
 * callback), which prevents an arena from starting until the timer finishes.
 * It contains an instance of the arena's auto-start-timer, and will start it
 * when the start-delay is over.
 * <p>
 * The timer is preprogrammed with a series of tick interval triggers, such
 * that the timer only ticks when the time remaining matches one of these
 * triggers. When the timer ticks, the lobby players are informed about the
 * time remaining, and when the timer finishes, the arena's auto-start-timer
 * is started. If the {@code display-timer-as-level} flag is set, the timer
 * will tick once per second, updating the player levels.
 * <p>
 * The timer realizes a semi-"Null Object" pattern if the duration (i.e. the
 * value of the start-delay-timer setting) is 0, where calling the timer's
 * {@link #start()} method only starts the auto-start-timer.
 */
public class StartDelayTimer extends CountdownTimer implements TimerCallback {
    private Arena arena;
    private CountdownTimer autoStartTimer;
    private TimerCallback internalCallback;

    /**
     * Create a StartDelayTimer for the given arena.
     * <p>
     * The duration is determined from the {@code start-delay-timer} arena
     * setting. If the value is non-positive, the timer acts as a type of
     * Null Object, allowing it to be safely used for arenas that do not
     * have a start-delay-timer, skipping the start-delay and going directly
     * to the auto-start-timer.
     *
     * @param arena the arena the timer is responsible for
     * @param autoStartTimer the auto-start-timer of the arena
     */
    public StartDelayTimer(Arena arena, CountdownTimer autoStartTimer) {
        super(arena.getPlugin());
        super.setCallback(this);

        this.arena = arena;
        this.autoStartTimer = autoStartTimer;

        // Set the duration
        long duration = arena.getSettings().getInt("start-delay-timer", 0) * 20l;
        setDuration(Math.max(0l, duration));

        // Choose level- or chat-callback
        boolean level = arena.getSettings().getBoolean("display-timer-as-level", false);
        if (level) {
            internalCallback = new LevelCallback(arena, this);
        } else {
            int[] triggers = {30, 10, 5, 4, 3, 2, 1};
            internalCallback = new ChatCallback(arena, Msg.ARENA_START_DELAY, this, triggers);
        }
    }

    @Override
    public synchronized void start() {
        // Start auto-start-timer if arena has no start-delay
        if (super.getDuration() > 0) {
            super.start();
        } else {
            // Idempotent
            autoStartTimer.start();
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
            Messenger.severe("START DELAY TIMER WAS NOT STOPPED!");
            Messenger.severe("  Please make a ticket and inform me about this at:");
            Messenger.severe("  http://dev.bukkit.org/bukkit-plugins/mobarena/tickets/");
            stop();
            return;
        }
        internalCallback.onTick();
    }

    @Override
    public void onFinish() {
        // Start either the arena or the auto-start-timer
        if (!arena.startArena()) {
            autoStartTimer.start();

            // Notify players of auto-start-timer duration
            if (autoStartTimer.isRunning()) {
                Messenger.announce(arena, Msg.ARENA_AUTO_START, "" + autoStartTimer.getRemaining() / 20l);
            }
        }
    }
}
