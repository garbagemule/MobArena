package com.garbagemule.MobArena.formula;

import com.garbagemule.MobArena.MonsterManager;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.WaveManager;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

@RunWith(Enclosed.class)
public class FormulaManagerIT {

    static Arena arena;
    static FormulaManager subject;

    static int finalWave = 13;
    static int currentWave = finalWave - 2;
    static int liveMonsters = 9;
    static int initialPlayers = 7;
    static int livePlayers = initialPlayers - 2;
    static int deadPlayers = initialPlayers - livePlayers;
    static int minPlayers = 3;
    static int maxPlayers = initialPlayers + 3;

    @BeforeClass
    public static void setup() {
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
        when(arena.getMinPlayers()).thenReturn(minPlayers);
        when(arena.getMaxPlayers()).thenReturn(maxPlayers);

        subject = FormulaManager.createDefault();
    }

    @RunWith(Parameterized.class)
    public static class NumberLiterals {

        @Parameters(name = "{0}")
        public static Iterable<Object[]> data() {
            return Arrays.asList(new Object[][]{
                {"0"},
                {"1"},
                {"-1"},
                {"1337"},
                {"3.14"},
                {"1e4"},
                {"-1e4"},
                {"1e-4"},
                {"-1e-4"},
            });
        }

        String input;
        double expected;

        public NumberLiterals(String input) {
            this.input = input;
            this.expected = Double.parseDouble(input);
        }

        @Test
        public void test() {
            Formula formula = subject.parse(input);
            double result = formula.evaluate(arena);

            assertThat(result, equalTo(expected));
        }

    }

    @RunWith(Parameterized.class)
    public static class DefaultConstants {

        @Parameters(name = "{0} = {1}")
        public static Iterable<Object[]> data() {
            return Arrays.asList(new Object[][]{
                {"pi", Math.PI},
                {"e", Math.E},
                {"pi^e", Math.pow(Math.PI, Math.E)},
            });
        }

        String input;
        double expected;

        public DefaultConstants(String input, double expected) {
            this.input = input;
            this.expected = expected;
        }

        @Test
        public void test() {
            Formula formula = subject.parse(input);
            double result = formula.evaluate(arena);

            assertThat(result, equalTo(expected));
        }

    }

    @RunWith(Parameterized.class)
    public static class DefaultVariables {

        @Parameters(name = "{0} = {1}")
        public static Iterable<Object[]> data() {
            return Arrays.asList(new Object[][]{
                {"<current-wave>", currentWave},
                {"<final-wave>", finalWave},
                {"<initial-players>", initialPlayers},
                {"<live-players>", livePlayers},
                {"<dead-players>", deadPlayers},
                {"<min-players>", minPlayers},
                {"<max-players>", maxPlayers},
                {"<live-monsters>", liveMonsters},
            });
        }

        String input;
        double expected;

        public DefaultVariables(String input, double expected) {
            this.input = input;
            this.expected = expected;
        }

        @Test
        public void test() {
            Formula formula = subject.parse(input);
            double result = formula.evaluate(arena);

            assertThat(result, equalTo(expected));
        }

    }

    /**
     * With custom variables, we are manipulating the internal
     * state of the manager, so we need to use a local subject.
     */
    public static class CustomVariables {

        FormulaManager subject;

        @Before
        public void setup() {
            subject = FormulaManager.createDefault();
        }

        @Test
        public void resolveRegisteredCustomVariable() {
            subject.registerVariable("bob", a -> 7.5);

            Formula formula = subject.parse("2.5 + <bob>");
            double result = formula.evaluate(arena);

            double expected = 10;
            assertThat(result, equalTo(expected));
        }

        @Test
        public void throwsOnUnknownCustomVariable() {
            assertThrows(
                UnknownToken.class,
                () -> subject.parse("2 + <bob>")
            );
        }

    }

    @RunWith(Parameterized.class)
    public static class DefaultUnaryOperators {

        @Parameters(name = "{0} = {1}")
        public static Iterable<Object[]> data() {
            return Arrays.asList(new Object[][]{
                {"1 + +1.2", 1 + +1.2},
                {"1 + -1.2", 1 + -1.2},
            });
        }

        String input;
        double expected;

        public DefaultUnaryOperators(String input, double expected) {
            this.input = input;
            this.expected = expected;
        }

        @Test
        public void test() {
            Formula formula = subject.parse(input);
            double result = formula.evaluate(arena);

            assertThat(result, equalTo(expected));
        }

    }

    @RunWith(Parameterized.class)
    public static class DefaultOperators {

        @Parameters(name = "{0} = {1}")
        public static Iterable<Object[]> data() {
            return Arrays.asList(new Object[][]{
                {"1+-2", 1 + -2},
                {"3-+4", 3 - +4},
                {"3*7.5", 3 * 7.5},
                {"10/2.5", 10 / 2.5},
                {"9%4", 9 % 4},
                {"2^-8", Math.pow(2, -8)},
                {"-2^-8", -Math.pow(2, -8)},
                {"(-2)^-8", Math.pow(-2, -8)},
            });
        }

        String input;
        double expected;

        public DefaultOperators(String input, double expected) {
            this.input = input;
            this.expected = expected;
        }

        @Test
        public void test() {
            Formula formula = subject.parse(input);
            double result = formula.evaluate(arena);

            assertThat(result, equalTo(expected));
        }

    }

    @RunWith(Parameterized.class)
    public static class DefaultUnaryFunctions {

        @Parameters(name = "{0} = {1}")
        public static Iterable<Object[]> data() {
            return Arrays.asList(new Object[][]{
                {"sqrt(4)", 2},
                {"sqrt(9)", 3},
                {"abs(2)", 2},
                {"abs(-2)", 2},
                {"ceil(8.2)", 9},
                {"ceil(8.7)", 9},
                {"floor(8.2)", 8},
                {"floor(8.7)", 8},
                {"round(8.2)", 8},
                {"round(8.7)", 9},
                {"sin(pi / 2)", Math.sin(Math.PI / 2)},
                {"cos(pi / 3)", Math.cos(Math.PI / 3)},
                {"tan(pi / 4)", Math.tan(Math.PI / 4)},
            });
        }

        String input;
        double expected;

        public DefaultUnaryFunctions(String input, double expected) {
            this.input = input;
            this.expected = expected;
        }

        @Test
        public void test() {
            Formula formula = subject.parse(input);
            double result = formula.evaluate(arena);

            assertThat(result, equalTo(expected));
        }

    }

    @RunWith(Parameterized.class)
    public static class DefaultBinaryFunctions {

        @Parameters(name = "{0} = {1}")
        public static Iterable<Object[]> data() {
            return Arrays.asList(new Object[][]{
                {"min(1, 2)", 1},
                {"min(2, 1)", 1},
                {"max(1, 2)", 2},
                {"max(2, 1)", 2},
            });
        }

        String input;
        double expected;

        public DefaultBinaryFunctions(String input, double expected) {
            this.input = input;
            this.expected = expected;
        }

        @Test
        public void test() {
            Formula formula = subject.parse(input);
            double result = formula.evaluate(arena);

            assertThat(result, equalTo(expected));
        }

    }

    /**
     * With custom functions, we are manipulating the internal
     * state of the manager, so we need to use a local subject.
     */
    public static class CustomFunctions {

        FormulaManager subject;

        @Before
        public void setup() {
            subject = FormulaManager.createDefault();
        }

        @Test
        public void resolveRegisteredCustomFunctions() {
            subject.registerUnaryFunction("flip", a -> -a);
            subject.registerBinaryFunction("car", (a, b) -> a);
            subject.registerBinaryFunction("cdr", (a, b) -> b);

            Formula formula = subject.parse("flip(car(1, 2) + cdr(3, 4))");
            double result = formula.evaluate(arena);

            double expected = -(1 + 4);
            assertThat(result, equalTo(expected));
        }

        @Test
        public void throwsOnUnknownCustomFunction() {
            assertThrows(
                UnknownToken.class,
                () -> subject.parse("flip(1)")
            );
        }

    }

}
