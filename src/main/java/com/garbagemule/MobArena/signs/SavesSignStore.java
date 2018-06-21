package com.garbagemule.MobArena.signs;

import com.garbagemule.MobArena.MobArena;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class SavesSignStore {

    private final MobArena plugin;

    SavesSignStore(MobArena plugin) {
        this.plugin = plugin;
    }

    void save(SignStore signStore) {
        YamlConfiguration yaml = new YamlConfiguration();
        List<ArenaSign> values = new ArrayList<>(signStore.findAll());
        yaml.set("signs", values);
        try {
            File data = new File(plugin.getDataFolder(), "data");
            yaml.options().header("MobArena Sign Store\n\nPlease DON'T edit this file by hand!\n");
            yaml.save(new File(data, SignStore.FILENAME));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

}
