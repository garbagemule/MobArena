package com.garbagemule.MobArena.formula;

class UnknownToken extends FormulaError {

    private static final String template = "Unknown %s '%s' in column %d";

    UnknownToken(String type, Lexeme lexeme, String input) {
        super(message(type, lexeme), input, lexeme.pos);
    }

    UnknownToken(String type, String value, String input, int pos) {
        super(message(type, value, pos), input, pos);
    }

    private static String message(String type, Lexeme lexeme) {
        return message(type, lexeme.value, lexeme.pos);
    }

    private static String message(String type, String identifier, int pos) {
        return String.format(template, type, identifier, pos + 1);
    }

}
