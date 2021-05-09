package com.garbagemule.MobArena.signs;

import com.garbagemule.MobArena.Messenger;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.logging.Level;
import java.util.logging.Logger;

class HandlesSignDestruction implements Listener {

    private final SignStore store;
    private final SignWriter writer;
    private final Messenger messenger;
    private final Logger log;

    HandlesSignDestruction(
        SignStore store,
        SignWriter writer,
        Messenger messenger,
        Logger log
    ) {
        this.store = store;
        this.writer = writer;
        this.messenger = messenger;
        this.log = log;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void on(BlockBreakEvent event) {
        Location location = event.getBlock().getLocation();

        ArenaSign sign = store.removeByLocation(location);
        if (sign == null) {
            return;
        }

        try {
            writer.erase(sign);
        } catch (Exception e) {
            messenger.tell(event.getPlayer(), "Sign destruction failed:\n" + ChatColor.RED + e.getMessage());
            log.log(Level.SEVERE, "Failed to erase arena sign from data file", e);
            return;
        }

        messenger.tell(event.getPlayer(), String.format(
            "Removed %s sign for arena %s.",
            ChatColor.YELLOW + sign.type + ChatColor.RESET,
            ChatColor.GREEN + sign.arenaId + ChatColor.RESET
        ));
        log.info(String.format(
            "%s destroyed %s sign for '%s' at (%d,%d,%d) in '%s'.",
            event.getPlayer().getName(),
            sign.type,
            sign.arenaId,
            sign.location.getBlockX(),
            sign.location.getBlockY(),
            sign.location.getBlockZ(),
            sign.location.getWorld().getName()
        ));
    }

}
