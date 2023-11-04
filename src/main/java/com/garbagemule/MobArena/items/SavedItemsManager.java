package com.garbagemule.MobArena.items;

import com.garbagemule.MobArena.MobArena;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class SavedItemsManager {

    private static final String FOLDER_NAME = "items";
    private static final String FILE_EXT = ".yml";
    private static final String YAML_KEY = "item";

    private final MobArena plugin;
    private final Path folder;
    private final Map<String, ItemStack> items;

    public SavedItemsManager(MobArena plugin) {
        this.plugin = plugin;
        this.folder = plugin.getDataFolder().toPath().resolve(FOLDER_NAME);
        this.items = new HashMap<>();
    }

    public void reload() {
        try {
            Files.createDirectories(folder);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to create items folder", e);
        }

        loadItems(folder);
    }

    private void loadItems(Path folder) {
        items.clear();

        try (Stream<Path> candidates = Files.list(folder)) {
            candidates.forEach(this::loadItem);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load saved items", e);
        }

        if (!items.isEmpty()) {
            plugin.getLogger().info("Loaded " + items.size() + " saved item(s).");
        }
    }

    private void loadItem(Path path) {
        if (!Files.isRegularFile(path)) {
            return;
        }

        String filename = path.getFileName().toString();
        if (!filename.endsWith(FILE_EXT)) {
            return;
        }

        YamlConfiguration yaml = new YamlConfiguration();
        try {
            yaml.load(path.toFile());
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load saved item from " + path, e);
        }

        ItemStack stack = yaml.getItemStack(YAML_KEY);
        if (stack == null) {
            throw new IllegalStateException("No item found in saved item file " + path);
        }

        String key = filename.substring(0, filename.length() - FILE_EXT.length());
        items.put(key, stack);
    }

    public List<String> getKeys() {
        return new ArrayList<>(items.keySet());
    }

    public ItemStack getItem(String key) {
        ItemStack stack = items.get(key);
        if (stack == null) {
            return null;
        }
        return stack.clone();
    }

    public void saveItem(String key, ItemStack stack) throws IOException {
        Path file = folder.resolve(key + FILE_EXT);
        Files.deleteIfExists(file);

        YamlConfiguration yaml = new YamlConfiguration();
        yaml.set(YAML_KEY, stack);
        yaml.save(file.toFile());

        items.put(key, stack.clone());
    }

    public void deleteItem(String key) throws IOException {
        items.remove(key);

        Path file = folder.resolve(key + FILE_EXT);
        try {
            Files.delete(file);
        } catch (NoSuchFileException e) {
            throw new IllegalArgumentException("File " + file + " not found");
        }
    }

}
