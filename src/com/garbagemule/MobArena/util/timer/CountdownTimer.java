package com.garbagemule.MobArena.util.timer;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

/**
 * A simple implementation of a generic countdown timer, which has an initial
 * duration and a tick interval.
 * <p>
 * Every time the tick interval has passed, the {@code onTick()} method on the
 * underlying {@link TimerCallback} is called. If the tick interval is equal
 * to the timer duration, {@code onTick()} is never called.
 * <p>
 * When the timer "runs out" (when the duration has passed), the timer calls
 * the {@code onFinish()} method on the callback. In case that the duration
 * is not divisible by the tick interval, the final interval will be shorter
 * than the previous intervals to make sure the timer ends no later than it
 * should.
 * <p>
 * Stopping the timer prematurely via the {@link #stop()} method causes the
 * timer to immediately call the {@code onStop()} method on the callback,
 * and any subsequent ticks will be ignored. The timer supports stopping and
 * (re)starting in the same tick.
 */
public class CountdownTimer extends AbstractTimer {
    private long duration;
    private long remaining;

    private Timer timer;

    /**
     * Create a CountdownTimer that will call the {@code onTick()} method on
     * the callback every {@code interval} ticks. Furthermore, the timer will
     * either call the {@code onFinish()} method on the callback when it ends,
     * or the {@code onStop()} method if the timer is stopped prematurely.
     *
     * @param plugin   the plugin responsible for the timer
     * @param duration the duration of the timer; must be non-negative
     * @param interval the amount of ticks between each {@code onTick()} call
     *                 on the callback object; must be positive and less than
     *                 or equal to {@code duration}
     * @param callback a callback object
     */
    public CountdownTimer(Plugin plugin, long duration, long interval, TimerCallback callback) {
        super(plugin, interval, callback);

        setDuration(duration);
        this.remaining = 0l;
        this.timer = null;
    }

    /**
     * Create a CountdownTimer with the given duration and tick interval.
     * <p>
     * This constructor leaves the timer in an inconsistent state until the
     * {@link #setCallback(TimerCallback)} method is called with a valid
     * callback object.
     *
     * @param plugin   the plugin responsible for the timer
     * @param duration the duration of the timer; must be non-negative
     * @param interval the amount of ticks between each {@code onTick()} call
     *                 on the callback object; must be positive and less than
     *                 or equal to {@code duration}
     */
    public CountdownTimer(Plugin plugin, long duration, long interval) {
        this(plugin, duration, interval, null);
    }

    /**
     * Create a CountdownTimer that will never tick. The timer will either
     * call the {@code onFinish()} method on the callback when it ends, or
     * the {@code onStop()} method if the timer is stopped prematurely.
     *
     * @param plugin   the plugin responsible for the timer
     * @param duration the duration of the timer; must be non-negative
     * @param callback a callback object
     */
    public CountdownTimer(Plugin plugin, long duration, TimerCallback callback) {
        this(plugin, duration, duration, callback);
    }

    /**
     * Create a CountdownTimer that will never tick. The timer will either
     * call the {@code onFinish()} method on the callback when it ends, or
     * the {@code onStop()} method if the timer is stopped prematurely.
     * <p>
     * This constructor leaves the timer in an inconsistent state until the
     * {@link #setCallback(TimerCallback)} method is called with a valid
     * callback object.
     *
     * @param plugin   the plugin responsible for the timer
     * @param duration the duration of the timer; must be non-negative
     */
    public CountdownTimer(Plugin plugin, long duration) {
        this(plugin, duration, duration, null);
    }

    /**
     * Create an uninitialized (0 duration) CountdownTimer.
     * <p>
     * This constructor leaves the timer in an inconsistent state until the
     * {@link #setCallback(TimerCallback)} method is called with a valid
     * callback object.
     * <p>
     * The CountdownTimer acts as a Null Object until a positive duration
     * is set via the {@link #setDuration(long)} method.
     *
     * @param plugin the plugin responsible for the timer
     */
    public CountdownTimer(Plugin plugin) {
        this(plugin, 0, 1, null);
    }

    /**
     * Start the timer.
     * <p>
     * The timer will start counting down from the duration specified in the
     * constructor, and count down {@code interval} ticks every time it ticks,
     * as well as call the {@code onTick()} method on the callback.
     * <p>
     * If no interval was provided in the constructor, the timer will never
     * call the {@code onTick()} method, but only the {@code onFinish()} when
     * the timer runs out, or the {@code onStop()} method, if the timer is
     * stopped prematurely via the {@link #stop()} method.
     */
    @Override
    public synchronized void start() {
        if (timer != null) {
            return;
        }
        remaining = duration;
        callback.onStart();
        timer = new Timer();
    }

    /**
     * Stop the timer prematurely.
     * <p>
     * This will call the {@code onStop()} method on the callback, and reset
     * the timer to a state in which calling the {@link #start()} method will
     * restart the timer.
     */
    @Override
    public synchronized void stop() {
        if (timer == null) {
            return;
        }
        timer.stop();
        timer = null;
        remaining = 0l;
        callback.onStop();
    }

    /** {@inheritDoc} */
    @Override
    public synchronized boolean isRunning() {
        return timer != null;
    }

    /**
     * Get the duration of the timer.
     *
     * @return the duration of the timer in server ticks
     */
    public synchronized long getDuration() {
        return duration;
    }

    /**
     * Set the duration of the timer.
     * <p>
     * This method should only be used to set the duration post-construction
     * if it is inconvenient (or impossible) to set it during construction.
     * <p>
     * Changing the duration while the timer is running is not recommended,
     * because external classes may depend on it remaining constant.
     *
     * @param duration the duration of the timer; must be non-negative
     */
    public synchronized void setDuration(long duration) {
        if (duration < 0l) {
            throw new IllegalArgumentException("Duration must be non-negative: " + duration);
        }
        this.duration = duration;
    }

    /**
     * Get the remaining number of ticks before this timer runs out.
     *
     * @return the remaining number of server ticks
     */
    public synchronized long getRemaining() {
        return remaining;
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
            synchronized (CountdownTimer.this) {
                remaining -= interval;

                // If we're done, null timer, call onFinish(), and bail
                if (remaining <= 0l) {
                    timer = null;
                    callback.onFinish();
                    return;
                }

                // Otherwise, tick
                callback.onTick();

                // If stop() was called from onTick(), don't reschedule
                if (task != null) {
                    reschedule();
                }
            }
        }

        public synchronized void stop() {
            task.cancel();
            task = null;
        }

        private synchronized void reschedule() {
            // Make sure the timer stops on time
            long nextInterval = (remaining < interval) ? remaining : interval;
            task = Bukkit.getScheduler().runTaskLater(plugin, this, nextInterval);
        }
    }
}
