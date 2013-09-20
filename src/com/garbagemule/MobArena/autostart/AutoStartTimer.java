package com.garbagemule.MobArena.autostart;

import com.garbagemule.MobArena.MobArena;
import com.garbagemule.MobArena.Msg;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.garbagemule.MobArena.Messenger;
import com.garbagemule.MobArena.framework.Arena;
import org.bukkit.scheduler.BukkitRunnable;

public class AutoStartTimer {
    private MobArena plugin;
    private Arena arena;
    private int seconds;
    private Timer timer;
    private boolean useLevels;
    
    public AutoStartTimer(Arena arena, int seconds) {
        this.plugin    = arena.getPlugin();
        this.arena     = arena;
        this.seconds   = seconds;
        this.useLevels = arena.getSettings().getBoolean("display-timer-as-level", false);
    }
    
    /**
     * Starts the timer.
     * The method is idempotent, meaning if the timer was already
     * started, nothing happens if the method is called again.
     */
    public void start() {
        if (seconds > 5 && timer == null) {
            timer = new Timer(seconds);
            timer.runTaskTimer(plugin, 20, 20);
        }
    }

    /**
     * Stops the timer.
     */
    public void stop() {
        if (timer != null) {
            timer.stop();
        }
    }
    
    public boolean isRunning() {
        return (timer != null);
    }
    
    public int getRemaining() {
        return (isRunning() ? timer.getRemaining() : -1);
    }
    
    /**
     * The internal timer class used for the auto-join-timer setting.
     * Using an extra internal object allows the interruption of a current
     * timer, followed by the creation of a new. Thus, no timers should
     * ever interfere with each other.
     */
    private class Timer extends BukkitRunnable {
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
         * Get the remaining number of seconds
         * @return number of seconds left
         */
        public int getRemaining() {
            return remaining;
        }

        public void stop() {
            cancel();
            AutoStartTimer.this.timer = null;
        }
    
        @Override
        public void run() {
            // Abort if the arena is running, or if players have left
            if (arena.isRunning() || arena.getPlayersInLobby().isEmpty()) {
                stop();
                return;
            }

            // Count down and start if 0
            if (--remaining <= 0) {
                stop();
                arena.forceStart();
                return;
            }

            // If using levels, update 'em
            if (useLevels) {
                for (Player p : arena.getPlayersInLobby()) {
                    p.setLevel(remaining);
                }
            }
            // Otherwise, warn at x seconds left
            else if (remaining == intervals[countdownIndex]) {
                Messenger.announce(arena, Msg.ARENA_AUTO_START, String.valueOf(remaining));
                countdownIndex--;
            }
        }
    }
}
