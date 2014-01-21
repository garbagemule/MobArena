package com.garbagemule.MobArena.util.config;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Set;

public class ConfigUtils
{
    public static void addIfEmpty(Plugin plugin, String resource, ConfigurationSection section) {
        process(plugin, resource, section, true, false);
    }

    public static void addMissingRemoveObsolete(Plugin plugin, String resource, ConfigurationSection section) {
        process(plugin, resource, section, false, true);
    }

    public static void addMissingRemoveObsolete(File file, YamlConfiguration defaults, FileConfiguration config) {
        try {
            process(defaults, config, false, true);
            config.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void process(Plugin plugin, String resource, ConfigurationSection section, boolean addOnlyIfEmpty, boolean removeObsolete) {
        try {
            YamlConfiguration defaults = new YamlConfiguration();
            defaults.load(plugin.getResource("res/" + resource));

            process(defaults, section, addOnlyIfEmpty, removeObsolete);
            plugin.saveConfig();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void process(YamlConfiguration defaults, ConfigurationSection section, boolean addOnlyIfEmpty, boolean removeObsolete) {
        Set<String> present = section.getKeys(true);
        Set<String> required = defaults.getKeys(true);
        if (!addOnlyIfEmpty || present.isEmpty()) {
            for (String req : required) {
                if (!present.remove(req)) {
                    section.set(req, defaults.get(req));
                }
            }
        }
        if (removeObsolete) {
            for (String obs : present) {
                section.set(obs, null);
            }
        }
    }

    public static ConfigurationSection makeSection(ConfigurationSection config, String section) {
        if (!config.contains(section)) {
            return config.createSection(section);
        } else {
            return config.getConfigurationSection(section);
        }
    }

    public static Location parseLocation(ConfigurationSection config, String path, World world) {
        String value = config.getString(path);
        if (value == null) return null;

        String[] parts = value.split(",");
        if (parts.length < 3) {
            throw new IllegalArgumentException("A location must be at least (x,y,z)");
        }
        Double x = Double.parseDouble(parts[0]);
        Double y = Double.parseDouble(parts[1]);
        Double z = Double.parseDouble(parts[2]);
        if (parts.length == 3) {
            return new Location(world, x, y, z);
        }
        if (parts.length < 5) {
            throw new IllegalArgumentException("Expected location of type (x,y,z,yaw,pitch)");
        }
        Float yaw = Float.parseFloat(parts[3]);
        Float pit = Float.parseFloat(parts[4]);
        if (world == null) {
            if (parts.length != 6) {
                throw new IllegalArgumentException("Expected location of type (x,y,z,yaw,pitch,world)");
            }
            world = Bukkit.getWorld(parts[5]);
        }
        return new Location(world, x, y, z, yaw, pit);
    }

    public static void setLocation(ConfigurationSection config, String path, Location location) {
        if (location == null) {
            config.set(path, null);
            return;
        }
        String x = twoPlaces(location.getX());
        String y = twoPlaces(location.getY());
        String z = twoPlaces(location.getZ());

        String yaw = twoPlaces(location.getYaw(),   true);
        String pit = twoPlaces(location.getPitch(), true);

        String world = location.getWorld().getName();

        StringBuilder buffy = new StringBuilder();
        buffy.append(x).append(",").append(y).append(",").append(z);
        buffy.append(",").append(yaw).append(",").append(pit);
        buffy.append(",").append(world);

        config.set(path, buffy.toString());
    }

    private static String twoPlaces(double value, boolean force) {
        return force ? DF_FORCE.format(value) : DF_NORMAL.format(value);
    }

    private static String twoPlaces(double value) {
        return twoPlaces(value, false);
    }
    private static final DecimalFormat DF_NORMAL = new DecimalFormat("0.##");
    private static final DecimalFormat DF_FORCE  = new DecimalFormat("0.0#");
}
