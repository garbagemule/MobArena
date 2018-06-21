package com.garbagemule.MobArena.signs;

import org.bukkit.Location;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

class SignStore {

    static final String FILENAME = "signs.data";

    private final Map<Location, ArenaSign> signs;

    SignStore(List<ArenaSign> signs) {
        this.signs = signs.stream()
            .collect(Collectors.toMap(
                sign -> sign.location,
                sign -> sign
            ));
    }

    void store(ArenaSign sign) {
        signs.put(sign.location, sign);
    }

    Optional<ArenaSign> remove(Location location) {
        ArenaSign sign = signs.remove(location);
        return Optional.ofNullable(sign);
    }

    Collection<ArenaSign> findAll() {
        return signs.values();
    }

    Optional<ArenaSign> findByLocation(Location location) {
        ArenaSign sign = signs.get(location);
        return Optional.ofNullable(sign);
    }

    List<ArenaSign> findByArenaId(String arenaId) {
        return signs.values().stream()
            .filter(sign -> sign.arenaId.equals(arenaId))
            .collect(Collectors.toList());
    }

}
