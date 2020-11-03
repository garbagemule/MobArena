package com.garbagemule.MobArena.things;

import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class ThingGroup implements Thing {

    private final List<Thing> things;

    public ThingGroup(List<Thing> things) {
        this.things = things;
    }

    @Override
    public boolean giveTo(Player player) {
        things.forEach(thing -> thing.giveTo(player));
        return true;
    }

    @Override
    public boolean takeFrom(Player player) {
        return false;
    }

    @Override
    public boolean heldBy(Player player) {
        return false;
    }

    @Override
    public String toString() {
        return things.stream()
            .map(Thing::toString)
            .collect(Collectors.joining(" and "));
    }

}
