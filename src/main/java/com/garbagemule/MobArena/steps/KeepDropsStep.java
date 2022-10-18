package com.garbagemule.MobArena.steps;

import com.garbagemule.MobArena.framework.Arena;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;

public class KeepDropsStep extends PlayerStep {

    private final File keepDrops;
    private final Arena arena;

    private ItemStack[] contents;
    private File backup;

    private KeepDropsStep(Player player, Arena arena) {
        super(player);
        this.keepDrops = new File(arena.getPlugin().getDataFolder(), "extra-drops");
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
        for (ItemStack itemStack : contents) {
            if (itemStack == null) continue;
            player.getInventory().addItem(itemStack);
        }

        arena.getInventoryManager().removeKeepDrops(player);
        deleteBackup();
    }

    private void createBackup() {
        arena.getInventoryManager().removeOriginalItems(arena.getPlugin(), player);
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.set("contents", contents);

        backup = new File(keepDrops, player.getUniqueId().toString());
        try {
            yaml.save(backup);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store keep-drops for " + player.getName(), e);
        }
        arena.getInventoryManager().putKeepDrops(player, contents);
    }

    private void deleteBackup() {
        try {
            Files.delete(backup.toPath());
        } catch (IOException e) {
            arena.getPlugin().getLogger().log(Level.WARNING, "Couldn't delete backup inventory file for " + player.getName(), e);
        }
    }

    public static StepFactory create(Arena arena) {
        return player -> new KeepDropsStep(player, arena);
    }
}
