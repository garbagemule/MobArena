package com.garbagemule.MobArena.signs;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

class HandlesSignClicks implements Listener {

    private static final long COOLDOWN_TIME = 500;

    private final SignStore signStore;
    private final InvokesSignAction invokesSignAction;
    private final Map<UUID, Long> cooldowns;

    HandlesSignClicks(
        SignStore signStore,
        InvokesSignAction invokesSignAction
    ) {
        this.signStore = signStore;
        this.invokesSignAction = invokesSignAction;
        this.cooldowns = new HashMap<>();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void on(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }
        if (!(block.getState() instanceof Sign)) {
            return;
        }

        ArenaSign sign = signStore.findByLocation(block.getLocation());
        if (sign != null) {
            event.setCancelled(true);
            purgeAndInvoke(sign, event.getPlayer());
        }
    }

    private void purgeAndInvoke(ArenaSign sign, Player player) {
        long now = System.currentTimeMillis();
        cooldowns.values().removeIf(time -> time < now);

        cooldowns.computeIfAbsent(player.getUniqueId(), id -> {
            invokesSignAction.invoke(sign, player);
            return now + COOLDOWN_TIME;
        });
    }

}
