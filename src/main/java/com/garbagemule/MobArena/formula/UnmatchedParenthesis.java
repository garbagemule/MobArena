package com.garbagemule.MobArena.formula;

class UnmatchedParenthesis extends FormulaError {

    private static final String template = "Unmatched %s parenthesis '%s' in column %d";

    UnmatchedParenthesis(Lexeme lexeme, String input) {
        super(message(lexeme), input, lexeme.pos);
    }

    private static String message(Lexeme lexeme) {
        String side = (lexeme.token.type == TokenType.LEFT_PAREN) ? "left" : "right";
        return String.format(template, side, lexeme.value, lexeme.pos + 1);
    }

}
