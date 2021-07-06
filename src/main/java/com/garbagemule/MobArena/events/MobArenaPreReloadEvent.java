package com.garbagemule.MobArena.events;

import com.garbagemule.MobArena.MobArena;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called right before MobArena begins reloading.
 * <p>
 * Use this event to re-register components that need to be set up and in
 * place <i>before</i> MobArena dips into its config-file. This is mostly
 * relevant for plugins that <code>loadbefore</code> MobArena to register
 * {@link com.garbagemule.MobArena.things.ThingParser}s and such.
 * <p>
 * This event is <i>not</i> suitable for working with the "current" state
 * of arenas, classes, etc., because MobArena's state at the time of this
 * event is about to become stale. To work with the "current" state after
 * a reload, use the {@link MobArenaReloadEvent} instead.
 *
 * @see MobArenaReloadEvent
 */
public class MobArenaPreReloadEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final MobArena plugin;

    public MobArenaPreReloadEvent(MobArena plugin) {
        this.plugin = plugin;
    }

    /**
     * Get the current MobArena plugin instance.
     *
     * @return the MobArena plugin instance
     */
    public MobArena getPlugin() {
        return plugin;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
