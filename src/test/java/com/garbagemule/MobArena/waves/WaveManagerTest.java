package com.garbagemule.MobArena.waves;

import com.garbagemule.MobArena.MobArena;
import com.garbagemule.MobArena.framework.Arena;
import org.bukkit.configuration.ConfigurationSection;
import org.junit.Before;
import org.junit.Test;

import java.util.logging.Logger;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WaveManagerTest {

    private Arena arena;
    private ConfigurationSection waves;
    private ConfigurationSection arenaNode;

    @Before
    public void setup() {
        MobArena plugin = mock(MobArena.class);
        when(plugin.getLogger()).thenReturn(Logger.getLogger("WaveManagerTest"));

        arena = mock(Arena.class);
        when(arena.getPlugin()).thenReturn(plugin);
        when(arena.configName()).thenReturn("test-arena");

        // The WaveManager receives the arena's "waves" node, and reaches back
        // to the arena-node via getParent() to read settings.
        arenaNode = mock(ConfigurationSection.class);

        waves = mock(ConfigurationSection.class);
        when(waves.getParent()).thenReturn(arenaNode);
        // No wave branches configured => empty wave sets, default wave is used.
        when(waves.getConfigurationSection("recurrent")).thenReturn(null);
        when(waves.getConfigurationSection("single")).thenReturn(null);
    }

    @Test
    public void startsFromWaveZeroByDefault() {
        when(arenaNode.getInt("skip-waves", 0)).thenReturn(0);
        when(arenaNode.getInt("settings.final-wave", 0)).thenReturn(0);

        WaveManager subject = new WaveManager(arena, waves);
        subject.reset();

        assertThat(subject.getWaveNumber(), is(0));
    }

    @Test
    public void skipWavesSetsInitialWaveNumber() {
        when(arenaNode.getInt("skip-waves", 0)).thenReturn(5);
        when(arenaNode.getInt("settings.final-wave", 0)).thenReturn(0);

        WaveManager subject = new WaveManager(arena, waves);
        subject.reset();

        assertThat(subject.getWaveNumber(), is(5));
    }
}
