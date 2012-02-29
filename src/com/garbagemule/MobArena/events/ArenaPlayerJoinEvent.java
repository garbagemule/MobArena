package com.garbagemule.MobArena.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.garbagemule.MobArena.framework.Arena;

public class ArenaPlayerJoinEvent extends Event implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private Arena arena;
    private boolean cancelled;
    
    public ArenaPlayerJoinEvent(Player player, Arena arena) {
        this.player = player;
        this.arena =  arena;
        this.cancelled = false;
    }
    
    public Player getPlayer() {
        return player;
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