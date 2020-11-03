package com.garbagemule.MobArena.things;

import java.util.List;
import java.util.stream.Collectors;

public class ThingGroupPicker implements ThingPicker {

    private final List<ThingPicker> pickers;

    public ThingGroupPicker(List<ThingPicker> pickers) {
        this.pickers = pickers;
    }

    @Override
    public Thing pick() {
        List<Thing> things = pickers.stream()
            .map(ThingPicker::pick)
            .collect(Collectors.toList());

        return new ThingGroup(things);
    }

    @Override
    public String toString() {
        String list = pickers.stream()
            .map(ThingPicker::toString)
            .collect(Collectors.joining(" and "));
        return "(" + list + ")";
    }

}
