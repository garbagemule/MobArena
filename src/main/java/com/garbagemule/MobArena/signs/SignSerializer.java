package com.garbagemule.MobArena.signs;

import org.bukkit.Location;
import org.bukkit.World;

class SignSerializer {

    String serialize(ArenaSign sign) {
        String id = sign.location.getWorld().getUID().toString();
        String name = sign.location.getWorld().getName();

        String x = String.valueOf(sign.location.getBlockX());
        String y = String.valueOf(sign.location.getBlockY());
        String z = String.valueOf(sign.location.getBlockZ());

        String arenaId = sign.arenaId;
        String type = sign.type;
        String templateId = sign.templateId;

        return String.join(";", id, name, x, y, z, arenaId, type, templateId);
    }

    ArenaSign deserialize(String input, World world) {
        String[] parts = input.split(";");
        if (parts.length != 8) {
            throw new IllegalArgumentException("Invalid input; expected 8 parts, got " + parts.length);
        }

        String id = parts[0];
        if (!id.equals(world.getUID().toString())) {
            throw new IllegalArgumentException("World mismatch");
        }

        int x = Integer.parseInt(parts[2]);
        int y = Integer.parseInt(parts[3]);
        int z = Integer.parseInt(parts[4]);

        Location location = new Location(world, x, y, z);
        String arenaId = parts[5];
        String type = parts[6];
        String templateId = parts[7];

        return new ArenaSign(location, templateId, arenaId, type);
    }

    boolean equal(String line1, String line2) {
        if (line1.equals(line2)) {
            return true;
        }

        String[] parts1 = line1.split(";");
        String[] parts2 = line2.split(";");

        // World ID and (x,y,z) are all that matter
        return parts1[0].equals(parts2[0])
            && parts1[2].equals(parts2[2])
            && parts1[3].equals(parts2[3])
            && parts1[4].equals(parts2[4]);
    }

}
