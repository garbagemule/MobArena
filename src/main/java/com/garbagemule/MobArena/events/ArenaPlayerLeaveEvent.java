package com.garbagemule.MobArena.events;

import com.garbagemule.MobArena.events.api.MobArenaEvent;
import com.garbagemule.MobArena.framework.Arena;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@RequiredArgsConstructor
public class ArenaPlayerLeaveEvent extends MobArenaEvent {
    private Player player;
    private Arena arena;
    @Getter @Setter
    private boolean cancelled;

    public ArenaPlayerLeaveEvent(Player player, Arena arena) {
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
}
