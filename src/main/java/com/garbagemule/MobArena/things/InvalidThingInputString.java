package com.garbagemule.MobArena.things;

public class InvalidThingInputString extends RuntimeException {
    private String input;

    InvalidThingInputString(String input) {
        super("Invalid input: " + input);
        this.input = input;
    }

    public String getInput() {
        return input;
    }
}
