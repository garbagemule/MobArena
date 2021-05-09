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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThrows;

@RunWith(Enclosed.class)
public class LexerConstantTest {

    static Environment env;
    static Lexer subject;

    @RunWith(Parameterized.class)
    public static class ConstantLiterals {

        @Before
        public void setup() {
            env = Environment.createDefault();
            subject = new Lexer(env);
        }

        @Parameters(name = "{0}")
        public static Iterable<Object[]> data() {
            return Arrays.asList(new Object[][]{
                {"pi"},
                {"e"},
            });
        }

        String input;

        public ConstantLiterals(String input) {
            this.input = input;
        }

        @Test
        public void test() {
            List<Lexeme> result = subject.tokenize(input);

            assertThat(result, contains(matches(IDENTIFIER, input)));
        }

    }

    public static class InvalidConstants {

        @Before
        public void setup() {
            env = Environment.createDefault();
            subject = new Lexer(env);
        }

        @Test
        public void unknownConstant() {
            assertThrows(
                UnknownToken.class,
                () -> subject.tokenize("pie")
            );
        }

    }

}
