package com.garbagemule.MobArena.signs;

import org.bukkit.Location;

import java.util.Optional;

class RemovesSignAtLocation {

    private final SignStore signStore;
    private final SavesSignStore savesSignStore;

    RemovesSignAtLocation(
        SignStore signStore,
        SavesSignStore savesSignStore
    ) {
        this.signStore = signStore;
        this.savesSignStore = savesSignStore;
    }

    Optional<ArenaSign> remove(Location location) {
        Optional<ArenaSign> sign = signStore.remove(location);
        sign.ifPresent(s -> savesSignStore.save(signStore));
        return sign;
    }

}
