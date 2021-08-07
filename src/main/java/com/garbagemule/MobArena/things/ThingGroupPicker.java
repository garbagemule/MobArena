package com.garbagemule.MobArena.things;

import java.util.List;
import java.util.Objects;
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
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

        if (things.isEmpty()) {
            return null;
        }

        if (things.size() == 1) {
            return things.get(0);
        }

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
