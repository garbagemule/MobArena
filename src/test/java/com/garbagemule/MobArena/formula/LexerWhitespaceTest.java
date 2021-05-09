package com.garbagemule.MobArena.formula;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.garbagemule.MobArena.formula.LexemeMatcher.matches;
import static com.garbagemule.MobArena.formula.TokenType.*;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

public class LexerWhitespaceTest {

    Environment env;
    Lexer subject;

    @Before
    public void setup() {
        env = Environment.createDefault();
        subject = new Lexer(env);
    }

    @Test
    public void ignoresWhitespace() {
        String input = "  1+ 5 -  2\t ^  \n8";

        List<Lexeme> result = subject.tokenize(input);

        assertThat(result, contains(asList(
            matches(NUMBER, "1"),
            matches(BINARY_OPERATOR, "+"),
            matches(NUMBER, "5"),
            matches(BINARY_OPERATOR, "-"),
            matches(NUMBER, "2"),
            matches(BINARY_OPERATOR, "^"),
            matches(NUMBER, "8")
        )));
    }

}
