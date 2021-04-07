package com.garbagemule.MobArena.formula;

class ArgumentMismatch extends FormulaError {

    private static final String few = "Not enough arguments for %s(%s), expected %d, got %d";
    private static final String many = "Too many arguments for %s(%s), expected %d, got %d";

    ArgumentMismatch(Lexeme lexeme, int expected, int counted, String input) {
        super(message(lexeme, expected, counted), input, lexeme.pos);
    }

    private static String message(Lexeme lexeme, int expected, int counted) {
        String name = lexeme.value;
        String args = args(expected);
        String template = (counted < expected) ? few : many;
        return String.format(template, name, args, expected, counted);
    }

    private static String args(int count) {
        if (count == 0) {
            return "";
        }

        char current = 'a';
        StringBuilder result = new StringBuilder();
        result.append(current);

        for (int i = 1; i < count; i++) {
            current++;
            result.append(",").append(current);
        }

        return result.toString();
    }

}
