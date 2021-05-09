package com.garbagemule.MobArena.formula;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.List;

import static com.garbagemule.MobArena.formula.LexemeMatcher.matches;
import static com.garbagemule.MobArena.formula.TokenType.*;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

@RunWith(Enclosed.class)
public class LexerParenthesisTest {

    static Environment env;
    static Lexer subject;

    public static class ParenthesizedExpressions {

        @Before
        public void setup() {
            env = Environment.createDefault();
            subject = new Lexer(env);
        }

        @Test
        public void parenthesizedPositiveNumberLiteral() {
            String input = "(2)";

            List<Lexeme> result = subject.tokenize(input);

            assertThat(result, contains(asList(
                matches(LEFT_PAREN),
                matches(NUMBER, "2"),
                matches(RIGHT_PAREN)
            )));
        }

        @Test
        public void parenthesizedNegativeNumberLiteral() {
            String input = "(-2)";

            List<Lexeme> result = subject.tokenize(input);

            assertThat(result, contains(asList(
                matches(LEFT_PAREN),
                matches(UNARY_OPERATOR, "-"),
                matches(NUMBER, "2"),
                matches(RIGHT_PAREN)
            )));
        }

        @Test
        public void negatedParenthesizedPositiveNumberLiteral() {
            String input = "-(2)";

            List<Lexeme> result = subject.tokenize(input);

            assertThat(result, contains(asList(
                matches(UNARY_OPERATOR, "-"),
                matches(LEFT_PAREN),
                matches(NUMBER, "2"),
                matches(RIGHT_PAREN)
            )));
        }

        @Test
        public void simpleExpression() {
            String input = "(2+3)";

            List<Lexeme> result = subject.tokenize(input);

            assertThat(result, contains(asList(
                matches(LEFT_PAREN),
                matches(NUMBER, "2"),
                matches(BINARY_OPERATOR, "+"),
                matches(NUMBER, "3"),
                matches(RIGHT_PAREN)
            )));
        }

        @Test
        public void nestedExpression() {
            String input = "(((2+3)))";

            List<Lexeme> result = subject.tokenize(input);

            assertThat(result, contains(asList(
                matches(LEFT_PAREN),
                matches(LEFT_PAREN),
                matches(LEFT_PAREN),
                matches(NUMBER, "2"),
                matches(BINARY_OPERATOR, "+"),
                matches(NUMBER, "3"),
                matches(RIGHT_PAREN),
                matches(RIGHT_PAREN),
                matches(RIGHT_PAREN)
            )));
        }

        @Test
        public void multipleExpressions() {
            String input = "(2+3)*-(4-5)^(6/7)";

            List<Lexeme> result = subject.tokenize(input);

            assertThat(result, contains(asList(
                matches(LEFT_PAREN),
                matches(NUMBER, "2"),
                matches(BINARY_OPERATOR, "+"),
                matches(NUMBER, "3"),
                matches(RIGHT_PAREN),
                matches(BINARY_OPERATOR, "*"),
                matches(UNARY_OPERATOR, "-"),
                matches(LEFT_PAREN),
                matches(NUMBER, "4"),
                matches(BINARY_OPERATOR, "-"),
                matches(NUMBER, "5"),
                matches(RIGHT_PAREN),
                matches(BINARY_OPERATOR, "^"),
                matches(LEFT_PAREN),
                matches(NUMBER, "6"),
                matches(BINARY_OPERATOR, "/"),
                matches(NUMBER, "7"),
                matches(RIGHT_PAREN)
            )));
        }

    }

}
