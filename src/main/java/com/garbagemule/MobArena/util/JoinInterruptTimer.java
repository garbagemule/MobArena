package com.garbagemule.MobArena.util;

import com.garbagemule.MobArena.Msg;
import com.garbagemule.MobArena.framework.Arena;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class JoinInterruptTimer {
    private Set<UUID> waiting;

    public JoinInterruptTimer() {
        this.waiting = new HashSet<>();
    }

    public boolean isWaiting(Player player) {
        return waiting.contains(player.getUniqueId());
    }

    public boolean start(Player player, Arena arena, int seconds, Runnable completed) {
        if (isWaiting(player)) {
            return false;
        }

        UUID id = player.getUniqueId();
        waiting.add(id);

        TimedInterruptListener listener = new TimedInterruptListener(
            player,
            arena,
            () -> waiting.remove(id),
            completed
        );
        listener.runTaskLater(arena.getPlugin(), seconds * 20);
        Bukkit.getPluginManager().registerEvents(listener, arena.getPlugin());

        return true;
    }

    static class TimedInterruptListener extends BukkitRunnable implements Listener {
        Player player;
        Location location;
        Arena arena;
        Runnable stopped;
        Runnable completed;

        boolean done;

        TimedInterruptListener(Player player, Arena arena, Runnable stopped, Runnable completed) {
            this.player = player;
            this.location = player.getLocation();
            this.arena = arena;
            this.stopped = stopped;
            this.completed = completed;

            this.done = false;
        }

        @Override
        public void run() {
            complete();
        }

        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
        public void on(EntityDamageEvent event) {
            if (done || !event.getEntity().equals(player)) {
                return;
            }
            arena.getMessenger().tell(player, Msg.JOIN_INTERRUPTED_BY_DAMAGE);
            interrupt();
        }

        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
        public void on(PlayerMoveEvent event) {
            if (done || !event.getPlayer().equals(player)) {
                return;
            }
            if (location.getWorld() == event.getTo().getWorld() && location.distanceSquared(event.getTo()) < 1) {
                return;
            }
            arena.getMessenger().tell(player, Msg.JOIN_INTERRUPTED_BY_MOVEMENT);
            interrupt();
        }

        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
        public void on(PlayerQuitEvent event) {
            if (done || !event.getPlayer().equals(player)) {
                return;
            }
            interrupt();
        }

        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
        public void on(PlayerKickEvent event) {
            if (done || !event.getPlayer().equals(player)) {
                return;
            }
            interrupt();
        }

        void complete() {
            if (done) {
                return;
            }
            done = true;

            try {
                HandlerList.unregisterAll(this);
                stopped.run();
                completed.run();
            } finally {
                clear();
            }
        }

        void interrupt() {
            if (done) {
                return;
            }
            done = true;

            try {
                HandlerList.unregisterAll(this);
                stopped.run();
                super.cancel();
            } catch (IllegalStateException e) {
                // Swallow this exception from super.cancel()
            } finally {
                clear();
            }
        }

        void clear() {
            player = null;
            location = null;
            arena = null;
            stopped = null;
            completed = null;
        }
    }
}
