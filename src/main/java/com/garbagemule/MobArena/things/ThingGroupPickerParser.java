package com.garbagemule.MobArena.things;

import java.util.List;
import java.util.stream.Collectors;

public class ThingGroupPickerParser implements ThingPickerParser {

    private final ThingPickerParser parser;

    public ThingGroupPickerParser(ThingPickerParser parser) {
        this.parser = parser;
    }

    @Override
    public ThingPicker parse(String s) {
        if (!(s.startsWith("all(") && s.endsWith(")"))) {
            return null;
        }

        String inner = ParserUtil.extractBetween(s, '(', ')');
        List<ThingPicker> pickers = ParserUtil.split(inner)
            .stream()
            .map(String::trim)
            .map(parser::parse)
            .collect(Collectors.toList());

        if (pickers.isEmpty()) {
            throw new IllegalArgumentException("Nothing to group: " + s);
        }
        if (pickers.size() == 1) {
            return pickers.get(0);
        }

        return new ThingGroupPicker(pickers);
    }

}
