package com.garbagemule.MobArena.things;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class RandomThingPickerParser implements ThingPickerParser {

    private final ThingPickerParser parser;
    private final Random random;

    public RandomThingPickerParser(
        ThingPickerParser parser,
        Random random
    ) {
        this.parser = parser;
        this.random = random;
    }

    @Override
    public ThingPicker parse(String s) {
        if (!(s.startsWith("random(") && s.endsWith(")"))) {
            return null;
        }

        String inner = ParserUtil.extractBetween(s, '(', ')');
        List<ThingPicker> pickers = ParserUtil.split(inner)
            .stream()
            .map(String::trim)
            .map(parser::parse)
            .collect(Collectors.toList());

        if (pickers.isEmpty()) {
            throw new IllegalArgumentException("Nothing to pick from: " + s);
        }
        if (pickers.size() == 1) {
            return pickers.get(0);
        }

        return new RandomThingPicker(pickers, random);
    }

}
