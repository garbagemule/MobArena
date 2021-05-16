package com.garbagemule.MobArena.housekeeper;

import java.util.logging.Logger;

public final class Housekeepers {

    private static final Housekeeper DEFAULT = new CompositeHousekeeper(
        MonsterCleaner.getDefault(),
        BlockCleaner.getDefault(),
        EntityCleaner.getDefault()
    );

    public static Housekeeper getDefault() {
        return DEFAULT;
    }

    public static Housekeeper create(HousekeeperConfig config, Logger log) {
        MonsterCleaner monsters = MonsterCleaner.getDefault();
        BlockCleaner blocks = BlockCleaner.getDefault();
        EntityCleaner entities = EntityCleaner.create(config, log);

        return new CompositeHousekeeper(monsters, blocks, entities);
    }

}
