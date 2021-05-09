package com.garbagemule.MobArena.signs;

import org.bukkit.Location;

class ArenaSign {

    final Location location;
    final String templateId;
    final String arenaId;
    final String type;

    ArenaSign(Location location, String templateId, String arenaId, String type) {
        this.location = location;
        this.templateId = templateId;
        this.arenaId = arenaId;
        this.type = type;
    }

}
