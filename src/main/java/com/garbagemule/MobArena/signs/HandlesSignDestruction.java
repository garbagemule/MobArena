package com.garbagemule.MobArena.signs;

import com.garbagemule.MobArena.Messenger;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

class HandlesSignDestruction implements Listener {

    private final RemovesSignAtLocation removesSignAtLocation;
    private final Messenger messenger;

    HandlesSignDestruction(
        RemovesSignAtLocation removesSignAtLocation,
        Messenger messenger
    ) {
        this.removesSignAtLocation = removesSignAtLocation;
        this.messenger = messenger;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void on(BlockBreakEvent event) {
        Location location = event.getBlock().getLocation();
        removesSignAtLocation.remove(location)
            .ifPresent(sign -> messenger.tell(
                event.getPlayer(),
                "Removed " + sign.type + " sign for arena " + sign.arenaId
            ));
    }

}
