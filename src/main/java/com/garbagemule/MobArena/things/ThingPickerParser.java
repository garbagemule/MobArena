package com.garbagemule.MobArena.things;

public interface ThingPickerParser {

    /**
     * Parse the given string, returning a {@link ThingPicker} instance on
     * success, otherwise null.
     *
     * @param s a string to parse
     * @return an instance of {@link ThingPicker}, or null
     */
    ThingPicker parse(String s);

}
