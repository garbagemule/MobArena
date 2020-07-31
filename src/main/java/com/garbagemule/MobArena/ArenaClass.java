package com.garbagemule.MobArena;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.framework.ArenaMaster;
import com.garbagemule.MobArena.things.Thing;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class ArenaClass
{
    private String configName, lowercaseName;
    private Thing helmet, chestplate, leggings, boots, offhand;
    private List<Thing> armor;
    private List<Thing> items;
    private List<Thing> effects;
    private List<Thing> perms;
    private List<Thing> lobbyperms;
    private boolean unbreakableWeapons, unbreakableArmor;
    private Thing price;
    private Location classchest;
    private String petName;

    /**
     * Create a new, empty arena class with the given name.
     * @param name the class name as it appears in the config-file
     */
    public ArenaClass(String name, Thing price, boolean unbreakableWeapons, boolean unbreakableArmor) {
        this.configName    = name;
        this.lowercaseName = name.toLowerCase().replace(" ", "");
        
        this.items = new ArrayList<>();
        this.armor = new ArrayList<>(4);
        this.effects = new ArrayList<>();
        this.perms = new ArrayList<>();
        this.lobbyperms = new ArrayList<>();

        this.unbreakableWeapons = unbreakableWeapons;
        this.unbreakableArmor = unbreakableArmor;

        this.price = price;
    }
    
    /**
     * Get the name of the arena class as it appears in the config-file.
     * @return the class name as it appears in the config-file
     */
    public String getConfigName() {
        return configName;
    }
    
    /**
     * Get the lowercase class name.
     * @return the lowercase class name
     */
    public String getLowercaseName() {
        return lowercaseName;
    }
    
    /**
     * Set the helmet slot for the class.
     * @param helmet a Thing
     */
    public void setHelmet(Thing helmet) {
        this.helmet = helmet;
    }
    
    /**
     * Set the chestplate slot for the class.
     * @param chestplate a Thing
     */
    public void setChestplate(Thing chestplate) {
        this.chestplate = chestplate;
    }
    
    /**
     * Set the leggings slot for the class.
     * @param leggings a Thing
     */
    public void setLeggings(Thing leggings) {
        this.leggings = leggings;
    }
    
    /**
     * Set the boots slot for the class.
     * @param boots a Thing
     */
    public void setBoots(Thing boots) {
        this.boots = boots;
    }
    
    /**
     * Set the off-hand slot for the class.
     * @param offhand a Thing
     */
    public void setOffHand(Thing offhand) {
        this.offhand = offhand;
    }

    /**
     * Add an item to the items list.
     * @param item a Thing
     */
    public void addItem(Thing item) {
        if (item != null) {
            items.add(item);
        }
    }
    
    /**
     * Replace the current items list with a new list of all the items in the given list.
     * This method uses the addItem() method for each item to ensure consistency.
     * @param items a list of Things
     */
    public void setItems(List<Thing> items) {
        this.items = new ArrayList<>(items.size());
        items.forEach(this::addItem);
    }
    
    /**
     * Replace the current armor list with the given list.
     * @param armor a list of Things
     */
    public void setArmor(List<Thing> armor) {
        this.armor = armor;
    }

    public void setEffects(List<Thing> effects) {
        this.effects = effects;
    }

    public String getPetName() {
        return this.petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }
    
    public boolean hasPermission(Player p) {
        String perm = "mobarena.classes." + configName;
        return !p.isPermissionSet(perm) || p.hasPermission(perm);
    }

    /**
     * Grants all of the class items and armor to the given player.
     * The normal items will be added to the inventory normally, while the
     * armor items will be verified as armor items and placed in their
     * appropriate slots. If any specific armor slots are specified, they
     * will overwrite any items in the armor list. 
     * @param p a player
     */
    public void grantItems(Player p) {
        PlayerInventory inv = p.getInventory();

        // Fork over the items.
        items.forEach(item -> item.giveTo(p));
        
        // Check for legacy armor-node items
        armor.forEach(thing -> thing.giveTo(p));

        // Check type specifics.
        if (helmet     != null) helmet.giveTo(p);
        if (chestplate != null) chestplate.giveTo(p);
        if (leggings   != null) leggings.giveTo(p);
        if (boots      != null) boots.giveTo(p);
        if (offhand    != null) offhand.giveTo(p);
    }

    public void grantPotionEffects(Player p) {
        effects.forEach(thing -> thing.giveTo(p));
    }
    
    /**
     * Add a permission value to the class.
     */
    public void addPermission(Thing permission) {
        perms.add(permission);
    }

    public void addLobbyPermission(Thing permission) {
        lobbyperms.add(permission);
    }

    /**
     * Grant the given player all the permissions of the class.
     * All permissions will be attached to a PermissionAttachment object, which
     * will be returned to the caller.
     * @param p a player
     */
    public void grantPermissions(Player p) {
        perms.forEach(perm -> perm.giveTo(p));
    }

    public void grantLobbyPermissions(Player p) {
        lobbyperms.forEach(perm -> perm.giveTo(p));
    }

    public Location getClassChest() {
        return classchest;
    }

    public void setClassChest(Location loc) {
        classchest = loc;
    }

    public boolean hasUnbreakableWeapons() {
        return unbreakableWeapons;
    }

    public boolean hasUnbreakableArmor() {
        return unbreakableArmor;
    }

    public Thing getPrice() {
        return price;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (this == o) return true;
        if (!this.getClass().equals(o.getClass())) return false;
        
        ArenaClass other = (ArenaClass) o;
        return other.lowercaseName.equals(this.lowercaseName);
    }
    
    @Override
    public int hashCode() {
        return lowercaseName.hashCode();
    }

    public static class MyItems extends ArenaClass {
        private ArenaMaster am;

        public MyItems(Thing price, boolean unbreakableWeapons, boolean unbreakableArmor, ArenaMaster am) {
            super("My Items", price, unbreakableWeapons, unbreakableArmor);
            this.am = am;
        }

        @Override
        public void grantItems(Player p) {
            Arena arena = am.getArenaWithPlayer(p);
            if (arena != null) {
                try {
                    arena.getInventoryManager().equip(p);
                    removeBannedItems(p.getInventory());
                } catch (Exception e) {
                    am.getPlugin().getLogger().severe("Failed to give " + p.getName() + " their own items: " + e.getMessage());
                }
            }
        }

        @Override
        public Location getClassChest() {
            return null;
        }

        private void removeBannedItems(PlayerInventory inv) {
            ItemStack[] contents = inv.getContents();
            IntStream.range(0, contents.length)
                .filter(i -> contents[i] != null)
                .filter(i -> isBanned(contents[i].getType()))
                .forEach(inv::clear);
        }

        private boolean isBanned(Material type) {
            switch (type) {
                case ENDER_PEARL:
                case ENDER_CHEST:
                case SHULKER_SHELL:
                    return true;
            }
            return type.name().endsWith("_SHULKER_BOX");
        }
    }
}
