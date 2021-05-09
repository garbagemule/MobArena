package com.garbagemule.MobArena.signs;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.world.WorldLoadEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class HandlesWorldLoadTest {

    SignDataMigrator migrator;
    SignReader reader;
    SignStore store;
    Logger log;

    HandlesWorldLoad subject;

    @Before
    public void setup() {
        migrator = mock(SignDataMigrator.class);
        reader = mock(SignReader.class);
        store = mock(SignStore.class);
        log = mock(Logger.class);

        subject = new HandlesWorldLoad(
            migrator,
            reader,
            store,
            log
        );
    }

    @Test
    public void errorMessageIfDataMigrationThrows() throws IOException {
        World world = mock(World.class);
        when(world.getName()).thenReturn("world");
        IOException exception = new IOException("it's bad");
        doThrow(exception).when(migrator).migrate(world);
        WorldLoadEvent event = new WorldLoadEvent(world);

        subject.on(event);

        verify(log).log(eq(Level.SEVERE), anyString(), eq(exception));
    }

    @Test
    public void addsLoadedSignsToStore() throws IOException {
        World world = mock(World.class);
        when(world.getName()).thenReturn("world");
        when(world.getUID()).thenReturn(UUID.fromString("cafebabe-ea75-dead-beef-deadcafebabe"));
        Location location = mock(Location.class);
        ArenaSign sign = new ArenaSign(location, "cool-sign", "castle", "join");
        when(reader.read(world)).thenReturn(Collections.singletonList(sign));
        WorldLoadEvent event = new WorldLoadEvent(world);

        subject.on(event);

        verify(store).add(sign);
    }

    @Test
    public void logsMessageOnSignAddition() throws IOException {
        World world = mock(World.class);
        when(world.getName()).thenReturn("world");
        when(world.getUID()).thenReturn(UUID.fromString("cafebabe-ea75-dead-beef-deadcafebabe"));
        Location location = mock(Location.class);
        ArenaSign sign = new ArenaSign(location, "cool-sign", "castle", "join");
        when(reader.read(world)).thenReturn(Collections.singletonList(sign));
        WorldLoadEvent event = new WorldLoadEvent(world);

        subject.on(event);

        verify(log).info(anyString());
    }

    @Test
    public void addsNothingIfNoSignsLoaded() throws IOException {
        World world = mock(World.class);
        when(reader.read(world)).thenReturn(Collections.emptyList());
        WorldLoadEvent event = new WorldLoadEvent(world);

        subject.on(event);

        verifyNoInteractions(store);
    }

    @Test
    public void logsNothingIfNoSignsLoaded() throws IOException {
        World world = mock(World.class);
        when(reader.read(world)).thenReturn(Collections.emptyList());
        WorldLoadEvent event = new WorldLoadEvent(world);

        subject.on(event);

        verifyNoInteractions(log);
    }

    @Test
    public void errorMessageIfReaderThrows() throws IOException {
        World world = mock(World.class);
        IOException exception = new IOException("it's bad");
        when(reader.read(world)).thenThrow(exception);
        WorldLoadEvent event = new WorldLoadEvent(world);

        subject.on(event);

        verify(log).log(eq(Level.SEVERE), anyString(), eq(exception));
    }

}
