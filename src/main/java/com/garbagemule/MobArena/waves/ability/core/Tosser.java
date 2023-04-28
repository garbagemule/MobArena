package com.garbagemule.MobArena.waves.ability.core;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

class Tosser {

    static void yeet(Entity victim, Location origin) {
        Location point = victim.getLocation();

        double x = point.getX() - origin.getX();
        double z = point.getZ() - origin.getZ();

        Vector direction = new Vector(x, 0, z);
        direction.normalize();
        direction.setY(0.8);

        victim.setVelocity(direction);
    }

    static void yoink(Entity victim, Location destination) {
        Location point = victim.getLocation();

        double x = destination.getX() - point.getX();
        double z = destination.getZ() - point.getZ();

        double a = Math.abs(x);
        double b = Math.abs(z);
        double c = Math.sqrt((a * a) + (b * b));

        Vector direction = new Vector(x, 0, z);
        direction.normalize();
        direction.multiply(c * 0.3);
        direction.setY(0.8);

        victim.setVelocity(direction);
    }

}
