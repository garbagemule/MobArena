package com.garbagemule.MobArena.signs;

import com.garbagemule.MobArena.Messenger;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
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
public class HandlesSignDestructionTest {

    SignStore store;
    SignWriter writer;
    Messenger messenger;
    Logger log;

    HandlesSignDestruction subject;

    @Before
    public void setup() {
        store = mock(SignStore.class);
        writer = mock(SignWriter.class);
        messenger = mock(Messenger.class);
        log = mock(Logger.class);

        subject = new HandlesSignDestruction(
            store,
            writer,
            messenger,
            log
        );
    }

    @Test
    public void noSignNoAction() {
        Location location = mock(Location.class);
        Block block = mock(Block.class);
        BlockBreakEvent event = new BlockBreakEvent(block, null);
        when(block.getLocation()).thenReturn(location);
        when(store.removeByLocation(location)).thenReturn(null);

        subject.on(event);

        verifyNoInteractions(writer, messenger, log);
    }

    @Test
    public void erasesFoundSign() throws IOException {
        Location location = location();
        Block block = mock(Block.class);
        Player player = mock(Player.class);
        ArenaSign sign = new ArenaSign(location, "cool-sign", "castle", "join");
        when(block.getLocation()).thenReturn(location);
        when(store.removeByLocation(location)).thenReturn(sign);
        BlockBreakEvent event = new BlockBreakEvent(block, player);

        subject.on(event);

        verify(writer).erase(sign);
    }

    @Test
    public void successMessageOnDestruction() {
        Location location = location();
        Block block = mock(Block.class);
        Player player = mock(Player.class);
        ArenaSign sign = new ArenaSign(location, "cool-sign", "castle", "join");
        when(block.getLocation()).thenReturn(location);
        when(store.removeByLocation(location)).thenReturn(sign);
        BlockBreakEvent event = new BlockBreakEvent(block, player);

        subject.on(event);

        verify(messenger).tell(eq(player), anyString());
        verify(log).info(anyString());
    }

    @Test
    public void errorMessageIfWriterThrows() throws Exception {
        Location location = mock(Location.class);
        Block block = mock(Block.class);
        Player player = mock(Player.class);
        ArenaSign sign = new ArenaSign(location, "cool-sign", "castle", "join");
        IOException exception = new IOException("it's bad");
        when(block.getLocation()).thenReturn(location);
        when(store.removeByLocation(location)).thenReturn(sign);
        doThrow(exception).when(writer).erase(sign);
        BlockBreakEvent event = new BlockBreakEvent(block, player);

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
