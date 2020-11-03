package com.garbagemule.MobArena.things;

public class NothingPickerParser implements ThingPickerParser {

    @Override
    public ThingPicker parse(String s) {
        if (!s.equalsIgnoreCase("nothing")) {
            return null;
        }
        return NothingPicker.getInstance();
    }

}
