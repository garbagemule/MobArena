package com.garbagemule.MobArena.util.timer;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

/**
 * A simple implementation of a generic stopwatch timer, which periodically
 * ticks according to a given tick interval.
 * <p>
 * Every time the tick interval has passed, the {@code onTick()} method on the
 * underlying {@link TimerCallback} is called.
 * <p>
 * This timer never runs out, and it must be manually stopped via the
 * {@link #stop()} method, which causes the timer to immediately call the
 * {@code onStop()} method on the callback, and any subsequent ticks will be
 * ignored. The timer supports stopping and (re)starting in the same tick.
 */
public class StopwatchTimer extends AbstractTimer {
    private Timer timer;

    /**
     * Create a StopwatchTimer that will call the {@code onTick()} method on
     * the callback every {@code interval} ticks. Furthermore, the timer will
     * either call the {@code onFinish()} method on the callback when it ends,
     * or the {@code onStop()} method if the timer is stopped prematurely.
     *
     * @param plugin   the plugin responsible for the timer
     * @param interval the amount of ticks between each {@code onTick()} call
     *                 on the callback object; must be positive
     * @param callback a callback object
     */
    public StopwatchTimer(Plugin plugin, long interval, TimerCallback callback) {
        super(plugin, interval, callback);

        this.timer = null;
    }

    /**
     * Create a StopwatchTimer with the given tick interval.
     * <p>
     * This constructor leaves the timer in an inconsistent state until the
     * {@link #setCallback(TimerCallback)} method is called with a valid
     * callback object.
     *
     * @param plugin   the plugin responsible for the timer
     * @param interval the amount of ticks between each {@code onTick()} call
     *                 on the callback object provided later; must be positive
     */
    public StopwatchTimer(Plugin plugin, long interval) {
        this(plugin, interval, null);
    }

    /**
     * Start the timer.
     * <p>
     * The timer will start ticking every {@code interval} ticks, as given in
     * the constructor, calling the {@code onTick()} method on the callback
     * on every tick.
     * <p>
     * The timer will continue ticking until manually stopped via the timer's
     * {@link #stop()} method.
     */
    @Override
    public void start() {
        if (timer != null) {
            return;
        }
        callback.onStart();
        timer = new Timer();
    }

    /**
     * Stop the timer.
     * <p>
     * This will call the {@code onStop()} method on the callback, and reset
     * the timer to a state in which calling the {@link #start()} method will
     * restart the timer.
     */
    @Override
    public void stop() {
        if (timer == null) {
            return;
        }
        timer.stop();
        timer = null;
        callback.onStop();
    }

    /** {@inheritDoc} */
    @Override
    public synchronized boolean isRunning() {
        return timer != null;
    }

    /**
     * Internal timer class for the actual legwork. The timer will reschedule
     * itself after every tick, if rescheduling is applicable. Furthermore,
     * the timer will auto-start on creation to avoid having to schedule it
     * from the {@link #start()} method.
     */
    private class Timer implements Runnable {
        private BukkitTask task;

        public Timer() {
            reschedule();
        }

        @Override
        public void run() {
            // Tick
            callback.onTick();

            // If stop() was called from onTick(), don't reschedule
            if (task != null) {
                reschedule();
            }
        }

        public synchronized void stop() {
            task.cancel();
            task = null;
        }

        private synchronized void reschedule() {
            task = Bukkit.getScheduler().runTaskLater(plugin, this, interval);
        }
    }
}
