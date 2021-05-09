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

@RunWith(Enclosed.class)
public class LexerNumberTest {

    static Environment env;
    static Lexer subject;

    @RunWith(Parameterized.class)
    public static class PositiveNumberLiterals {

        @Before
        public void setup() {
            env = Environment.createDefault();
            subject = new Lexer(env);
        }

        @Parameters(name = "{0}")
        public static Iterable<Object[]> data() {
            return asList(new Object[][]{
                {"0"},
                {"1"},
                {"1337"},
                {"3.14"},
                {"1e4"},
                {"1e-4"},
                {"1.2e4"},
            });
        }

        String input;

        public PositiveNumberLiterals(String input) {
            this.input = input;
        }

        @Test
        public void test() {
            List<Lexeme> result = subject.tokenize(input);

            assertThat(result, contains(matches(NUMBER, input)));
        }

    }

    @RunWith(Parameterized.class)
    public static class NegativeNumberLiterals {

        @Before
        public void setup() {
            env = Environment.createDefault();
            subject = new Lexer(env);
        }

        @Parameters(name = "{0}")
        public static Iterable<Object[]> data() {
            return asList(new Object[][]{
                {"-0"},
                {"-1"},
                {"-1337"},
                {"-3.14"},
                {"-1e4"},
                {"-1e-4"},
                {"-1.2e4"},
            });
        }

        String input;
        String number;

        public NegativeNumberLiterals(String input) {
            this.input = input;
            this.number = input.substring(1);
        }

        @Test
        public void test() {
            List<Lexeme> result = subject.tokenize(input);

            assertThat(result, contains(asList(
                matches(UNARY_OPERATOR, "-"),
                matches(NUMBER, number)
            )));
        }

    }

}
