package com.garbagemule.MobArena.events;

import com.garbagemule.MobArena.framework.Arena;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.HashSet;
import java.util.Set;

public class ArenaCompleteEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private Arena arena;
    private Set<Player> survivors;

    public ArenaCompleteEvent(Arena arena) {
        this.arena = arena;
        this.survivors = new HashSet<>();
        this.survivors.addAll(arena.getPlayersInArena());
    }

    /**
     * Get the arena the event happened in.
     *
     * @return an arena
     */
    public Arena getArena() {
        return arena;
    }

    /**
     * Get a set of players who survived until the final wave.
     *
     * @return a set of winners
     */
    public Set<Player> getSurvivors() {
        return survivors;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
