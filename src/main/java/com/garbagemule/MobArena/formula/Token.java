package com.garbagemule.MobArena.formula;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Token {

    static final Token LEFT_PAREN = new Token(TokenType.LEFT_PAREN, "\\(", "(");
    static final Token RIGHT_PAREN = new Token(TokenType.RIGHT_PAREN, "\\)", ")");
    static final Token COMMA = new Token(TokenType.COMMA, "\\,", ",");

    static final Token NUMBER = new Token(
        TokenType.NUMBER,
        "([0-9]+[.])?[0-9]+([eE][-+]?[0-9]+)?"
    );

    static final Token IDENTIFIER = new Token(
        TokenType.IDENTIFIER,
        "\\p{L}([0-9]|\\p{L})*"
    );

    static final Token VARIABLE = new Token(
        TokenType.VARIABLE,
        "[<]\\p{L}(([0-9_-]|\\p{L})*([0-9]|\\p{L})+)?"
    );

    final TokenType type;
    final Pattern pattern;
    final String symbol;

    Token(TokenType type, String regex, String symbol) {
        this.type = type;
        this.pattern = Pattern.compile("^" + regex);
        this.symbol = symbol;
    }

    Token(TokenType type, String regex) {
        this(type, regex, null);
    }

    int match(CharSequence input) {
        Matcher m = pattern.matcher(input);
        if (m.find()) {
            return m.end();
        }
        return -1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Token token = (Token) o;
        return type == token.type
            && Objects.equals(symbol, token.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, symbol);
    }

    @Override
    public String toString() {
        if (symbol == null) {
            return type.toString();
        } else {
            return type.toString() + " '" + symbol + "'";
        }
    }

}
