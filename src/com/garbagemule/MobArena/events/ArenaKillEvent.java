package com.garbagemule.MobArena.events;

import com.garbagemule.MobArena.framework.Arena;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when an arena player kills a mob or another player.
 */
public class ArenaKillEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private Arena arena;
    private Player killer;
    private Entity victim;

    public ArenaKillEvent(Arena arena, Player killer, Entity victim) {
        this.arena  = arena;
        this.killer = killer;
        this.victim = victim;
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

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
