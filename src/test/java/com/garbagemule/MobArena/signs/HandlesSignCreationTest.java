package com.garbagemule.MobArena.signs;

import com.garbagemule.MobArena.Messenger;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class HandlesSignCreationTest {

    SignCreator creator;
    SignWriter writer;
    SignStore store;
    SignRenderer renderer;
    Messenger messenger;
    Logger log;

    HandlesSignCreation subject;

    @Before
    public void setup() {
        creator = mock(SignCreator.class);
        writer = mock(SignWriter.class);
        store = mock(SignStore.class);
        renderer = mock(SignRenderer.class);
        messenger = mock(Messenger.class);
        log = mock(Logger.class);

        subject = new HandlesSignCreation(
            creator,
            writer,
            store,
            renderer,
            messenger,
            log
        );
    }

    @Test
    public void noSignCreationNoAction() {
        SignChangeEvent event = new SignChangeEvent(null, null, null);
        when(creator.create(event)).thenReturn(null);

        subject.on(event);

        verifyNoInteractions(writer, store, messenger, log);
    }

    @Test
    public void passesSignFromCreator() throws Exception {
        Player player = mock(Player.class);
        SignChangeEvent event = new SignChangeEvent(null, player, null);
        ArenaSign sign = new ArenaSign(location(), null, null, null);
        when(creator.create(event)).thenReturn(sign);

        subject.on(event);

        verify(writer).write(sign);
        verify(store).add(sign);
        verify(renderer).render(sign, event);
    }

    @Test
    public void successMessageOnCreation() {
        Player player = mock(Player.class);
        SignChangeEvent event = new SignChangeEvent(null, player, null);
        ArenaSign sign = new ArenaSign(location(), null, "castle", "join");
        when(creator.create(event)).thenReturn(sign);

        subject.on(event);

        verify(messenger).tell(eq(player), anyString());
        verify(log).info(anyString());
    }

    @Test
    public void noWriteIfCreatorThrows() {
        SignChangeEvent event = new SignChangeEvent(null, null, null);
        doThrow(IllegalArgumentException.class).when(creator).create(event);

        subject.on(event);

        verifyNoInteractions(writer, store, renderer, log);
    }

    @Test
    public void errorMessageIfCreatorThrows() {
        Player player = mock(Player.class);
        SignChangeEvent event = new SignChangeEvent(null, player, null);
        String message = "it's bad";
        doThrow(new IllegalArgumentException(message)).when(creator).create(event);

        subject.on(event);

        verify(messenger).tell(player, message);
    }

    @Test
    public void noStorageIfWriterThrows() throws Exception {
        SignChangeEvent event = new SignChangeEvent(null, null, null);
        ArenaSign sign = new ArenaSign(null, null, null, null);
        when(creator.create(event)).thenReturn(sign);
        doThrow(IOException.class).when(writer).write(sign);

        subject.on(event);

        verifyNoInteractions(store, renderer);
    }

    @Test
    public void errorMessageIfWriterThrows() throws Exception {
        Player player = mock(Player.class);
        SignChangeEvent event = new SignChangeEvent(null, player, null);
        ArenaSign sign = new ArenaSign(null, null, null, null);
        when(creator.create(event)).thenReturn(sign);
        IOException exception = new IOException("it's bad");
        doThrow(exception).when(writer).write(sign);

        subject.on(event);

        verify(messenger).tell(eq(player), anyString());
        verify(log).log(eq(Level.SEVERE), anyString(), eq(exception));
    }

    private Location location() {
        World world = mock(World.class);
        when(world.getName()).thenReturn("world");
        Location location = mock(Location.class);
        when(location.getBlockX()).thenReturn(1);
        when(location.getBlockY()).thenReturn(2);
        when(location.getBlockZ()).thenReturn(3);
        when(location.getWorld()).thenReturn(world);
        return location;
    }

}
