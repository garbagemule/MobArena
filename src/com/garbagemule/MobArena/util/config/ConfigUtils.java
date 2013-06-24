package com.garbagemule.MobArena.util.config;

import java.util.Set;

import com.garbagemule.MobArena.Messenger;
import org.bukkit.configuration.file.YamlConfiguration;

import com.garbagemule.MobArena.MobArena;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.util.FileUtils;

public class ConfigUtils
{
    public static void addMissingNodes(Config config, String path, String filename) {
        assertNodes(config, path, filename, true);
    }
    
    public static void replaceAllNodes(Config config, String path, String filename) {
        assertNodes(config, path, filename, false);
    }
    
    private static void assertNodes(Config config, String path, String filename, boolean keepOthers) {
        // Grab the section that the path is pointing to.
        ConfigSection section = config.getConfigSection(path);
        
        // If null, create the node.
        if (section == null) {
            config.set(path, "");
            section = config.getConfigSection(path);
        }

        try {
            // Extract the yml file.
            YamlConfiguration ymlConfig = FileUtils.getConfig(filename);
        
            // Assert the nodes.
            assertNodes(section, ymlConfig, keepOthers);
        } catch (Exception e) {
            e.printStackTrace();
            Messenger.severe("Failed to load '" + filename + "'. Restart required!");
        }
    }
    
    private static void assertNodes(ConfigSection config, YamlConfiguration ymlConfig, boolean keepOthers) {
        if (config == null || ymlConfig == null) return;
        
        // Grab the default keys.
        Set<String> keys = ymlConfig.getKeys(false);
        if (keys == null || keys.isEmpty()) return;
        
        // First ensure that all default nodes exist
        for (String key : keys) {
            if (config.get(key) == null) {
                Object o = ymlConfig.get(key);
                config.set(key, o);
            }
        }
        
        // If any other nodes in the config should remain, return
        if (keepOthers) return;

        // Otherwise, grab all the current nodes.
        Set<String> oldKeys = config.getKeys();
        if (oldKeys == null || oldKeys.isEmpty()) return;

        // Remove all nodes that aren't in the defaults.
        for (String old : oldKeys) {
            if (!keys.contains(old)) {
                config.set(old, null);
            }
        }
    }
    
    public static String waveRewardList(String arena, String type) {
        return "arenas." + arena + ".rewards.waves." + type;
    }
    
    public static String waveReward(String arena, String type, int wave) {
        return "arenas." + arena + ".rewards.waves." + type + "." + wave;
    }
    
    public static String waveReward(Arena arena, String type, int wave) {
        return "arenas." + arena.configName() + ".rewards.waves." + type + "." + wave;
    }
}
