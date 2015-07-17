package com.garbagemule.MobArena.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.garbagemule.MobArena.framework.Arena;

public class ArenaEndEvent extends Event implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();
    private Arena arena;
    private boolean cancelled;
    
    public ArenaEndEvent(Arena arena) {
        this.arena = arena;
        this.cancelled = false;
    }
    
    public Arena getArena() {
        return arena;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
    
    public HandlerList getHandlers() {
        return handlers;
    }
     
    public static HandlerList getHandlerList() {
        return handlers;
    }
}