package com.garbagemule.MobArena.signs;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldUnloadEvent;

import java.util.List;
import java.util.logging.Logger;

class HandlesWorldUnload implements Listener {

    private final SignStore store;
    private final Logger log;

    HandlesWorldUnload(
        SignStore store,
        Logger log
    ) {
        this.store = store;
        this.log = log;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void on(WorldUnloadEvent event) {
        World world = event.getWorld();
        List<ArenaSign> removed = store.removeByWorld(world);
        if (!removed.isEmpty()) {
            log.info(removed.size() + " arena sign(s) unloaded due to unloading of world '" + world.getName() + "' (" + world.getUID().toString() + ").");
        }
    }

}
