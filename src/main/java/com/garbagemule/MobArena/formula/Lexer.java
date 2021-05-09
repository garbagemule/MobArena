package com.garbagemule.MobArena.formula;

import java.util.ArrayList;
import java.util.List;

class Lexer {

    private final Environment env;

    private List<Lexeme> result;
    private String input;
    private int pos;

    Lexer(Environment env) {
        this.env = env;
    }

    List<Lexeme> tokenize(String input) {
        this.result = new ArrayList<>();
        this.input = input;
        this.pos = 0;

        tokenize();

        List<Lexeme> result = this.result;
        this.result = null;
        this.input = null;
        this.pos = -1;

        return result;
    }

    private void tokenize() {
        while (pos < input.length()) {
            skipWhitespace();
            nextToken();
        }
    }

    private void skipWhitespace() {
        for (int i = pos; i < input.length(); i++) {
            char current = input.charAt(i);
            if (Character.isWhitespace(current)) {
                continue;
            }
            pos = i;
            return;
        }
    }

    private void nextToken() {
        if (nextNumber()) {
            return;
        }
        if (nextIdentifier()) {
            return;
        }
        if (nextVariable()) {
            return;
        }
        if (nextOperator()) {
            return;
        }
        if (nextSymbol()) {
            return;
        }
        String message = String.format("Unexpected token in column %d", pos + 1);
        throw new LexerError(message, input, pos);
    }

    private boolean nextNumber() {
        char first = input.charAt(pos);
        if (!Character.isDigit(first)) {
            return false;
        }

        String chunk = input.substring(pos);
        int end = Token.NUMBER.match(chunk);
        if (end < 0) {
            String message = String.format("Invalid number in column %d", pos + 1);
            throw new LexerError(message, input, pos);
        }

        String lexeme = input.substring(pos, pos + end);
        result.add(new Lexeme(Token.NUMBER, lexeme, pos));
        pos += end;
        return true;
    }

    private boolean nextIdentifier() {
        char first = input.charAt(pos);
        if (!Character.isLetter(first)) {
            return false;
        }

        String chunk = input.substring(pos);
        int end = Token.IDENTIFIER.match(chunk);
        if (end < 0) {
            String message = String.format("Invalid identifier in column %d", pos + 1);
            throw new LexerError(message, input, pos);
        }

        if (pos + end < input.length()) {
            if (input.charAt(pos + end) == '>') {
                String message = String.format("Unmatched right bracket in column %d", pos + 1);
                throw new LexerError(message, input, pos);
            }
        }

        String identifier = input.substring(pos, pos + end);
        if (!env.isConstant(identifier) && !env.isFunction(identifier)) {
            throw new UnknownToken("identifier", identifier, input, pos);
        }

        result.add(new Lexeme(Token.IDENTIFIER, identifier, pos));
        pos += end;
        return true;
    }

    private boolean nextVariable() {
        char first = input.charAt(pos);
        if (first == '>') {
            String message = String.format("Unmatched right bracket in column %d", pos + 1);
            throw new LexerError(message, input, pos);
        }
        if (first != '<') {
            return false;
        }

        String chunk = input.substring(pos);
        int end = Token.VARIABLE.match(chunk);
        if (end < 0) {
            String message = String.format("Invalid variable in column %d", pos + 1);
            throw new LexerError(message, input, pos);
        }
        if (end >= chunk.length() || chunk.charAt(end) != '>') {
            String message = String.format("Unmatched left bracket in column %d", pos + 1);
            throw new LexerError(message, input, pos);
        }

        String identifier = input.substring(pos + 1, pos + end);
        if (!env.isVariable(identifier)) {
            String varible = "<" + identifier + ">";
            throw new UnknownToken("variable", varible, input, pos);
        }

        result.add(new Lexeme(Token.VARIABLE, identifier, pos));
        pos += end + 1;
        return true;
    }

    private boolean nextOperator() {
        if (isUnaryTokenExpected()) {
            return nextUnaryOperator();
        } else {
            return nextBinaryOperator();
        }
    }

    private boolean nextUnaryOperator() {
        return nextToken(env.unary);
    }

    private boolean nextBinaryOperator() {
        return nextToken(env.binary);
    }

    private boolean nextSymbol() {
        return nextToken(env.symbols);
    }

    private boolean nextToken(List<Token> tokens) {
        String chunk = input.substring(pos);
        for (Token token : tokens) {
            int end = token.match(chunk);
            if (end < 0) {
                continue;
            }

            String symbol = input.substring(pos, pos + end);
            result.add(new Lexeme(token, symbol, pos));
            pos += end;
            return true;
        }
        return false;
    }

    private boolean isUnaryTokenExpected() {
        if (result.isEmpty()) {
            return true;
        }

        Lexeme previous = result.get(result.size() - 1);

        switch (previous.token.type) {
            case LEFT_PAREN:
            case UNARY_OPERATOR:
            case BINARY_OPERATOR:
            case COMMA: {
                return true;
            }
            case NUMBER:
            case IDENTIFIER:
            case VARIABLE:
            case RIGHT_PAREN: {
                return false;
            }
        }

        throw new UnknownToken("symbol", previous, input);
    }

}
