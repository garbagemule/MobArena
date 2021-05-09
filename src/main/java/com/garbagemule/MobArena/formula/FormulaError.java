package com.garbagemule.MobArena.formula;

import java.util.Arrays;

class FormulaError extends IllegalArgumentException {

    private final String input;
    private final int pos;

    FormulaError(String message, String input, int pos) {
        super(message);
        this.input = input;
        this.pos = pos;
    }

    @Override
    public String getMessage() {
        String arrow = arrow(pos + 1);
        String template = "%s\n%s\n%s";
        return String.format(template, super.getMessage(), input, arrow);
    }

    private String arrow(int length) {
        char[] value = new char[length];
        Arrays.fill(value, ' ');
        value[length - 1] = '^';
        return new String(value);
    }

}
