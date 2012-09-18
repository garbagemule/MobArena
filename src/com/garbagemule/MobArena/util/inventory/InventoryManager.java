package com.garbagemule.MobArena.util.inventory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.garbagemule.MobArena.MobArena;
import com.garbagemule.MobArena.framework.Arena;

public class InventoryManager
{
    private static String EXT = ".inv";
    private File dir;
    private Map<Player,ItemStack[]> items, armor;
    
    public InventoryManager(Arena arena) {
        this.dir    = new File(arena.getPlugin().getDataFolder(), "inventories");
        this.dir.mkdir();
        
        this.items  = new HashMap<Player,ItemStack[]>();
        this.armor  = new HashMap<Player,ItemStack[]>();
    }
    
    /**
     * Store a player's inventory in memory and in a file.
     * @param p a player
     * @return true, if the inventory was stored, false if it already existed
     */
    public boolean storeInventory(Player p) {
        if (items.containsKey(p) && armor.containsKey(p)) {
            return false;
        }
        
        // Store the inventory in memory
        PlayerInventory inv = p.getInventory();
        ItemStack[] memItems = inv.getContents();
        ItemStack[] memArmor = inv.getArmorContents();
        items.put(p, memItems);
        armor.put(p, memArmor);
        
        // And save it to a file
        saveToFile(p);
        clearInventory(p);
        return true;
    }
    
    /**
     * Restore a player's inventory from memory or from a file
     * Note that this method is idempotent; calling it multiple times will
     * not duplicate player items or anything like that.
     * @param p a player
     * @return true, if the inventory was restored successfully, false otherwise
     */
    public boolean restoreInventory(Player p) {
        ItemStack[] memItems = items.remove(p);
        ItemStack[] memArmor = armor.remove(p);
        
        // Restore from inventory if possible
        if (memItems != null && memArmor != null) {
            PlayerInventory inv = p.getInventory();
            
            inv.setContents(memItems);
            inv.setArmorContents(memArmor);
            
            new File(dir, p.getName() + EXT).delete();
            
            return true;
        }
        
        // Otherwise, load from file
        return loadFromFile(p);
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
        InventoryView view = p.getOpenInventory();
        if (view != null) {
            view.setCursor(null);
            Inventory i = view.getTopInventory();
            if (i != null) {
                i.clear();
            }
        }
    }
    
    private boolean saveToFile(Player p) {
        File file = new File(dir, p.getName() + EXT);
        
        // If the file exists, encourage restoring first.
        if (file.exists()) {
            return false;
        }
        
        try {
            SerializableInventory inv = new SerializableInventory(p.getInventory());
            
            FileOutputStream fos   = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            
            oos.writeObject(inv);
            oos.close();
            
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private boolean loadFromFile(Player p) {
        File file = new File(dir, p.getName() + EXT);
        
        // If the file doesn't exist, we can't restore from it!
        if (!file.exists()) {
            return false;
        }
        
        try {
            FileInputStream fis   = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            
            Object o = ois.readObject();
            ois.close();
            
            SerializableInventory inv = (SerializableInventory) o;
            SerializableInventory.loadContents(p, inv);
            
            file.delete();
            
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean hasEmptyInventory(Player p) {
        ItemStack[] inventory = p.getInventory().getContents();
        ItemStack[] armor     = p.getInventory().getArmorContents();
        
        // For inventory, check for null
        for (ItemStack stack : inventory) {
            if (stack != null)
                return false;
        }
        
        // For armor, check for id 0, or AIR
        for (ItemStack stack : armor) {
            if (stack.getTypeId() != 0)
                return false;
        }
        
        return true;
    }
    
    /**
     * Restore a player's inventory from file
     * @param plugin MobArena instance
     * @param p a player
     * @return true, if the inventory was restored successfully, false otherwise
     */
    public static boolean restoreFromFile(MobArena plugin, Player p) {
        File dir = new File(plugin.getDataFolder(), "inventories");
        
        File file = new File(dir, p.getName() + EXT);
        
        // If the file doesn't exist, we can't restore from it!
        if (!file.exists()) {
            return false;
        }
        
        try {
            FileInputStream fis   = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            
            Object o = ois.readObject();
            ois.close();
            
            SerializableInventory inv = (SerializableInventory) o;
            SerializableInventory.loadContents(p, inv);
            
            file.delete();
            
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
