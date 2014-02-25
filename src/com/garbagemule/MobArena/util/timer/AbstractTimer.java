package com.garbagemule.MobArena.util.timer;

import org.bukkit.plugin.Plugin;

public abstract class AbstractTimer implements Timer {
    protected Plugin plugin;
    protected TimerCallback callback;
    protected long interval;

    public AbstractTimer(Plugin plugin, long interval, TimerCallback callback) {
        this.plugin = plugin;
        this.callback = callback;

        setInterval(interval);
    }

    @Override
    public void setCallback(TimerCallback callback) {
        if (callback == null) {
            throw new IllegalArgumentException("Callback may not be null.");
        }
        if (this.callback != null) {
            throw new IllegalStateException("Timer already has a callback.");
        }
        this.callback = callback;
    }

    @Override
    public long getInterval() {
        return interval;
    }

    @Override
    public void setInterval(long interval) {
        if (interval <= 0l) {
            throw new IllegalArgumentException("Tick interval must be positive: " + interval);
        }
        this.interval = interval;
    }
}
