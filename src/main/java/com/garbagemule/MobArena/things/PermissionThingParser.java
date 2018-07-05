package com.garbagemule.MobArena.things;

import com.garbagemule.MobArena.MobArena;

class PermissionThingParser implements ThingParser {
    private static final String PREFIX = "perm:";

    private MobArena plugin;

    PermissionThingParser(MobArena plugin) {
        this.plugin = plugin;
    }

    @Override
    public PermissionThing parse(String s) {
        String value = trimPrefix(s);
        if (value == null) {
            return null;
        }

        if (value.startsWith("-") || value.startsWith("^")) {
            return new PermissionThing(value.substring(1), false, plugin);
        }
        return new PermissionThing(value, true, plugin);
    }

    private String trimPrefix(String s) {
        if (s.startsWith(PREFIX)) {
            return s.substring(PREFIX.length());
        }
        return null;
    }
}
