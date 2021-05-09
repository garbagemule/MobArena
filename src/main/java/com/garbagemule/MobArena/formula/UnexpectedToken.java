package com.garbagemule.MobArena.formula;

class UnexpectedToken extends FormulaError {

    private static final String template = "Unexpected token '%s' in column %d";

    UnexpectedToken(Lexeme lexeme, String input) {
        super(message(lexeme), input, lexeme.pos);
    }

    private static String message(Lexeme lexeme) {
        return String.format(template, lexeme.value, lexeme.pos + 1);
    }

}
