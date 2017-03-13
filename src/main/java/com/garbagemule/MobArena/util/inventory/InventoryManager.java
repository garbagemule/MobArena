package com.garbagemule.MobArena.util.inventory;

import com.garbagemule.MobArena.MobArena;
import com.garbagemule.MobArena.framework.Arena;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryManager
{
    private File dir;
    private Map<Player,ItemStack[]> items, armor;
    
    public InventoryManager(Arena arena) {
        this.dir    = new File(arena.getPlugin().getDataFolder(), "inventories");
        this.dir.mkdir();
        
        this.items  = new HashMap<>();
        this.armor  = new HashMap<>();
    }
    
    public void storeInv(Player p) throws IOException {
        // Avoid overwrites
        if (items.containsKey(p)) return;
        
        // Fetch the player's items and armor
        ItemStack[] items = p.getInventory().getContents();
        ItemStack[] armor = p.getInventory().getArmorContents();
        
        // Store them in memory
        this.items.put(p, items);
        this.armor.put(p, armor);
        
        // And on disk
        File file = new File(dir, p.getName());
        YamlConfiguration config = new YamlConfiguration();
        config.set("items", items);
        config.set("armor", armor);
        config.save(file);
        
        p.updateInventory();
    }
    
    public void restoreInv(Player p) throws IOException, InvalidConfigurationException {
        // Try to grab the items from memory first
        ItemStack[] items = this.items.get(p);
        ItemStack[] armor = this.armor.get(p);
        
        // If we can't restore from memory, restore from file
        if (items == null || armor == null) {
            File file = new File(dir, p.getName());

            YamlConfiguration config = new YamlConfiguration();
            config.load(file);
            
            // Get the items and armor lists
            List<?> itemsList = config.getList("items");
            List<?> armorList = config.getList("armor");
            
            // Turn the lists into arrays
            items = itemsList.toArray(new ItemStack[itemsList.size()]);
            armor = armorList.toArray(new ItemStack[armorList.size()]);
        }
        
        // Set the player inventory contents
        p.getInventory().setContents(items);
        p.getInventory().setArmorContents(armor);
    }

    public void clearCache(Player p) {
        items.remove(p);
        armor.remove(p);

        File file = new File(dir, p.getName());
        if (file.exists()) {
            file.delete();
        }
    }
    
    /**
     * Clear a player's inventory completely.
     * @param p a player
     */
    public void clearInventory(Player p) {
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
            if (stack != null && stack.getTypeId() != 0)
                return false;
        }

        for (ItemStack stack : armor) {
            if (stack != null && stack.getTypeId() != 0)
                return false;
        }
        
        return true;
    }
    
    public static boolean restoreFromFile(MobArena plugin, Player p) {
        try {
            // Grab the file and load the config
            File dir = new File(plugin.getDataFolder(), "inventories");
            File file = new File(dir, p.getName());
            YamlConfiguration config = new YamlConfiguration();
            config.load(file);
            
            // Get the items and armor lists
            List<?> itemsList = config.getList("items");
            List<?> armorList = config.getList("armor");
            
            // Turn the lists into arrays
            ItemStack[] items = itemsList.toArray(new ItemStack[itemsList.size()]);
            ItemStack[] armor = armorList.toArray(new ItemStack[armorList.size()]);
            
            // Set the player inventory contents
            p.getInventory().setContents(items);
            p.getInventory().setArmorContents(armor);
            
            // Delete files
            file.delete();
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
