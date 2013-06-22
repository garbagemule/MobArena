package com.garbagemule.MobArena.autostart;

import org.bukkit.entity.Player;

import com.garbagemule.MobArena.Messenger;
import com.garbagemule.MobArena.Msg;
import com.garbagemule.MobArena.framework.Arena;

public class AutoStartTimer {
    private Arena arena;
    private int seconds;
    private Timer timer;
    private boolean started;
    private boolean useLevels;
    
    public AutoStartTimer(Arena arena, int seconds) {
        this.arena     = arena;
        this.seconds   = seconds;
        this.started   = false;
        this.useLevels = arena.getSettings().getBoolean("display-timer-as-level", false);
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
        private int countdownIndex;
        private int[] intervals = new int[]{1, 2, 3, 4, 5, 10, 30};
        
        private Timer(int seconds) {
            this.remaining = seconds;
            
            // Find the first countdown announcement value
            for (int i = 0; i < intervals.length; i++) {
                if (seconds > intervals[i]) {
                    countdownIndex = i;
                } else {
                    break;
                }
            }
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
                    // If using levels, update 'em
                    if (useLevels) {
                        for (Player p : arena.getPlayersInLobby()) {
                            p.setLevel(remaining);
                        }
                    }
                    // Otherwise, warn at x seconds left
                    else if (remaining == intervals[countdownIndex]) {
                        Messenger.tellAll(arena, Msg.ARENA_AUTO_START, "" + remaining);
                        countdownIndex--;
                    }
                    
                    // Reschedule
                    arena.scheduleTask(this, 20);
                }
                this.notifyAll();
            }
        }
    }
}
