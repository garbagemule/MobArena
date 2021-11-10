package com.garbagemule.MobArena.signs;

import lombok.RequiredArgsConstructor;
import org.bukkit.Location;

@RequiredArgsConstructor
class ArenaSign {

    final Location location;
    final String templateId;
    final String arenaId;
    final String type;

}
