package com.garbagemule.MobArena.things;

import com.garbagemule.MobArena.MobArena;

import java.util.ArrayList;
import java.util.List;

public class ThingManager implements ThingParser {
    private List<ThingParser> parsers;

    public ThingManager(MobArena plugin) {
        parsers = new ArrayList<>();
        parsers.add(new CommandThingParser());
        parsers.add(new MoneyThingParser(plugin));
        parsers.add(new ItemStackThingParser());
    }

    /**
     * Register a new thing parser in the manager.
     *
     * @param parser a thing parser, non-null
     * @param beforeCoreParsers if true, the parser will be invoked before the
     * core parsers, effectively allowing it to override the defaults. Only do
     * this if you know what you're doing.
     */
    public void register(ThingParser parser, boolean beforeCoreParsers) {
        if (beforeCoreParsers) {
            parsers.add(0, parser);
        } else {
            parsers.add(parser);
        }
    }

    /**
     * Register a new thing parser in the manager.
     * <p>
     * This is a convenience method for {@link #register(ThingParser, boolean)}
     * that registers the parser <i>after</i> the core parsers.
     *
     * @param parser a thing parser, non-null
     */
    public void register(ThingParser parser) {
        register(parser, false);
    }

    @Override
    public Thing parse(String s) {
        if (s == null || s.isEmpty()) return null;
        for (ThingParser parser : parsers) {
            Thing thing = parser.parse(s);
            if (thing != null) {
                return thing;
            }
        }
        return null;
    }

    public List<Thing> parseThings(String s) {
        if (s == null) {
            return new ArrayList<>(1);
        }

        String[] items = s.split(",");
        List<Thing> result = new ArrayList<>(items.length);

        for (String item : items) {
            Thing thing = parse(item.trim());
            if (thing != null) {
                result.add(thing);
            }
        }

        return result;
    }
}
