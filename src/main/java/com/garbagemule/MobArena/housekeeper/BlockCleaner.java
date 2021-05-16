package com.garbagemule.MobArena.housekeeper;

import com.garbagemule.MobArena.framework.Arena;
import org.bukkit.Material;

class BlockCleaner implements Housekeeper {

    private static final BlockCleaner DEFAULT = new BlockCleaner();

    private BlockCleaner() {
        // OK BOSS
    }

    @Override
    public void clean(Arena arena) {
        arena.getBlocks().forEach(block -> block.setType(Material.AIR));
        arena.getBlocks().clear();
    }

    static BlockCleaner getDefault() {
        return DEFAULT;
    }

}
