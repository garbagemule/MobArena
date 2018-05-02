package com.garbagemule.MobArena.steps;

import com.garbagemule.MobArena.framework.Arena;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;

class ClearInventory extends PlayerStep {
    private final File inventories;
    private final Arena arena;

    private ItemStack[] contents;
    private File backup;

    private ClearInventory(Player player, Arena arena) {
        super(player);
        this.inventories = new File(arena.getPlugin().getDataFolder(), "inventories");
        this.arena = arena;
    }

    @Override
    public void run() {
        contents = player.getInventory().getContents();
        createBackup();

        player.getInventory().clear();
    }

    @Override
    public void undo() {
        player.getInventory().setContents(contents);

        arena.getInventoryManager().remove(player);
        deleteBackup();
    }

    private void createBackup() {
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.set("contents", contents);

        backup = new File(inventories, player.getUniqueId().toString());
        try {
            yaml.save(backup);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store inventory for " + player.getName(), e);
        }
        arena.getInventoryManager().put(player, contents);
    }

    private void deleteBackup() {
        try {
            Files.delete(backup.toPath());
        } catch (IOException e) {
            arena.getPlugin().getLogger().log(Level.WARNING, "Couldn't delete backup inventory file for " + player.getName(), e);
        }
    }

    static StepFactory create(Arena arena) {
        return player -> new ClearInventory(player, arena);
    }
}
