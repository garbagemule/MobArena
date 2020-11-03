package com.garbagemule.MobArena.things;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class RandomThingPicker implements ThingPicker {

    private final List<ThingPicker> pickers;
    private final Random random;

    public RandomThingPicker(List<ThingPicker> pickers, Random random) {
        this.pickers = pickers;
        this.random = random;
    }

    @Override
    public Thing pick() {
        int index = random.nextInt(pickers.size());
        return pickers.get(index).pick();
    }

    @Override
    public String toString() {
        String list = pickers.stream()
            .map(ThingPicker::toString)
            .collect(Collectors.joining(" or "));
        return "(" + list + ")";
    }

}
