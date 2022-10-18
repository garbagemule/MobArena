package com.garbagemule.MobArena.util.inventory;

import com.garbagemule.MobArena.MobArena;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class InventoryManager
{
    private static final String ORIGINAL_ITEM_KEY = "original-item";

    private Map<Player, ItemStack[]> inventories;
    private Map<Player, ItemStack[]> keepDrops;

    public InventoryManager() {
        this.inventories = new HashMap<>();
        this.keepDrops = new HashMap<>();
    }

    public void put(Player p, ItemStack[] contents) {
        inventories.put(p, contents);
    }

    public void putKeepDrops(Player p, ItemStack[] contents) {
        keepDrops.put(p, contents);
    }

    public ItemStack[] getKeepDrops(Player p) {
        return keepDrops.get(p);
    }

    public void equip(Player p) {
        ItemStack[] contents = inventories.get(p);
        if (contents == null) {
            return;
        }
        p.getInventory().setContents(contents);
    }

    /**
     * Sets PDC on the player's items from when they first joined the arena
     * So that item drops in the arena can be kept when they leave
     */
    public void setOriginalItemsPDC(MobArena plugin, Player player) {
        final NamespacedKey key = new NamespacedKey(plugin, ORIGINAL_ITEM_KEY);
        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack == null) continue;
            final ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta == null) continue;
            itemMeta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);
            itemStack.setItemMeta(itemMeta);
        }
    }

    public void removeOriginalItems(MobArena plugin, Player player) {
        final NamespacedKey key = new NamespacedKey(plugin, ORIGINAL_ITEM_KEY);
        final ItemStack[] contents = player.getInventory().getContents();
        for (int i = 0; i < contents.length; i++) {
            final ItemStack itemStack = contents[i];
            if (itemStack == null) continue;
            final ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta == null) continue;
            if (itemMeta.getPersistentDataContainer().has(key, PersistentDataType.BYTE)) {
                itemStack.setType(Material.AIR);
                player.getInventory().clear(i);
            }
        }
    }

    public void remove(Player p) {
        inventories.remove(p);
    }

    public void removeKeepDrops(Player p) {
        keepDrops.remove(p);
    }

    /**
     * Clear a player's inventory completely.
     * @param p a player
     */
    public static void clearInventory(Player p) {
        PlayerInventory inv = p.getInventory();
        inv.clear();
        inv.setHelmet(null);
        inv.setChestplate(null);
        inv.setLeggings(null);
        inv.setBoots(null);
        inv.setItemInOffHand(null);
        InventoryView view = p.getOpenInventory();
        if (view != null) {
            view.setCursor(null);
            Inventory i = view.getTopInventory();
            if (i != null) {
                i.clear();
            }
        }
    }

    public static boolean hasEmptyInventory(Player p) {
        ItemStack[] inventory = p.getInventory().getContents();
        ItemStack[] armor     = p.getInventory().getArmorContents();

        // Check for null or id 0, or AIR
        for (ItemStack stack : inventory) {
            if (stack != null && stack.getType() != Material.AIR)
                return false;
        }

        for (ItemStack stack : armor) {
            if (stack != null && stack.getType() != Material.AIR)
                return false;
        }

        return true;
    }

    public static boolean restoreFromFile(MobArena plugin, Player p) {
        try {
            File inventories = new File(plugin.getDataFolder(), "inventories");
            File file = new File(inventories, p.getUniqueId().toString());

            restoreKeepDrops(plugin, p);

            if (!file.exists()) {
                return false;
            }

            YamlConfiguration config = new YamlConfiguration();
            config.load(file);

            ItemStack[] contents = config.getList("contents").toArray(new ItemStack[0]);
            p.getInventory().setContents(contents);

            file.delete();
            return true;
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to restore inventory for " + p.getName(), e);
            return false;
        }
    }

    private static void restoreKeepDrops(MobArena plugin, Player p) {
        try {
            File inventories = new File(plugin.getDataFolder(), "keep-drops");
            File file = new File(inventories, p.getUniqueId().toString());

            if (!file.exists()) {
                return;
            }

            YamlConfiguration config = new YamlConfiguration();
            config.load(file);

            ItemStack[] contents = config.getList("extra-drops").toArray(new ItemStack[0]);
            p.getInventory().addItem(contents);

            file.delete();
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to restore extra drops for " + p.getName(), e);
        }
    }
}
