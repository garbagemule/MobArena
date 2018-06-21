package com.garbagemule.MobArena.signs;

import com.garbagemule.MobArena.MobArena;
import com.garbagemule.MobArena.events.*;
import com.garbagemule.MobArena.framework.Arena;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitScheduler;

class RedrawsSignsOnUpdates implements Listener {

    private final RedrawsArenaSigns redrawsArenaSigns;
    private final BukkitScheduler scheduler;
    private final MobArena plugin;

    RedrawsSignsOnUpdates(
        RedrawsArenaSigns redrawsArenaSigns,
        MobArena plugin
    ) {
        this.redrawsArenaSigns = redrawsArenaSigns;
        this.scheduler = plugin.getServer().getScheduler();
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void on(ArenaPlayerJoinEvent event) {
        handle(event.getArena());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void on(ArenaPlayerLeaveEvent event) {
        handle(event.getArena());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void on(ArenaPlayerReadyEvent event) {
        handle(event.getArena());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void on(ArenaStartEvent event) {
        handle(event.getArena());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void on(NewWaveEvent event) {
        handle(event.getArena());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void on(ArenaPlayerDeathEvent event) {
        handle(event.getArena());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void on(ArenaEndEvent event) {
        handle(event.getArena());
    }

    private void handle(Arena arena) {
        scheduler.runTask(plugin, () -> redrawsArenaSigns.redraw(arena));
    }

}
