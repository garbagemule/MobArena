package com.garbagemule.MobArena.signs;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;

public class ArenaSign implements ConfigurationSerializable {

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

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = new HashMap<>();
        result.put("location", location);
        result.put("templateId", templateId);
        result.put("arenaId", arenaId);
        result.put("type", type);
        return result;
    }

    @SuppressWarnings("WeakerAccess")
    public static ArenaSign deserialize(Map<String, Object> map) {
        try {
            Location location = (Location) map.get("location");
            String templateId = (String) map.get("templateId");
            String arenaId = (String) map.get("arenaId");
            String type = (String) map.get("type");
            return new ArenaSign(location, templateId, arenaId, type);
        } catch (ClassCastException e) {
            String msg = "An arena sign in " + SignStore.FILENAME + " is invalid! You may have to delete the file.";
            throw new IllegalStateException(msg);
        }
    }

}
