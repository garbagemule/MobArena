package com.garbagemule.MobArena.signs;

import com.garbagemule.MobArena.Messenger;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import java.util.logging.Level;
import java.util.logging.Logger;

class HandlesSignCreation implements Listener {

    private final SignCreator creator;
    private final SignWriter writer;
    private final SignStore store;
    private final SignRenderer renderer;
    private final Messenger messenger;
    private final Logger log;

    HandlesSignCreation(
        SignCreator creator,
        SignWriter writer,
        SignStore store,
        SignRenderer renderer,
        Messenger messenger,
        Logger log
    ) {
        this.creator = creator;
        this.writer = writer;
        this.store = store;
        this.renderer = renderer;
        this.messenger = messenger;
        this.log = log;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void on(SignChangeEvent event) {
        ArenaSign sign;
        try {
            sign = creator.create(event);
            if (sign == null) {
                return;
            }
        } catch (IllegalArgumentException e) {
            messenger.tell(event.getPlayer(), e.getMessage());
            return;
        }

        try {
            writer.write(sign);
        } catch (Exception e) {
            messenger.tell(event.getPlayer(), "Sign creation failed:\n" + ChatColor.RED + e.getMessage());
            log.log(Level.SEVERE, "Failed to write arena sign to data file", e);
            return;
        }

        store.add(sign);
        renderer.render(sign, event);

        messenger.tell(event.getPlayer(), String.format(
            "New %s sign created for arena %s.",
            ChatColor.YELLOW + sign.type + ChatColor.RESET,
            ChatColor.GREEN + sign.arenaId + ChatColor.RESET
        ));
        log.info(String.format(
            "%s created %s sign for '%s' at (%d,%d,%d) in '%s'.",
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
