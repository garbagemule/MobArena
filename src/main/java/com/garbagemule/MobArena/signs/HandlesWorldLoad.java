package com.garbagemule.MobArena.signs;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

class HandlesWorldLoad implements Listener {

    private final SignDataMigrator migrator;
    private final SignReader reader;
    private final SignStore store;
    private final Logger log;

    HandlesWorldLoad(
        SignDataMigrator migrator,
        SignReader reader,
        SignStore store,
        Logger log
    ) {
        this.migrator = migrator;
        this.reader = reader;
        this.store = store;
        this.log = log;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void on(WorldLoadEvent event) {
        World world = event.getWorld();

        try {
            migrator.migrate(world);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Failed to migrate sign data for world '" + world.getName() + "'", e);
        }

        List<ArenaSign> loaded;
        try {
            loaded = reader.read(world);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Failed to read from arena sign data file", e);
            return;
        }

        if (!loaded.isEmpty()) {
            loaded.forEach(store::add);
            log.info(loaded.size() + " arena sign(s) loaded due to loading of world '" + world.getName() + "' (" + world.getUID().toString() + ").");
        }
    }

}
