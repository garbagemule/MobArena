package com.garbagemule.MobArena.events;

import com.garbagemule.MobArena.MobArena;
import com.garbagemule.MobArena.events.api.MobArenaEvent;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called right after MobArena has reloaded.
 * <p>
 * The primary goal of this event is to allow extension plugins to "ride
 * along" with the <code>/ma reload</code> command so they don't have to
 * implement their own commands. <i>One command to rule them all.</i>
 * <p>
 * This event is useful if you need to work with arenas, classes, etc. in
 * their "current" state. This is typical of plugins that (soft)depend on
 * MobArena and use its API after the initialization phase.
 * <p>
 * This event is <i>not</i> suitable for re-registering components that
 * need to be in place <i>before</i> MobArena reloads. For that, see the
 * {@link MobArenaPreReloadEvent}.
 *
 * @see MobArenaPreReloadEvent
 */
@RequiredArgsConstructor
public class MobArenaReloadEvent extends MobArenaEvent {

    private final MobArena plugin;

    /**
     * Get the current MobArena plugin instance.
     *
     * @return the MobArena plugin instance
     */
    public MobArena getPlugin() {
        return plugin;
    }


}
