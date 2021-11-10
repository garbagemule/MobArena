package com.garbagemule.MobArena.events.api;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public abstract class MobArenaEvent extends Event {

    private static final HandlerList handlers = new HandlerList();


    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
