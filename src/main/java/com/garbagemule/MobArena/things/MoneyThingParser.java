package com.garbagemule.MobArena.things;

import net.milkbowl.vault.economy.Economy;

class MoneyThingParser implements ThingParser {
    private static final String PREFIX_LONG = "money:";
    private static final String PREFIX_SHORT = "$";

    private Economy economy;

    MoneyThingParser(Economy economy) {
        this.economy = economy;
    }

    @Override
    public MoneyThing parse(String s) {
        String money = trimPrefix(s);
        if (money == null) {
            return null;
        }
        return new MoneyThing(economy, Double.parseDouble(money));
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
