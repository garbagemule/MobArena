package com.garbagemule.MobArena.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.Wave;

public class NewWaveEvent extends Event implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();
    private Arena arena;
    private boolean cancelled;
    
    private Wave wave;
    private int waveNo;
    
    public NewWaveEvent(Arena arena, Wave wave, int waveNo) {
        this.arena  = arena;
        this.wave   = wave;
        this.waveNo = waveNo;
    }

    public Wave getWave() {
        return wave;
    }
    
    public int getWaveNumber() {
        return waveNo;
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
