package com.garbagemule.MobArena.signs;

import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;

class SetsLines {

    void set(Location location, String[] lines) {
        BlockState state = location.getBlock().getState();
        if (!(state instanceof Sign)) {
            return;
        }

        Sign sign = (Sign) state;
        for (int i = 0; i < lines.length; i++) {
            sign.setLine(i, lines[i]);
        }
        sign.update();
    }

}
