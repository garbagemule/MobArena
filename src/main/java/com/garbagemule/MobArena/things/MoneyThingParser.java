package com.garbagemule.MobArena.things;

import com.garbagemule.MobArena.MobArena;
import com.garbagemule.MobArena.finance.Finance;

class MoneyThingParser implements ThingParser {

    private static final String PREFIX_LONG = "money:";
    private static final String PREFIX_SHORT = "$";

    private final MobArena plugin;

    MoneyThingParser(MobArena plugin) {
        this.plugin = plugin;
    }

    @Override
    public MoneyThing parse(String s) {
        String money = trimPrefix(s);
        if (money == null) {
            return null;
        }

        Finance finance = plugin.getFinance();
        double amount = Double.parseDouble(money);

        return new MoneyThing(finance, amount);
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
