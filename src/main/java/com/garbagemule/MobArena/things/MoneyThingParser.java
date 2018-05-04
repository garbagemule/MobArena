package com.garbagemule.MobArena.things;

import net.milkbowl.vault.economy.Economy;

import java.util.function.Supplier;

class MoneyThingParser implements ThingParser {
    private static final String PREFIX_LONG = "money:";
    private static final String PREFIX_SHORT = "$";

    private Supplier<Economy> economy;

    MoneyThingParser(Supplier<Economy> economy) {
        this.economy = economy;
    }

    @Override
    public MoneyThing parse(String s) {
        String money = trimPrefix(s);
        if (money == null) {
            return null;
        }
        return new MoneyThing(economy.get(), Double.parseDouble(money));
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
}
