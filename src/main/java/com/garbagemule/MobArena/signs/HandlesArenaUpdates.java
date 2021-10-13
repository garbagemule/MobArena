package com.garbagemule.MobArena.signs;

import com.garbagemule.MobArena.MobArena;
import com.garbagemule.MobArena.events.ArenaEndEvent;
import com.garbagemule.MobArena.events.ArenaPlayerDeathEvent;
import com.garbagemule.MobArena.events.ArenaPlayerJoinEvent;
import com.garbagemule.MobArena.events.ArenaPlayerLeaveEvent;
import com.garbagemule.MobArena.events.ArenaPlayerReadyEvent;
import com.garbagemule.MobArena.events.ArenaStartEvent;
import com.garbagemule.MobArena.events.NewWaveEvent;
import com.garbagemule.MobArena.framework.Arena;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.List;

class HandlesArenaUpdates implements Listener {

    private final SignStore signStore;
    private final SignRenderer signRenderer;
    private final BukkitScheduler scheduler;
    private final MobArena plugin;

    HandlesArenaUpdates(
        SignStore signStore,
        SignRenderer signRenderer,
        MobArena plugin
    ) {
        this.signStore = signStore;
        this.signRenderer = signRenderer;
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
        scheduler.runTask(plugin, () -> {
            List<ArenaSign> signs = signStore.findByArenaId(arena.getSlug());
            signs.forEach(signRenderer::render);
        });
    }

}
