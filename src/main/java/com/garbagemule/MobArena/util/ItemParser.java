package com.garbagemule.MobArena.util;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemParser
{
    private static final int WOOL_ID = Material.WOOL.getId();
    private static final int DYE_ID  = Material.INK_SACK.getId();

    private static final Map<Short, PotionType> POTION_TYPE_MAP = new HashMap<>();
    static {
        POTION_TYPE_MAP.put((short) 8193, PotionType.REGEN);
        POTION_TYPE_MAP.put((short) 8194, PotionType.SPEED);
        POTION_TYPE_MAP.put((short) 8195, PotionType.FIRE_RESISTANCE);
        POTION_TYPE_MAP.put((short) 8196, PotionType.POISON);
        POTION_TYPE_MAP.put((short) 8197, PotionType.INSTANT_HEAL);
        POTION_TYPE_MAP.put((short) 8198, PotionType.NIGHT_VISION);
        POTION_TYPE_MAP.put((short) 8200, PotionType.WEAKNESS);
        POTION_TYPE_MAP.put((short) 8201, PotionType.STRENGTH);
        POTION_TYPE_MAP.put((short) 8202, PotionType.SLOWNESS);
        POTION_TYPE_MAP.put((short) 8203, PotionType.JUMP);
        POTION_TYPE_MAP.put((short) 8204, PotionType.INSTANT_DAMAGE);
        POTION_TYPE_MAP.put((short) 8205, PotionType.WATER_BREATHING);
        POTION_TYPE_MAP.put((short) 8206, PotionType.INVISIBILITY);
    }

    public static List<ItemStack> parseItems(String s) {
        if (s == null) {
            return new ArrayList<>(1);
        }
        
        String[] items = s.split(",");
        List<ItemStack> result = new ArrayList<>(items.length);
        
        for (String item : items) {
            ItemStack stack = parseItem(item.trim());
            if (stack != null) {
                result.add(stack);
            }
        }
        
        return result;
    }
    
    public static ItemStack parseItem(String item) {
        return parseItem(item, true);
    }

    public static ItemStack parseItem(String item, boolean logFailure) {
        if (item == null || item.equals(""))
            return null;
        
        // Check if the item has enchantments.
        String[] space = item.split(" ");
        String[] parts = (space.length == 2 ? space[0].split(":") : item.split(":"));
        
        ItemStack result = null;
        
        switch (parts.length) {
            case 1:
                result = singleItem(parts[0]);
                break;
            case 2:
                result = withAmount(parts[0], parts[1]);
                break;
            case 3:
                result = withDataAndAmount(parts[0], parts[1], parts[2]);
                break;
        }
        if (result == null || result.getTypeId() == 0) {
            if (logFailure) {
                Bukkit.getLogger().warning("[MobArena] Failed to parse item: " + item);
            }
            return null;
        }

        if (space.length == 2) {
            addEnchantments(result, space[1]);
        }

        return result;
    }
    
    private static ItemStack singleItem(String item) {
        int id = getTypeId(item);
        return new ItemStack(id);
    }
    
    private static ItemStack withAmount(String item, String amount) {
        int id = getTypeId(item);
        int a  = getAmount(amount);
        return new ItemStack(id,a);
    }
    
    private static ItemStack withDataAndAmount(String item, String data, String amount) {
        int   id = getTypeId(item);
        short d  = getData(data, id);
        int   a  = getAmount(amount);

        if (id == Material.LINGERING_POTION.getId() || id == Material.TIPPED_ARROW.getId() || id == Material.SPLASH_POTION.getId()) {
            return withPotionMeta(id, d, a);
        }
        return new ItemStack(id,a,d);
    }
    
    private static ItemStack withPotionMeta(int id, short d, int a) {
        ItemStack result = new ItemStack(id, a);
        PotionMeta meta = (PotionMeta) result.getItemMeta();

        boolean extended = (d & 64) > 0;
        boolean upgraded = (d & 32) > 0;
        if (extended) {
            d -= 64;
        }
        if (upgraded) {
            d -= 32;
        }

        PotionType type = POTION_TYPE_MAP.get(d);
        if (type != null) {
            PotionData pData = new PotionData(type, extended, upgraded);
            meta.setBasePotionData(pData);
            result.setItemMeta(meta);
            return result;
        }
        return null;
    }

    private static int getTypeId(String item) {
        if (item.matches("(-)?[0-9]*")) {
            return Integer.parseInt(item);
        }
        Material m = Enums.getEnumFromString(Material.class, item);
        return (m != null ? m.getId() : 0);
    }
    
    private static short getData(String data, int id) {
        // Wool and ink are special
        if (id == WOOL_ID) {
            DyeColor dye = Enums.getEnumFromString(DyeColor.class, data);
            if (dye == null) dye = DyeColor.getByWoolData(Byte.parseByte(data));
            return dye.getWoolData();
        } else if (id == DYE_ID) {
            DyeColor dye = Enums.getEnumFromString(DyeColor.class, data);
            if (dye == null) dye = DyeColor.getByDyeData(Byte.parseByte(data));
            return dye.getDyeData();
        }
        return (data.matches("(-)?[0-9]+") ? Short.parseShort(data) : 0);
    }
    
    private static int getAmount(String amount) {
        if (amount.matches("(-)?[1-9][0-9]*")) {
            return Integer.parseInt(amount);
        }
        
        return 1;
    }
    
    private static void addEnchantments(ItemStack stack, String list) {
        String[] parts = list.split(";");
        
        for (String ench : parts) {
            addEnchantment(stack, ench.trim());
        }
    }
    
    private static void addEnchantment(ItemStack stack, String ench) {
        String[] parts = ench.split(":");
        if (parts.length != 2 || !(parts[0].matches("[0-9]*") && parts[1].matches("[0-9]*"))) {
            return;
        }
        
        int id  = Integer.parseInt(parts[0]);
        int lvl = Integer.parseInt(parts[1]);
        
        Enchantment e = Enchantment.getById(id);
        if (e == null) {// || !e.canEnchantItem(stack) || lvl > e.getMaxLevel() || lvl < e.getStartLevel()) {
            return;
        }
        
        if (stack.getType() == Material.ENCHANTED_BOOK) {
            EnchantmentStorageMeta esm = (EnchantmentStorageMeta) stack.getItemMeta();
            esm.addStoredEnchant(e, lvl, true);
            stack.setItemMeta(esm);
        } else {
            stack.addUnsafeEnchantment(e, lvl);
        }
    }
}
