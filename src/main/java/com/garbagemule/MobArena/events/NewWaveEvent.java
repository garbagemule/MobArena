package com.garbagemule.MobArena.events;

import com.garbagemule.MobArena.events.api.MobArenaEvent;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.Wave;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@RequiredArgsConstructor
@Getter @Setter
public class NewWaveEvent extends MobArenaEvent {
    private final Arena arena;
    private boolean cancelled;

    private final Wave wave;
    private final int waveNumber;

}
