package com.garbagemule.MobArena.signs;

import static org.mockito.Mockito.*;

import com.garbagemule.MobArena.Messenger;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.framework.ArenaMaster;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@SuppressWarnings("WeakerAccess")
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class InvokesSignActionTest {

    ArenaMaster arenaMaster;
    Messenger messenger;

    InvokesSignAction subject;

    @Before
    public void setup() {
        arenaMaster = mock(ArenaMaster.class);
        messenger = mock(Messenger.class);

        subject = new InvokesSignAction(arenaMaster, messenger);
    }

    @Test
    public void infoSignDoesNothing() {
        String arenaId = "castle";
        ArenaSign sign = new ArenaSign(null, "", arenaId, "info");
        Player player = mock(Player.class);

        subject.invoke(sign, player);

        verifyZeroInteractions(arenaMaster);
    }

    @Test
    public void joinSignCallsCanJoin() {
        String arenaId = "castle";
        ArenaSign sign = new ArenaSign(null, "", arenaId, "join");
        Player player = mock(Player.class);
        Arena arena = mock(Arena.class);
        when(arenaMaster.getArenaWithName(arenaId))
            .thenReturn(arena);

        subject.invoke(sign, player);

        verify(arena).canJoin(player);
    }

    @Test
    public void joinSignCallsPlayerJoin() {
        String arenaId = "castle";
        ArenaSign sign = new ArenaSign(null, "", arenaId, "join");
        Player player = mock(Player.class);
        Arena arena = mock(Arena.class);
        when(arena.canJoin(player)).thenReturn(true);
        when(arenaMaster.getArenaWithName(arenaId))
            .thenReturn(arena);

        subject.invoke(sign, player);

        verify(arena).playerJoin(eq(player), any());
    }

    @Test
    public void leaveSignCallsInChecks() {
        String arenaId = "castle";
        ArenaSign sign = new ArenaSign(null, "", arenaId, "leave");
        Player player = mock(Player.class);
        Arena arena = mock(Arena.class);
        when(arenaMaster.getArenaWithName(arenaId))
            .thenReturn(arena);

        subject.invoke(sign, player);

        verify(arena).inArena(player);
        verify(arena).inLobby(player);
        verify(arena).inSpec(player);
    }

    @Test
    public void leaveSignCallsPlayerLeave() {
        String arenaId = "castle";
        ArenaSign sign = new ArenaSign(null, "", arenaId, "leave");
        Player player = mock(Player.class);
        Arena arena = mock(Arena.class);
        when(arena.inArena(player)).thenReturn(true);
        when(arenaMaster.getArenaWithName(arenaId))
            .thenReturn(arena);

        subject.invoke(sign, player);

        verify(arena).playerLeave(player);
    }

    @Test
    public void nonExistentArenaReportsToPlayer() {
        String arenaId = "castle";
        ArenaSign sign = new ArenaSign(null, "", arenaId, "join");
        Player player = mock(Player.class);
        when(arenaMaster.getArenaWithName(arenaId))
            .thenReturn(null);

        subject.invoke(sign, player);

        verify(messenger).tell(eq(player), anyString());
    }

}
