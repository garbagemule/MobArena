package com.garbagemule.MobArena.util.inventory;

import com.garbagemule.MobArena.MobArena;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class InventoryManager
{
    private Map<Player, ItemStack[]> items;
    private Map<Player, ItemStack[]> armor;
    
    public InventoryManager() {
        this.items = new HashMap<>();
        this.armor = new HashMap<>();
    }

    public void put(Player p, ItemStack[] items, ItemStack[] armor) {
        this.items.put(p, items);
        this.armor.put(p, armor);
    }

    public void equip(Player p) {
        ItemStack[] items = this.items.get(p);
        ItemStack[] armor = this.armor.get(p);
        if (items == null || armor == null) {
            return;
        }
        p.getInventory().setContents(items);
        p.getInventory().setArmorContents(armor);
    }

    public void remove(Player p) {
        items.remove(p);
        armor.remove(p);
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

            if (!file.exists()) {
                return false;
            }

            YamlConfiguration config = new YamlConfiguration();
            config.load(file);

            ItemStack[] items = config.getList("items").toArray(new ItemStack[0]);
            ItemStack[] armor = config.getList("armor").toArray(new ItemStack[0]);
            p.getInventory().setContents(items);
            p.getInventory().setArmorContents(armor);
            
            file.delete();
            return true;
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to restore inventory for " + p.getName(), e);
            return false;
        }
    }
}
