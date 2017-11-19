package com.garbagemule.MobArena.things;

class CommandThingParser implements ThingParser {
    private static final String PREFIX_LONG = "command";
    private static final String PREFIX_SHORT = "cmd";

    @Override
    public CommandThing parse(String s) {
        String trimmed = trimPrefix(s);
        if (trimmed == null) {
            return null;
        }
        if (trimmed.startsWith(":")) {
            return untitledCommand(trimmed);
        }
        if (trimmed.startsWith("(")) {
            return titledCommand(s, trimmed);
        }
        return null;
    }

    private String trimPrefix(String s) {
        if (s.startsWith(PREFIX_SHORT)) {
            return s.substring(PREFIX_SHORT.length()).trim();
        }
        if (s.startsWith(PREFIX_LONG)) {
            return s.substring(PREFIX_LONG.length()).trim();
        }
        return null;
    }

    private CommandThing untitledCommand(String trimmed) {
        String command = trimmed.substring(1).trim();
        return new CommandThing(command);

    }

    private CommandThing titledCommand(String s, String trimmed) {
        int end = findCloseParenthesis(trimmed);
        if (end == -1) {
            throw new IllegalArgumentException("Missing close parenthesis in " + s);
        }
        if (trimmed.length() <= end || trimmed.charAt(end + 1) != ':') {
            throw new IllegalArgumentException("Expected 'cmd(<name>):<command>' but got " + s);
        }

        String title = trimmed.substring(1, end);
        String command = trimmed.substring(end + 2).trim();

        return new CommandThing(command, title);
    }

    private int findCloseParenthesis(String trimmed) {
        int stack = 1;
        for (int i = 1; i < trimmed.length() - 1; i++) {
            switch (trimmed.charAt(i)) {
                case '(': stack++; break;
                case ')': stack--; break;
            }
            if (stack == 0) {
                return i;
            }
        }
        return -1;
    }
}
