package com.garbagemule.MobArena.signs;

import com.garbagemule.MobArena.MobArena;
import com.garbagemule.MobArena.events.ArenaPlayerJoinEvent;
import com.garbagemule.MobArena.framework.Arena;
import org.bukkit.Server;
import org.bukkit.scheduler.BukkitScheduler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;

import static org.mockito.Mockito.*;

@SuppressWarnings("WeakerAccess")
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class HandlesArenaUpdatesTest {

    SignStore signStore;
    SignRenderer signRenderer;
    BukkitScheduler scheduler;

    HandlesArenaUpdates subject;

    @Before
    public void setup() {
        signStore = mock(SignStore.class);
        signRenderer = mock(SignRenderer.class);
        scheduler = mock(BukkitScheduler.class);

        MobArena plugin = mock(MobArena.class);
        Server server = mock(Server.class);
        when(plugin.getServer()).thenReturn(server);
        when(server.getScheduler()).thenReturn(scheduler);

        subject = new HandlesArenaUpdates(signStore, signRenderer, plugin);
    }

    @Test
    public void usesArenaSlugForLookups() {
        String slug = "angry-dingo";
        Arena arena = mock(Arena.class);
        ArenaSign sign = new ArenaSign(null, "cool-sign", slug, "info");
        when(scheduler.runTask(any(), any(Runnable.class))).thenAnswer(i -> {
            Runnable task = i.getArgument(1);
            task.run();
            return null;
        });
        when(arena.getSlug()).thenReturn(slug);
        when(signStore.findByArenaId(slug)).thenReturn(Collections.singletonList(sign));
        ArenaPlayerJoinEvent event = new ArenaPlayerJoinEvent(null, arena);

        subject.on(event);

        verify(signRenderer).render(sign);
    }

}
