package com.garbagemule.MobArena.things;

/**
 * A thing parser takes a string as input and returns either an instance of
 * {@link Thing} or null.
 */
public interface ThingParser {
    /**
     * Parse the given string, returning a {@link Thing} instance on success,
     * otherwise null.
     *
     * @param s a string to parse
     * @return an instance of {@link Thing}, or null
     */
    Thing parse(String s);
}
