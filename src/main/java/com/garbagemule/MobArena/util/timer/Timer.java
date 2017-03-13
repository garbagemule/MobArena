package com.garbagemule.MobArena.util.timer;

/**
 * Generic Timer interface for various implementations of timers.
 * <p>
 * The public facing methods of timers provide means of starting and stopping
 * the timers, as well as getting and setting tick intervals, and setting
 * callbacks post-construction.
 * <p>
 * A timer is expected to call methods on an underlying {@link TimerCallback}
 * object (when appropriate), which is provided either in the construction of
 * the timer, or via the {@link #setCallback(TimerCallback)} method after the
 * timer has been constructed with a null callback.
 * <p>
 * Conditions for callbacks:
 * <ul>
 *     <li>When a timer is started via its {@link #start()} method, it calls
 *         {@code onStart()} on the callback.
 *     <li>If a timer is manually stopped, it calls {@code onStop()} on the
 *         callback when its {@link #stop()} method is called.
 *     <li>If a timer can "tick", it calls {@code onTick()} on the callback
 *         every time it ticks.
 *     <li>If a timer can finish ("run out"), it calls the {@code onFinish()}
 *         method on the callback when it finishes.
 * </ul>
 * A timer must support manual stopping, i.e. it must be possible to stop a
 * timer, even if it can "run out". Ticking and finishing is optional, as it
 * may not always be relevant, depending on the implementation.
 */
public interface Timer {
    /**
     * Start the timer.
     */
    void start();

    /**
     * Stop the timer.
     */
    void stop();

    /**
     * Check if the timer is running.
     *
     * @return true, if the timer is currently running, false otherwise
     */
    boolean isRunning();

    /**
     * Set the callback object of the timer.
     * <p>
     * This is a convenience method that allows for creating a callback that
     * references the timer. Due to Java's "local variable may not have been
     * initialized" rule, an anonymous callback cannot reference its timer
     * host, unless the host is a field or a final variable, and creating the
     * callback in the same statement as the timer means the timer has not
     * yet been initialized, according to Java. As such, if the callback
     * references the timer (e.g. to restart it or stop it prematurely), the
     * callback must be set with this method after creating the timer with
     * a null callback in the constructor.
     *
     * @param callback a callback object; must be non-null
     * @throws IllegalArgumentException if the callback is null
     * @throws IllegalStateException if the callback has already been set
     */
    void setCallback(TimerCallback callback);

    /**
     * Get the tick interval of the timer.
     *
     * @return the tick interval of the timer
     */
    long getInterval();

    /**
     * Set the tick interval of the timer.
     * <p>
     * The tick interval may be changed on-the-fly, but will not take effect
     * until after the next tick. As such, changing the tick interval after
     * the timer has been started without specifying the tick interval in the
     * constructor will have no effect.
     *
     * @param interval tick interval of the timer; must be positive
     * @throws IllegalArgumentException if the value is non-positive
     */
    void setInterval(long interval);
}
