package com.garbagemule.MobArena.events;

import com.garbagemule.MobArena.events.api.MobArenaEvent;
import com.garbagemule.MobArena.framework.Arena;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@RequiredArgsConstructor
public class ArenaEndEvent extends MobArenaEvent {
    private final Arena arena;
    @Getter @Setter
    private boolean cancelled;

    public Arena getArena() {
        return arena;
    }

}
