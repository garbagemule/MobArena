package com.garbagemule.MobArena.util.timer;

public interface TimerCallback {
    /**
     * Called when the timer is started.
     * <p>
     * Note that this method is called before the timer is first scheduled,
     * which means the interval can be changed within this method prior to
     * the timer actually starting.
     */
    public void onStart();

    /**
     * Called when the timer ticks.
     * <p>
     * Ticks are implementation-specific. Refer to the documentation of the
     * specific timer for details.
     */
    public void onTick();

    /**
     * Called when the timer finishes.
     * <p>
     * A timer finishes when it "runs out", which is implementation-specific.
     * For example, the {@link CountdownTimer} finishes when it has counted
     * down to 0.
     */
    public void onFinish();

    /**
     * Called when the timer is stopped prematurely.
     * <p>
     * Stopping a timer prematurely is different from the timer naturally
     * running out, however some timers may never run out and must be
     * stopped manually, in which case this method is called instead of
     * the {@code onFinish()} method.
     */
    public void onStop();
}
