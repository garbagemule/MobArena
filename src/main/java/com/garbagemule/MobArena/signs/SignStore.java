package com.garbagemule.MobArena.signs;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class SignStore {

    private final Map<Location, ArenaSign> signs = new HashMap<>();

    ArenaSign findByLocation(Location location) {
        return signs.get(location);
    }

    List<ArenaSign> findByArenaId(String arenaId) {
        return signs.values().stream()
            .filter(sign -> sign.arenaId.equals(arenaId))
            .collect(Collectors.toList());
    }

    void add(ArenaSign sign) {
        signs.put(sign.location, sign);
    }

    ArenaSign removeByLocation(Location location) {
        return signs.remove(location);
    }

    List<ArenaSign> removeByWorld(World world) {
        List<ArenaSign> removed = new ArrayList<>();
        Iterator<ArenaSign> iterator = signs.values().iterator();
        while (iterator.hasNext()) {
            ArenaSign sign = iterator.next();
            if (sign.location.getWorld().equals(world)) {
                removed.add(sign);
                iterator.remove();
            }
        }
        return removed;
    }

}
