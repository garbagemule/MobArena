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
public class LexerVariableTest {

    static Environment env;
    static Lexer subject;

    public static class VariableExpressions {

        @Before
        public void setup() {
            env = Environment.createDefault();
            subject = new Lexer(env);
        }

        @Test
        public void simpleVariableExpression() {
            env.registerVariable("a", null);
            String input = "<a>";

            List<Lexeme> result = subject.tokenize(input);

            assertThat(result, contains(matches(VARIABLE, "a")));
        }

        @Test
        public void multiVariableExpression() {
            env.registerVariable("a", null);
            env.registerVariable("b", null);
            String input = "<a>+<b>";

            List<Lexeme> result = subject.tokenize(input);

            assertThat(result, contains(asList(
                matches(VARIABLE, "a"),
                matches(BINARY_OPERATOR, "+"),
                matches(VARIABLE, "b")
            )));
        }

        @Test
        public void unknownVariable() {
            assertThrows(
                FormulaError.class,
                () -> subject.tokenize("<a>")
            );
        }

    }

    @RunWith(Parameterized.class)
    public static class InvalidVariables {

        @Before
        public void setup() {
            env = Environment.createDefault();
            subject = new Lexer(env);
        }

        @Parameters(name = "{0}")
        public static Iterable<Object[]> data() {
            return Arrays.asList(new Object[][]{
                {"<"},
                {">"},
                {"<a"},
                {"a>"},
                {"<<a>"},
                {"<a>>"},
                {"<a >"},
                {"< a>"},
            });
        }

        String input;

        public InvalidVariables(String input) {
            this.input = input;
        }

        @Test
        public void test() {
            env.registerVariable("a", null);
            assertThrows(
                LexerError.class,
                () -> subject.tokenize(input)
            );
        }

    }

}
