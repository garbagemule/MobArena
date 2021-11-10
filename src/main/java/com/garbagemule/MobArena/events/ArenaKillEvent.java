package com.garbagemule.MobArena.events;

import com.garbagemule.MobArena.events.api.MobArenaEvent;
import com.garbagemule.MobArena.framework.Arena;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when an arena player kills a mob or another player.
 */
@RequiredArgsConstructor
public class ArenaKillEvent extends MobArenaEvent {
    private final Arena arena;
    private final Player killer;
    private final Entity victim;

    /**
     * Get the arena the event happened in.
     *
     * @return an arena
     */
    public Arena getArena() {
        return arena;
    }

    /**
     * Get the killer.
     *
     * @return the killer
     */
    public Player getPlayer() {
        return killer;
    }

    /**
     * Get the victim.
     *
     * @return the victim
     */
    public Entity getVictim() {
        return victim;
    }

}
