package com.garbagemule.MobArena.formula;

import com.garbagemule.MobArena.MobArena;
import com.garbagemule.MobArena.MonsterManager;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.WaveManager;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.mockito.Mockito.*;

@RunWith(Enclosed.class)
public class FormulaMacrosIT {

    static MobArena plugin;
    static Arena arena;
    static FormulaMacros macros;
    static FormulaManager parser;

    static int finalWave = 13;
    static int currentWave = finalWave - 2;
    static int liveMonsters = 9;
    static int initialPlayers = 7;
    static int livePlayers = initialPlayers - 2;

    @BeforeClass
    public static void setup() throws IOException {
        plugin = mock(MobArena.class);
        File resources = new File("src/main/resources");
        when(plugin.getDataFolder()).thenReturn(resources);

        arena = mock(Arena.class);
        WaveManager wm = mock(WaveManager.class);
        when(wm.getWaveNumber()).thenReturn(currentWave);
        when(wm.getFinalWave()).thenReturn(finalWave);
        when(arena.getWaveManager()).thenReturn(wm);
        Set<LivingEntity> monsters = new HashSet<>();
        for (int i = 0; i < liveMonsters; i++) {
            monsters.add(mock(LivingEntity.class));
        }
        MonsterManager mm = mock(MonsterManager.class);
        when(mm.getMonsters()).thenReturn(monsters);
        when(arena.getMonsterManager()).thenReturn(mm);
        Set<Player> players = new HashSet<>();
        for (int i = 0; i < livePlayers; i++) {
            players.add(mock(Player.class));
        }
        when(arena.getPlayersInArena()).thenReturn(players);
        when(arena.getPlayerCount()).thenReturn(initialPlayers);

        macros = FormulaMacros.create(plugin);
        macros.reload();

        parser = FormulaManager.createDefault();
    }

    @RunWith(Parameterized.class)
    public static class Global {

        @Parameters(name = "{0}")
        public static Iterable<Object[]> data() {
            return Arrays.asList(new Object[][]{
                {"wave-squared", currentWave * currentWave},
                {"wave-inverted", finalWave - currentWave},
                {"five-each", livePlayers * 5},
                {"double-team", (double) livePlayers / 2},
                {"top-up", 10 - liveMonsters},
                {"dead-man-walking", initialPlayers - livePlayers},
            });
        }

        String macro;
        double expected;

        public Global(String macro, double expected) {
            this.macro = macro;
            this.expected = expected;
        }

        @Test
        public void test() {
            String value = macros.get("global", macro);
            Formula formula = parser.parse(value);

            double result = formula.evaluate(arena);

            assertThat(result, equalTo(expected));
        }

    }

    @RunWith(Parameterized.class)
    public static class WaveGrowth {

        @Parameters(name = "{0}")
        public static Iterable<Object[]> data() {
            return Arrays.asList(new Object[][]{
                {"slow", 0.5},
                {"medium", 0.65},
                {"fast", 0.8},
                {"psycho", 1.2},
            });
        }

        String macro;
        double exponent;

        public WaveGrowth(String macro, double exponent) {
            this.macro = macro;
            this.exponent = exponent;
        }

        @Test
        public void test() {
            String value = macros.get("wave-growth", macro);
            Formula formula = parser.parse(value);

            double result = (int) formula.evaluate(arena);

            double base = (int) Math.ceil(initialPlayers / 2.0) + 1;
            double expected = (int) (base * Math.pow(currentWave, exponent));
            assertThat(result, equalTo(expected));
        }

    }

    /**
     * Old wave growth formula is different, so we'll just have a
     * different test for it all-together.
     */
    public static class WaveGrowthOld {

        @Test
        public void oldWaveGrowth() {
            String value = macros.get("wave-growth", "old");
            Formula formula = parser.parse(value);

            double result = formula.evaluate(arena);

            double expected = currentWave + initialPlayers;
            assertThat(result, equalTo(expected));
        }

    }

    @RunWith(Parameterized.class)
    public static class SwarmAmount {

        @Parameters(name = "{0}")
        public static Iterable<Object[]> data() {
            return Arrays.asList(new Object[][]{
                {"low", 10},
                {"medium", 20},
                {"high", 30},
                {"psycho", 60},
            });
        }

        String macro;
        double multiplier;

        public SwarmAmount(String macro, double multiplier) {
            this.macro = macro;
            this.multiplier = multiplier;
        }

        @Test
        public void test() {
            String value = macros.get("swarm-amount", macro);
            Formula formula = parser.parse(value);

            double result = formula.evaluate(arena);

            double expected = (double) (initialPlayers / 2) * multiplier;
            assertThat(result, equalTo(expected));
        }

    }

    @RunWith(Parameterized.class)
    public static class BossHealth {

        @Parameters(name = "{0}")
        public static Iterable<Object[]> data() {
            return Arrays.asList(new Object[][]{
                {"verylow", 4},
                {"low", 8},
                {"medium", 15},
                {"high", 25},
                {"veryhigh", 40},
                {"psycho", 60},
            });
        }

        String macro;
        double multiplier;

        public BossHealth(String macro, double multiplier) {
            this.macro = macro;
            this.multiplier = multiplier;
        }

        @Test
        public void test() {
            String value = macros.get("boss-health", macro);
            Formula formula = parser.parse(value);

            double result = formula.evaluate(arena);

            double expected = (initialPlayers + 1) * 20 * multiplier;
            assertThat(result, equalTo(expected));
        }

    }

    /**
     * Try actually loading a non-default formulas.yml with a couple
     * of different types of formulas in it to test that the loading
     * itself actually works.
     */
    public static class TestFile {

        @Test
        public void loadsUnorthodoxFile() throws IOException {
            plugin = mock(MobArena.class);
            File resources = new File("src/test/resources");
            when(plugin.getDataFolder()).thenReturn(resources);

            FormulaMacros subject = FormulaMacros.create(plugin);
            subject.reload();

            assertThat(subject.get("numbers", "one"), equalTo("1"));
            assertThat(subject.get("numbers", "two"), equalTo("2"));
            assertThat(subject.get("constants", "three-point-one-four"), equalTo("pi"));
            assertThat(subject.get("constants", "eulers-number"), equalTo("e"));
            assertThat(subject.get("variables", "live"), equalTo("<live-players>"));
            assertThat(subject.get("variables", "max"), equalTo("<max-players>"));
            assertThat(subject.get("operators", "two-plus-two"), equalTo("2 + 2"));
            assertThat(subject.get("operators", "one-times-two"), equalTo("1 * 2"));
            assertThat(subject.get("functions", "square-root"), equalTo("sqrt(9)"));
            assertThat(subject.get("functions", "maximum"), equalTo("max(1, 2)"));
        }

    }

}
