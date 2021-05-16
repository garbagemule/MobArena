package com.garbagemule.MobArena.housekeeper;

import com.garbagemule.MobArena.framework.Arena;

import java.util.Arrays;
import java.util.List;

class CompositeHousekeeper implements Housekeeper {

    private final List<Housekeeper> minions;

    CompositeHousekeeper(Housekeeper... minions) {
        this.minions = Arrays.asList(minions);
    }

    @Override
    public void clean(Arena arena) {
        minions.forEach(minion -> minion.clean(arena));
    }

}
