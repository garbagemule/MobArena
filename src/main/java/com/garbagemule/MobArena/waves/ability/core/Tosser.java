package com.garbagemule.MobArena.waves.ability.core;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.Vector;

import java.util.logging.Level;
import java.util.logging.Logger;

class Tosser {

    private static final Logger log;

    static {
        PluginManager manager = Bukkit.getPluginManager();
        Plugin plugin = manager.getPlugin("MobArena");
        if (plugin == null) {
            log = Bukkit.getLogger();
        } else {
            log = plugin.getLogger();
        }
    }

    static void yeet(Entity victim, Location origin) {
        Location point = victim.getLocation();

        double x = point.getX() - origin.getX();
        if (Math.abs(x) <= 0.001 || 10_000 <= Math.abs(x)) {
            log.warning("[yeet] suspicious x value: " + x);
        }

        double z = point.getZ() - origin.getZ();
        if (Math.abs(z) <= 0.001 || 10_000 <= Math.abs(z)) {
            log.warning("[yeet] Suspicious z value: " + z);
        }

        Vector direction = new Vector(x, 0, z);
        direction.normalize();
        direction.setY(0.8);

        log.info("[yeet] final direction: " + direction);
        try {
            victim.setVelocity(direction);
        } catch (Exception e) {
            log.log(Level.SEVERE, "[yeet] failed to set player velocity", e);
        }
    }

    static void yoink(Entity victim, Location destination) {
        Location point = victim.getLocation();

        double x = destination.getX() - point.getX();
        if (Math.abs(x) <= 0.001 || 10_000 <= Math.abs(x)) {
            log.warning("[yoink] suspicious x value: " + x);
        }

        double z = destination.getZ() - point.getZ();
        if (Math.abs(z) <= 0.001 || 10_000 <= Math.abs(z)) {
            log.warning("[yoink] suspicious z value: " + z);
        }

        double a = Math.abs(x);
        double b = Math.abs(z);
        double c = Math.sqrt((a * a) + (b * b));

        Vector direction = new Vector(x, 0, z);
        direction.normalize();
        direction.multiply(c * 0.3);
        direction.setY(0.8);

        log.info("[yoink] final direction: " + direction);
        try {
            victim.setVelocity(direction);
        } catch (Exception e) {
            log.log(Level.SEVERE, "[yoink] failed to set player velocity", e);
        }
    }

}
