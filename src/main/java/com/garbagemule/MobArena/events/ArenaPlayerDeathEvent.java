package com.garbagemule.MobArena.events;

import com.garbagemule.MobArena.events.api.MobArenaEvent;
import com.garbagemule.MobArena.framework.Arena;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@RequiredArgsConstructor
public class ArenaPlayerDeathEvent extends MobArenaEvent {
    private final Player player;
    private final Arena arena;
    private final boolean last;

    public Player getPlayer() {
        return player;
    }

    public Arena getArena() {
        return arena;
    }

    public boolean wasLastPlayerStanding() {
        return last;
    }

}
