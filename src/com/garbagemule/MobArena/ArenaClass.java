package com.garbagemule.MobArena;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.permissions.PermissionAttachment;

public class ArenaClass
{
    private String configName, lowercaseName;
    private ItemStack helmet, chestplate, leggings, boots;
    private List<ItemStack> items, armor;
    private Map<String,Boolean> perms;
    private int pets;
    private boolean unbreakableWeapons;
    
    /**
     * Create a new, empty arena class with the given name.
     * @param name the class name as it appears in the config-file
     */
    public ArenaClass(String name, boolean unbreakableWeapons) {
        this.configName    = name;
        this.lowercaseName = name.toLowerCase();
        
        this.items = new ArrayList<ItemStack>();
        this.armor = new ArrayList<ItemStack>(4);
        this.perms = new HashMap<String,Boolean>();
        this.pets  = 0;
        
        this.unbreakableWeapons = unbreakableWeapons;
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
        return items.get(0).getType();
    }
    
    /**
     * Set the helmet slot for the class.
     * @param helmet an item
     */
    public void setHelmet(ItemStack helmet) {
        this.helmet = helmet;
    }
    
    /**
     * Set the chestplate slot for the class.
     * @param chestplate an item
     */
    public void setChestplate(ItemStack chestplate) {
        this.chestplate = chestplate;
    }
    
    /**
     * Set the leggings slot for the class.
     * @param leggings an item
     */
    public void setLeggings(ItemStack leggings) {
        this.leggings = leggings;
    }
    
    /**
     * Set the boots slot for the class.
     * @param boots an item
     */
    public void setBoots(ItemStack boots) {
        this.boots = boots;
    }
    
    /**
     * Add an item to the items list.
     * If the item is a weapon-type, its durability will be set to "infinite".
     * If the item is a bone, the pets counter will be incremented.
     * @param stack an item
     */
    public void addItem(ItemStack stack) {
        if (stack == null) return;
        
        if (unbreakableWeapons && isWeapon(stack)) {
            stack.setDurability(Short.MIN_VALUE);
        }
        else if (stack.getType() == Material.BONE) {
            pets += stack.getAmount();
        }
        else if (stack.getAmount() > 64) {
            while (stack.getAmount() > 64) {
                items.add(new ItemStack(stack.getType(), 64));
                stack.setAmount(stack.getAmount() - 64);
            }
        }
        
        items.add(stack);
    }
    
    /**
     * Replace the current items list with a new list of all the items in the given list.
     * This method uses the addItem() method for each item to ensure consistency.
     * @param stacks a list of items
     */
    public void setItems(List<ItemStack> stacks) {
        this.items = new ArrayList<ItemStack>(stacks.size());
        for (ItemStack stack : stacks) {
            addItem(stack);
        }
    }
    
    /**
     * Replace the current armor list with the given list.
     * @param armor a list of items
     */
    public void setArmor(List<ItemStack> armor) {
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
        for (ItemStack stack : items) {
            inv.addItem(stack);
        }
        
        // Check for legacy armor-node items
        if (!armor.isEmpty()) {
            for (ItemStack piece : armor) {
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
        if (helmet     != null) inv.setHelmet(helmet);
        if (chestplate != null) inv.setChestplate(chestplate);
        if (leggings   != null) inv.setLeggings(leggings);
        if (boots      != null) inv.setBoots(boots);
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
        
        for (Entry<String,Boolean> entry : perms.entrySet()) {
            try {
                pa.setPermission(entry.getKey(), entry.getValue());
            }
            catch (Exception e) {
                String perm   = entry.getKey() + ":" + entry.getValue();
                String player = p.getName();
                
                Messenger.warning("[PERM00] Failed to attach permission '" + perm + "' to player '" + player + " with class " + this.configName
                                + "'.\nPlease verify that your class permissions are well-formed.");
            }
        }
        return pa;
    }
    
    /**
     * Get the amount of pets this class is given upon starting the arena.
     * @return the number of pets this class has
     */
    public int getPetAmount() {
        return pets;
    }
    
    /**
     * Used by isWeapon() to determine if an ItemStack is a weapon type.
     */
    private static int[] weaponTypes = new int[]{256,257,258,261,267,268,269,270,271,272,273,274,275,276,277,278,279,283,284,285,286,290,291,292,293,294};
    
    /**
     * Returns true, if the ItemStack appears to be a weapon, in which case
     * the addItem() method will set the weapon durability to the absolute
     * maximum, as to give them "infinite" durability.
     * @param stack an ItemStack
     * @return true, if the item is a weapon
     */
    private boolean isWeapon(ItemStack stack) {
        int id = stack.getTypeId();
        
        for (int type : weaponTypes) {
            if (id == type) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Used by the grantItems() method to determine the armor type of a given
     * ItemStack. Armor pieces are auto-equipped.
     * Note: This enum is only necessary for backward-compatibility with the
     * 'armor'-node.
     */
    public enum ArmorType {
        HELMET     (298,302,306,310,314),
        CHESTPLATE (299,303,307,311,315),
        LEGGINGS   (300,304,308,312,316),
        BOOTS      (301,305,309,313,317);
        
        private int[] types;
        
        private ArmorType(int... types) {
            this.types = types;
        }
        
        public static ArmorType getType(ItemStack stack) {
            int id = stack.getTypeId();
            
            for (ArmorType armorType : ArmorType.values()) {
                for (int type : armorType.types) {
                    if (id == type) {
                        return armorType;
                    }
                }
            }
            return null;
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
}
