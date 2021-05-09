package com.garbagemule.MobArena.formula;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.List;

import static com.garbagemule.MobArena.formula.LexemeMatcher.matches;
import static com.garbagemule.MobArena.formula.TokenType.*;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThrows;

@RunWith(Enclosed.class)
public class LexerOperatorTest {

    static Environment env;
    static Lexer subject;

    @RunWith(Parameterized.class)
    public static class BinaryOperators {

        @Before
        public void setup() {
            env = Environment.createDefault();
            subject = new Lexer(env);
        }

        @Parameters(name = "{0}")
        public static Iterable<Object[]> data() {
            return Arrays.asList(new Object[][]{
                {"+"},
                {"-"},
                {"*"},
                {"/"},
                {"%"},
                {"^"},
            });
        }

        String symbol;
        String input;

        public BinaryOperators(String symbol) {
            this.symbol = symbol;
            this.input = "1" + symbol + "2";
        }

        @Test
        public void test() {
            List<Lexeme> result = subject.tokenize(input);

            assertThat(result, contains(asList(
                matches(NUMBER, "1"),
                matches(BINARY_OPERATOR, symbol),
                matches(NUMBER, "2")
            )));
        }

    }

    @RunWith(Parameterized.class)
    public static class UnaryOperators {

        @Before
        public void setup() {
            env = Environment.createDefault();
            subject = new Lexer(env);
        }

        @Parameters(name = "{0}")
        public static Iterable<Object[]> data() {
            return Arrays.asList(new Object[][]{
                {"+"},
                {"-"},
            });
        }

        String symbol;
        String input;

        public UnaryOperators(String symbol) {
            this.symbol = symbol;
            this.input = symbol + "1";
        }

        @Test
        public void test() {
            List<Lexeme> result = subject.tokenize(input);

            assertThat(result, contains(asList(
                matches(UNARY_OPERATOR, symbol),
                matches(NUMBER, "1")
            )));
        }

    }

    public static class OperatorAmbiguity {

        @Before
        public void setup() {
            env = Environment.createDefault();
            subject = new Lexer(env);
        }

        @Test
        public void longestMatchInfix() {
            env.registerBinaryOperator("--", 2, true, null);

            List<Lexeme> result = subject.tokenize("1---2");

            assertThat(result, contains(asList(
                matches(NUMBER, "1"),
                matches(BINARY_OPERATOR, "--"),
                matches(UNARY_OPERATOR, "-"),
                matches(NUMBER, "2")
            )));
        }

        @Test
        public void longestMatchPrefix() {
            env.registerUnaryOperator("--", 4, null);

            List<Lexeme> result = subject.tokenize("1---2");

            assertThat(result, contains(asList(
                matches(NUMBER, "1"),
                matches(BINARY_OPERATOR, "-"),
                matches(UNARY_OPERATOR, "--"),
                matches(NUMBER, "2")
            )));
        }

        @Test
        public void longestMatchBoth() {
            env.registerUnaryOperator("--", 4, null);
            env.registerBinaryOperator("--", 2, true, null);

            List<Lexeme> result = subject.tokenize("1---2");

            assertThat(result, contains(asList(
                matches(NUMBER, "1"),
                matches(BINARY_OPERATOR, "--"),
                matches(UNARY_OPERATOR, "-"),
                matches(NUMBER, "2")
            )));
        }

    }

    public static class InvalidOperators {

        @Before
        public void setup() {
            env = Environment.createDefault();
            subject = new Lexer(env);
        }

        @Test
        public void invalidOperator() {
            assertThrows(
                LexerError.class,
                () -> subject.tokenize("1@2")
            );
        }

    }

}
