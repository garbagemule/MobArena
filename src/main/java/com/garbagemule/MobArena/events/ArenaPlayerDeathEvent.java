package com.garbagemule.MobArena.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.garbagemule.MobArena.framework.Arena;

public class ArenaPlayerDeathEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private Arena arena;
    private boolean last;
    
    public ArenaPlayerDeathEvent(Player player, Arena arena, boolean last) {
        this.player = player;
        this.arena  = arena;
        this.last   = last;
    }
    
    public Player getPlayer() {
        return player;
    }
    
    public Arena getArena() {
        return arena;
    }

    public boolean wasLastPlayerStanding() {
        return last;
    }
    
    public HandlerList getHandlers() {
        return handlers;
    }
     
    public static HandlerList getHandlerList() {
        return handlers;
    }
}