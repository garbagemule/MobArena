package com.garbagemule.MobArena.things;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;

import java.util.function.Supplier;

class InventoryThingParser implements ThingParser {

    private static final String PREFIX = "inv(";
    private static final String SUFFIX = ")";

    private final Server server;

    InventoryThingParser(Server server) {
        this.server = server;
    }

    @Override
    public InventoryThing parse(String s) {
        if (!s.startsWith(PREFIX) || !s.endsWith(SUFFIX)) {
            return null;
        }

        // Trim prefix and suffix
        int start = PREFIX.length();
        int end = s.length() - SUFFIX.length();
        String inner = s.substring(start, end);

        // Split by whitespace to get all the parts
        String[] parts = inner.split("\\s+");
        if (parts.length != 5) {
            throw new IllegalArgumentException("Expected format " + PREFIX + "world x y z slot" + SUFFIX + ", got: " + s);
        }

        // Extract location
        String name = parts[0];
        int x = Integer.parseInt(parts[1]);
        int y = Integer.parseInt(parts[2]);
        int z = Integer.parseInt(parts[3]);
        Supplier<Location> location = () -> {
            World world = server.getWorld(name);
            return new Location(world, x, y ,z);
        };

        // Determine type by slot value
        String slot = parts[4];
        if (slot.equals("all")) {
            return group(location);
        }
        if (slot.contains("-")) {
            return range(location, slot);
        }
        return index(location, slot);
    }

    private InventoryThing group(Supplier<Location> location) {
        return new InventoryGroupThing(location);
    }

    private InventoryThing range(Supplier<Location> location, String slot) {
        String[] indices = slot.split("-");
        if (indices.length != 2) {
            throw new IllegalArgumentException("Expected range format (e.g. 0-8), got: " + slot);
        }
        int first = Integer.parseInt(indices[0]);
        int last = Integer.parseInt(indices[1]);
        if (last < first) {
            throw new IllegalArgumentException("Range end is less than range start: " + slot);
        }
        return new InventoryRangeThing(location, first, last);
    }

    private InventoryThing index(Supplier<Location> location, String slot) {
        int index = Integer.parseInt(slot);
        return new InventoryIndexThing(location, index);
    }

}
