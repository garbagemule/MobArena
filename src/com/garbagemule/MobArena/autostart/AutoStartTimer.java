package com.garbagemule.MobArena.autostart;

import com.garbagemule.MobArena.Messenger;
import com.garbagemule.MobArena.Msg;
import com.garbagemule.MobArena.framework.Arena;

public class AutoStartTimer {
    private Arena arena;
    private int seconds;
    private Timer timer;
    private boolean started;
    
    public AutoStartTimer(Arena arena, int seconds) {
        this.arena     = arena;
        this.seconds   = seconds;
        this.started   = false;
    }
    
    /**
     * Starts the timer.
     * The method is idempotent, meaning if the timer was already
     * started, nothing happens if the method is called again.
     */
    public void start() {
        if (seconds > 5 && !started) {
            timer = new Timer(seconds);
            timer.start();
            started = true;
        }
    }
    
    public boolean isRunning() {
        return (timer != null && started);
    }
    
    public int getRemaining() {
        return (timer != null ? timer.getRemaining() : -1);
    }
    
    /**
     * The internal timer class used for the auto-join-timer setting.
     * Using an extra internal object allows the interruption of a current
     * timer, followed by the creation of a new. Thus, no timers should
     * ever interfere with each other.
     */
    private class Timer implements Runnable {
        private int remaining;
        
        private Timer(int seconds) {
            this.remaining   = seconds;
        }
        
        /**
         * Start the timer
         */
        public synchronized void start() {
            arena.scheduleTask(this, 20);
        }
        
        /**
         * Get the remaining number of seconds
         * @return number of seconds left
         */
        public synchronized int getRemaining() {
            return remaining;
        }
    
        @Override
        public void run() {
            synchronized(this) {
                // Abort if the arena is running, or if players have left
                if (arena.isRunning() || arena.getPlayersInLobby().isEmpty()) {
                    started = false;
                    this.notifyAll();
                    return;
                }
                
                // Count down
                remaining--;
                
                // Start if 0
                if (remaining <= 0) {
                    arena.forceStart();
                    started = false;
                } else {                    
                    // Warn at 5 seconds left
                    if (remaining == 5) {
                        Messenger.tellAll(arena, Msg.ARENA_AUTO_START, "5");
                    }
                    
                    // Reschedule
                    arena.scheduleTask(this, 20);
                }
                this.notifyAll();
            }
        }
    }
}
