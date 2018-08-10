package com.garbagemule.MobArena.config;

import com.garbagemule.MobArena.MobArena;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class LoadsConfigFile {

    private final MobArena plugin;

    public LoadsConfigFile(MobArena plugin) {
        this.plugin = plugin;
    }

    public FileConfiguration load() {
        try {
            return loadConfiguration();
        } catch (IOException | InvalidConfigurationException e) {
            throw new IllegalStateException("Failed to load config-file", e);
        }
    }

    private FileConfiguration loadConfiguration() throws IOException, InvalidConfigurationException {
        File folder = createDataFolder();
        File file = new File(folder, "config.yml");
        if (!file.exists()) {
            plugin.getLogger().info("No config-file found. Creating default...");
            plugin.saveDefaultConfig();
        }
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.load(file);
        return yaml;
    }

    private File createDataFolder() {
        File folder = plugin.getDataFolder();
        if (!folder.exists()) {
            if (!folder.mkdirs()) {
                throw new IllegalStateException("Failed to create data folder");
            }
        }
        return folder;
    }

}
