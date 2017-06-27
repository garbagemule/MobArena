package com.garbagemule.MobArena.things;

import com.garbagemule.MobArena.MobArena;

class MoneyThingParser implements ThingParser {
    private static final String PREFIX_LONG = "money:";
    private static final String PREFIX_SHORT = "$";

    private MobArena plugin;

    MoneyThingParser(MobArena plugin) {
        this.plugin = plugin;
    }

    @Override
    public MoneyThing parse(String s) {
        String money = trimPrefix(s);
        if (money == null) {
            return null;
        }

        Double value = valueOf(money);
        if (value == null) {
            return null;
        }

        return new MoneyThing(plugin, value);
    }

    private String trimPrefix(String s) {
        if (s.startsWith(PREFIX_SHORT)) {
            return s.substring(PREFIX_SHORT.length());
        }
        if (s.startsWith(PREFIX_LONG)) {
            return s.substring(PREFIX_LONG.length());
        }
        return null;
    }

    private Double valueOf(String money) {
        try {
            return Double.parseDouble(money);
        } catch (NumberFormatException e) {
            plugin.getLogger().warning("Invalid economy value: " + money);
            return null;
        }
    }
}
