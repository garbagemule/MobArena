package com.garbagemule.MobArena;

import static org.bukkit.Material.*;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.framework.ArenaMaster;
import com.garbagemule.MobArena.things.ItemStackThing;
import com.garbagemule.MobArena.things.Thing;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.permissions.PermissionAttachment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.IntStream;

public class ArenaClass
{
    private String configName, lowercaseName;
    private ItemStackThing helmet, chestplate, leggings, boots, offhand;
    private List<Thing> items;
    private List<ItemStackThing> armor;
    private Map<String,Boolean> perms;
    private Map<String,Boolean> lobbyperms;
    private boolean unbreakableWeapons, unbreakableArmor;
    private Thing price;
    private Location classchest;

    /**
     * Create a new, empty arena class with the given name.
     * @param name the class name as it appears in the config-file
     */
    public ArenaClass(String name, Thing price, boolean unbreakableWeapons, boolean unbreakableArmor) {
        this.configName    = name;
        this.lowercaseName = name.toLowerCase().replace(" ", "");
        
        this.items = new ArrayList<>();
        this.armor = new ArrayList<>(4);
        this.perms = new HashMap<>();
        this.lobbyperms = new HashMap<>();

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
     * Get the Material type of the first item in the items list.
     * If the items list is empty, the method returns Material.STONE
     * @return the type of the first item, or STONE if the list is empty
     */
    public Material getLogo() {
        if (items.isEmpty()) {
            return Material.STONE;
        }
        Thing firstItem = items.get(0);
        if (firstItem instanceof ItemStackThing) {
            return ((ItemStackThing)firstItem).getItem().getType();
        }
        return Material.STONE;
    }
    
    /**
     * Set the helmet slot for the class.
     * @param helmet an item
     */
    public void setHelmet(ItemStackThing helmet) {
        this.helmet = helmet;
    }
    
    /**
     * Set the chestplate slot for the class.
     * @param chestplate an item
     */
    public void setChestplate(ItemStackThing chestplate) {
        this.chestplate = chestplate;
    }
    
    /**
     * Set the leggings slot for the class.
     * @param leggings an item
     */
    public void setLeggings(ItemStackThing leggings) {
        this.leggings = leggings;
    }
    
    /**
     * Set the boots slot for the class.
     * @param boots an item
     */
    public void setBoots(ItemStackThing boots) {
        this.boots = boots;
    }
    
    /**
     * Set the off-hand slot for the class.
     * @param offHand
     */
    public void setOffHand(ItemStackThing offHand) {
        this.offhand = offHand;
    }

    /**
     * Add an item to the items list.
     * @param thing a thing
     */
    public void addItem(Thing thing) {
        if (thing == null) return;
        items.add(thing);
    }
    
    /**
     * Replace the current items list with a new list of all the items in the given list.
     * This method uses the addItem() method for each item to ensure consistency.
     * @param things a list of things
     */
    public void setItems(List<Thing> things) {
        this.items = new ArrayList<>(things.size());
        for (Thing thing : things) {
            addItem(thing);
        }
    }
    
    /**
     * Replace the current armor list with the given list.
     * @param armor a list of items
     */
    public void setArmor(List<ItemStackThing> armor) {
        this.armor = armor;
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
        for (Thing thing : items) {
            thing.giveTo(p);
        }
        
        // Check for legacy armor-node items
        if (!armor.isEmpty()) {
            for (ItemStackThing armorThing : armor) {
                ItemStack piece = armorThing.getItem();
                ArmorType type = ArmorType.getType(piece);
                if (type == null) continue;
                
                switch (type) {
                    case HELMET:
                        inv.setHelmet(piece);
                        break;
                    case CHESTPLATE:
                        inv.setChestplate(piece);
                        break;
                    case LEGGINGS:
                        inv.setLeggings(piece);
                        break;
                    case BOOTS:
                        inv.setBoots(piece);
                        break;
                    default:
                        break;
                }
            }
        }
        
        // Check type specifics.
        if (helmet     != null) inv.setHelmet(helmet.getItem());
        if (chestplate != null) inv.setChestplate(chestplate.getItem());
        if (leggings   != null) inv.setLeggings(leggings.getItem());
        if (boots      != null) inv.setBoots(boots.getItem());
        if (offhand    != null) inv.setItemInOffHand(offhand.getItem());
    }
    
    /**
     * Add a permission value to the class.
     * @param perm the permission
     * @param value the value
     */
    public void addPermission(String perm, boolean value) {
        perms.put(perm, value);
    }
    
    /**
     * Get an unmodifiable map of permissions and values for the class.
     * @return a map of permissions and values
     */
    public Map<String,Boolean> getPermissions() {
        return Collections.unmodifiableMap(perms);
    }

    public void addLobbyPermission(String perm, boolean value) {
        lobbyperms.put(perm, value);
    }

    public Map<String,Boolean> getLobbyPermissions() {
        return Collections.unmodifiableMap(lobbyperms);
    }
    
    /**
     * Grant the given player all the permissions of the class.
     * All permissions will be attached to a PermissionAttachment object, which
     * will be returned to the caller.
     * @param plugin a MobArena instance
     * @param p a player
     * @return the PermissionAttachment with all the permissions
     */
    public PermissionAttachment grantPermissions(MobArena plugin, Player p) {
        if (perms.isEmpty()) return null;
        
        PermissionAttachment pa = p.addAttachment(plugin);
        grantPerms(pa, perms, p);
        return pa;
    }

    public PermissionAttachment grantLobbyPermissions(MobArena plugin, Player p) {
        if (lobbyperms.isEmpty()) return null;

        PermissionAttachment pa = p.addAttachment(plugin);
        grantPerms(pa, lobbyperms, p);
        return pa;
    }

    private void grantPerms(PermissionAttachment pa, Map<String,Boolean> map, Player p) {
        for (Entry<String,Boolean> entry : map.entrySet()) {
            try {
                pa.setPermission(entry.getKey(), entry.getValue());
            }
            catch (Exception e) {
                String perm   = entry.getKey() + ":" + entry.getValue();
                String player = p.getName();

                pa.getPlugin().getLogger().warning("[PERM00] Failed to attach permission '" + perm + "' to player '" + player + " with class " + this.configName
                                + "'.\nPlease verify that your class permissions are well-formed.");
            }
        }
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

    public void removeThings(Player p) {
        // We're going to let inventory restore take care of armor
        for (Thing thing : items) {
            thing.takeFrom(p);
        }
    }
    
    /**
     * Used by isWeapon() to determine if an ItemStack is a weapon type.
     */
    private static EnumSet<Material> weaponTypes = EnumSet.of(
        WOOD_SWORD,   GOLD_SWORD,   STONE_SWORD,   IRON_SWORD,   DIAMOND_SWORD,
        WOOD_AXE,     GOLD_AXE,     STONE_AXE,     IRON_AXE,     DIAMOND_AXE,
        WOOD_PICKAXE, GOLD_PICKAXE, STONE_PICKAXE, IRON_PICKAXE, DIAMOND_PICKAXE,
        WOOD_SPADE,   GOLD_SPADE,   STONE_SPADE,   IRON_SPADE,   DIAMOND_SPADE,
        WOOD_HOE,     GOLD_HOE,     STONE_HOE,     IRON_HOE,     DIAMOND_HOE,
        BOW, FISHING_ROD, FLINT_AND_STEEL, SHEARS, CARROT_STICK, SHIELD
    );

    /**
     * Returns true, if the ItemStack appears to be a weapon, in which case
     * the addItem() method will set the weapon durability to the absolute
     * maximum, as to give them "infinite" durability.
     * @param stack an ItemStack
     * @return true, if the item is a weapon
     */
    public static boolean isWeapon(ItemStack stack) {
        if (stack == null) return false;
        return weaponTypes.contains(stack.getType());
    }

    /**
     * Used by the grantItems() method to determine the armor type of a given
     * ItemStack. Armor pieces are auto-equipped.
     * Note: This enum is only necessary for backward-compatibility with the
     * 'armor'-node.
     */
    public enum ArmorType {
        HELMET,
        CHESTPLATE,
        LEGGINGS,
        BOOTS;

        public static ArmorType getType(ItemStack stack) {
            switch (stack.getType()) {
                case LEATHER_HELMET:
                case CHAINMAIL_HELMET:
                case IRON_HELMET:
                case DIAMOND_HELMET:
                case GOLD_HELMET:
                    return HELMET;

                case LEATHER_CHESTPLATE:
                case CHAINMAIL_CHESTPLATE:
                case IRON_CHESTPLATE:
                case DIAMOND_CHESTPLATE:
                case GOLD_CHESTPLATE:
                    return CHESTPLATE;

                case LEATHER_LEGGINGS:
                case CHAINMAIL_LEGGINGS:
                case IRON_LEGGINGS:
                case DIAMOND_LEGGINGS:
                case GOLD_LEGGINGS:
                    return LEGGINGS;

                case LEATHER_BOOTS:
                case CHAINMAIL_BOOTS:
                case IRON_BOOTS:
                case DIAMOND_BOOTS:
                case GOLD_BOOTS:
                    return BOOTS;

                default:
                    return null;
            }
        }
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
                    arena.getInventoryManager().restoreInv(p);
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
