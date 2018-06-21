package com.garbagemule.MobArena.things;

import com.garbagemule.MobArena.util.PotionEffectParser;
import org.bukkit.potion.PotionEffect;

class PotionEffectThingParser implements ThingParser {
    private static final String PREFIX = "effect:";

    @Override
    public PotionEffectThing parse(String s) {
        String value = trimPrefix(s);
        if (value == null) {
            return null;
        }
        PotionEffect effect = PotionEffectParser.parsePotionEffect(value, false);
        if (effect == null) {
            return null;
        }
        return new PotionEffectThing(effect);
    }

    private String trimPrefix(String s) {
        if (s.startsWith(PREFIX)) {
            return s.substring(PREFIX.length());
        }
        return null;
    }
}
