package com.garbagemule.MobArena.formula;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.Objects;

public class LexemeMatcher extends TypeSafeMatcher<Lexeme> {

    private final TokenType type;
    private final String value;

    private LexemeMatcher(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    @Override
    protected boolean matchesSafely(Lexeme item) {
        if (item.token.type != type) {
            return false;
        }
        if (value == null) {
            return true;
        }
        return Objects.equals(item.value, value);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(type.name() + " '" + value + "'");
    }

    public static Matcher<Lexeme> matches(TokenType type, String value) {
        return new LexemeMatcher(type, value);
    }

    public static Matcher<Lexeme> matches(TokenType type) {
        return new LexemeMatcher(type, null);
    }

}
