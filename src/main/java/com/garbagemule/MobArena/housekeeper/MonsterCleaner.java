package com.garbagemule.MobArena.housekeeper;

import com.garbagemule.MobArena.framework.Arena;

class MonsterCleaner implements Housekeeper {

    private static final MonsterCleaner DEFAULT = new MonsterCleaner();

    private MonsterCleaner() {
        // OK BOSS
    }

    @Override
    public void clean(Arena arena) {
        arena.getMonsterManager().clear();
    }

    static MonsterCleaner getDefault() {
        return DEFAULT;
    }

}
