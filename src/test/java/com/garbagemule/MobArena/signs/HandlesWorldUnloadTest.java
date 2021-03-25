package com.garbagemule.MobArena.signs;

import org.bukkit.World;
import org.bukkit.event.world.WorldUnloadEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class HandlesWorldUnloadTest {

    SignStore store;
    Logger log;

    HandlesWorldUnload subject;

    @Before
    public void setup() {
        store = mock(SignStore.class);
        log = mock(Logger.class);

        subject = new HandlesWorldUnload(
            store,
            log
        );
    }

    @Test
    public void logsMessagesOnSignRemoval() {
        UUID id = UUID.fromString("cafebabe-ea75-dead-beef-deadcafebabe");
        String name = "world";
        World world = mock(World.class);
        when(world.getUID()).thenReturn(id);
        when(world.getName()).thenReturn(name);
        List<ArenaSign> signs = Arrays.asList(null, null);
        when(store.removeByWorld(world)).thenReturn(signs);
        WorldUnloadEvent event = new WorldUnloadEvent(world);

        subject.on(event);

        verify(log).info(anyString());
    }

    @Test
    public void logsNothingIfNoSignsRemoved() {
        World world = mock(World.class);
        when(store.removeByWorld(world)).thenReturn(Collections.emptyList());
        WorldUnloadEvent event = new WorldUnloadEvent(world);

        subject.on(event);

        verifyNoInteractions(log);
    }

}
