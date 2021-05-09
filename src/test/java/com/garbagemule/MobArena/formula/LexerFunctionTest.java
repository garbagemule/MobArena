package com.garbagemule.MobArena.formula;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.List;

import static com.garbagemule.MobArena.formula.LexemeMatcher.matches;
import static com.garbagemule.MobArena.formula.TokenType.*;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThrows;

@RunWith(Enclosed.class)
public class LexerFunctionTest {

    static Environment env;
    static Lexer subject;

    @RunWith(Parameterized.class)
    public static class UnaryFunctions {

        @Before
        public void setup() {
            env = Environment.createDefault();
            subject = new Lexer(env);
        }

        @Parameters(name = "{0}")
        public static Iterable<Object[]> data() {
            return asList(new Object[][]{
                {"sqrt"},
                {"abs"},
                {"ceil"},
                {"floor"},
                {"round"},
                {"sin"},
                {"cos"},
                {"tan"},
            });
        }

        String name;
        String input;

        public UnaryFunctions(String name) {
            this.name = name;
            this.input = name + "(1.2)";
        }

        @Test
        public void test() {
            List<Lexeme> result = subject.tokenize(input);

            assertThat(result, contains(asList(
                matches(IDENTIFIER, name),
                matches(LEFT_PAREN),
                matches(NUMBER, "1.2"),
                matches(RIGHT_PAREN)
            )));
        }

    }

    @RunWith(Parameterized.class)
    public static class BinaryFunctions {

        @Before
        public void setup() {
            env = Environment.createDefault();
            subject = new Lexer(env);
        }

        @Parameters(name = "{0}")
        public static Iterable<Object[]> data() {
            return asList(new Object[][]{
                {"min"},
                {"max"},
            });
        }

        String name;
        String input;

        public BinaryFunctions(String name) {
            this.name = name;
            this.input = name + "(1, 2)";
        }

        @Test
        public void test() {
            List<Lexeme> result = subject.tokenize(input);

            assertThat(result, contains(asList(
                matches(IDENTIFIER, name),
                matches(LEFT_PAREN),
                matches(NUMBER, "1"),
                matches(COMMA),
                matches(NUMBER, "2"),
                matches(RIGHT_PAREN)
            )));
        }

    }

    public static class InvalidFunctions {

        @Before
        public void setup() {
            env = Environment.createDefault();
            subject = new Lexer(env);
        }

        @Test
        public void unknownFunction() {
            assertThrows(
                UnknownToken.class,
                () -> subject.tokenize("best(1.2)")
            );
        }

    }


}
