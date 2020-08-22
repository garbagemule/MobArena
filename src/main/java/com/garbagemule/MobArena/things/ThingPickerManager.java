package com.garbagemule.MobArena.things;

import java.util.ArrayList;
import java.util.List;

public class ThingPickerManager implements ThingPickerParser {

    private final List<ThingPickerParser> parsers;
    private final ThingParser things;

    public ThingPickerManager(ThingParser things) {
        this.parsers = new ArrayList<>();
        this.things = things;
    }

    public void register(ThingPickerParser parser) {
        parsers.add(parser);
    }

    @Override
    public ThingPicker parse(String s) {
        for (ThingPickerParser parser : parsers) {
            ThingPicker picker = parser.parse(s);
            if (picker != null) {
                return picker;
            }
        }
        Thing thing = things.parse(s);
        return new SingleThingPicker(thing);
    }

}
