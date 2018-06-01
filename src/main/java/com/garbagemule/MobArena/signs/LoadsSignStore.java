package com.garbagemule.MobArena.signs;

import com.garbagemule.MobArena.MobArena;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

class LoadsSignStore {

    private final MobArena plugin;

    LoadsSignStore(MobArena plugin) {
        this.plugin = plugin;
    }

    SignStore load() {
        YamlConfiguration yaml = new YamlConfiguration();
        try {
            File data = new File(plugin.getDataFolder(), "data");
            yaml.load(new File(data, SignStore.FILENAME));
        } catch (FileNotFoundException e) {
            return new SignStore(Collections.emptyList());
        } catch (InvalidConfigurationException e) {
            String msg = SignStore.FILENAME + " is invalid! You may have to delete it.";
            throw new IllegalStateException(msg, e);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        List<ArenaSign> signs = yaml.getList("signs").stream()
            .filter(raw -> raw instanceof ArenaSign)
            .map(raw -> (ArenaSign) raw)
            .collect(Collectors.toList());

        plugin.getLogger().info("Loaded " + signs.size() + " arena signs.");

        return new SignStore(signs);
    }

}
