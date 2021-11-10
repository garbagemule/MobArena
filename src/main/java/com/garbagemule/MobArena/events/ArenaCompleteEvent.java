package com.garbagemule.MobArena.events;

import com.garbagemule.MobArena.events.api.MobArenaEvent;
import com.garbagemule.MobArena.framework.Arena;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.HashSet;
import java.util.Set;

public class ArenaCompleteEvent extends MobArenaEvent {
    private final Arena arena;
    private final Set<Player> survivors;

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
}
