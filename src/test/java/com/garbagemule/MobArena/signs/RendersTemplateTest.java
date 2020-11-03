package com.garbagemule.MobArena.signs;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.WaveManager;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;

@SuppressWarnings("WeakerAccess")
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class RendersTemplateTest {

    RendersTemplate subject;

    @Before
    public void setup() {
        subject = new RendersTemplate();
    }

    @Test
    public void rendersArenaName() {
        String name = "castle";
        Arena arena = arena(name, false, false);
        Template template = new Template.Builder("template")
            .withBase(new String[]{"<arena-name>", "", "", ""})
            .build();

        String[] result = subject.render(template, arena);

        String[] expected = new String[]{name, "", "", ""};
        assertThat(result, equalTo(expected));
    }

    @Test
    public void defaultsToBaseIfArenaIsNotRunning() {
        Arena arena = arena("castle", false, false);
        String[] base = {"this", "is", "the", "base"};
        Template template = new Template.Builder("template")
            .withBase(base)
            .withRunning(new String[]{"here", "is", "running", "yo"})
            .build();

        String[] result = subject.render(template, arena);

        assertThat(result, equalTo(base));
    }

    @Test
    public void idleOverridesBaseIfNotRunning() {
        Arena arena = arena("castle", false, false);
        String[] idle = {"relax", "don't", "do", "it"};
        Template template = new Template.Builder("template")
            .withBase(new String[]{"this", "is", "the", "base"})
            .withIdle(idle)
            .build();

        String[] result = subject.render(template, arena);

        assertThat(result, equalTo(idle));
    }

    @Test
    public void runningOverridesBaseIfArenaIsRunning() {
        Arena arena = arena("castle", true, false);
        String[] running = {"here", "is", "running", "yo"};
        Template template = new Template.Builder("template")
            .withBase(new String[]{"this", "is", "the", "base"})
            .withRunning(running)
            .build();

        String[] result = subject.render(template, arena);

        assertThat(result, equalTo(running));
    }

    @Test
    public void lobbyOverridesBaseIfPlayersInLobby() {
        Arena arena = arena("castle", false, true);
        String[] joining = {"we", "in", "da", "lobby"};
        Template template = new Template.Builder("template")
            .withBase(new String[]{"this", "is", "the", "base"})
            .withJoining(joining)
            .build();

        String[] result = subject.render(template, arena);

        assertThat(result, equalTo(joining));
    }

    @Test
    public void readyOverridesLobbyIfPlayersReady() {
        Player ready = mock(Player.class);
        Arena arena = mock(Arena.class);
        when(arena.configName()).thenReturn("castle");
        when(arena.isRunning()).thenReturn(false);
        when(arena.getPlayersInLobby()).thenReturn(Collections.singleton(ready));
        when(arena.getNonreadyPlayers()).thenReturn(Collections.emptyList());
        when(arena.getReadyPlayersInLobby()).thenReturn(Collections.singleton(ready));
        String[] readyTemplate = {"we", "are", "all", "ready"};
        Template template = new Template.Builder("template")
            .withBase(new String[]{"this", "is", "the", "base"})
            .withJoining(new String[]{"joining", "template", "is", "here"})
            .withReady(readyTemplate)
            .build();

        String[] result = subject.render(template, arena);

        assertThat(result, equalTo(readyTemplate));
    }

    @Test
    public void readyPlayersReturnsJoiningIfNotDefined() {
        Player ready = mock(Player.class);
        Arena arena = mock(Arena.class);
        when(arena.configName()).thenReturn("castle");
        when(arena.isRunning()).thenReturn(false);
        when(arena.getPlayersInLobby()).thenReturn(Collections.singleton(ready));
        when(arena.getNonreadyPlayers()).thenReturn(Collections.emptyList());
        when(arena.getReadyPlayersInLobby()).thenReturn(Collections.singleton(ready));
        String[] joining = {"we", "in", "da", "lobby"};
        Template template = new Template.Builder("template")
            .withBase(new String[]{"this", "is", "the", "base"})
            .withJoining(joining)
            .build();

        String[] result = subject.render(template, arena);

        assertThat(result, equalTo(joining));
    }

    @Test
    public void rendersReadyListEntries() {
        Player ready = mock(Player.class);
        when(ready.getName()).thenReturn("Bobcat00");
        Player notready = mock(Player.class);
        when(notready.getName()).thenReturn("garbagemule");
        Arena arena = mock(Arena.class);
        when(arena.configName()).thenReturn("castle");
        when(arena.isRunning()).thenReturn(false);
        when(arena.getNonreadyPlayers()).thenReturn(Collections.singletonList(notready));
        when(arena.getReadyPlayersInLobby()).thenReturn(Collections.singleton(ready));
        Template template = new Template.Builder("template")
            .withBase(new String[]{"<ready-1>", "<ready-2>", "<notready-1>", "<notready-2>"})
            .build();

        String[] result = subject.render(template, arena);

        String[] expected = {"Bobcat00", "", "garbagemule", ""};
        assertThat(result, equalTo(expected));
    }

    @Test
    public void doesNotRenderInvalidListEntries() {
        Arena arena = arena("castle", false, true);
        String[] base = new String[]{"<ready-0>", "<ready--1>", "<ready-n>", "<ready-999999>"};
        Template template = new Template.Builder("template")
            .withBase(base)
            .build();

        String[] result = subject.render(template, arena);

        assertThat(result, equalTo(base));
    }

    private Arena arena(String name, boolean running, boolean lobby) {
        Arena arena = mock(Arena.class);
        when(arena.configName()).thenReturn(name);
        when(arena.isRunning()).thenReturn(running);
        when(arena.getPlayersInLobby()).thenReturn(lobby ? Collections.singleton(null) : Collections.emptySet());
        when(arena.getWaveManager()).thenReturn(mock(WaveManager.class));
        return arena;
    }

}
