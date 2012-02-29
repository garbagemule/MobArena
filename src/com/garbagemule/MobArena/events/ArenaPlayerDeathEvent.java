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
    
    public ArenaPlayerDeathEvent(Player player, Arena arena) {
        this.player = player;
        this.arena =  arena;
    }
    
    public Player getPlayer() {
        return player;
    }
    
    public Arena getArena() {
        return arena;
    }
    
    public HandlerList getHandlers() {
        return handlers;
    }
     
    public static HandlerList getHandlerList() {
        return handlers;
    }
}