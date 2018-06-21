package com.garbagemule.MobArena.signs;

import static org.mockito.Mockito.*;

import com.garbagemule.MobArena.Messenger;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

@SuppressWarnings("WeakerAccess")
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class HandlesSignDestructionTest {

    RemovesSignAtLocation removesSignAtLocation;
    Messenger messenger;

    HandlesSignDestruction subject;

    @Before
    public void setup() {
        removesSignAtLocation = mock(RemovesSignAtLocation.class);
        messenger = mock(Messenger.class);

        subject = new HandlesSignDestruction(
            removesSignAtLocation,
            messenger
        );
    }

    @Test
    public void doesNothingWithNonArenaSign() {
        Block block = mock(Block.class);
        Player player = mock(Player.class);
        when(removesSignAtLocation.remove(any()))
            .thenReturn(Optional.empty());
        BlockBreakEvent event = new BlockBreakEvent(block, player);

        subject.on(event);

        verifyZeroInteractions(messenger);
    }

    @Test
    public void reportsBreakageWithArenaSign() {
        Block block = mock(Block.class);
        Player player = mock(Player.class);
        ArenaSign sign = new ArenaSign(null, "", "", "");
        when(removesSignAtLocation.remove(any()))
            .thenReturn(Optional.of(sign));
        BlockBreakEvent event = new BlockBreakEvent(block, player);

        subject.on(event);

        verify(messenger).tell(eq(player), anyString());
    }

}
