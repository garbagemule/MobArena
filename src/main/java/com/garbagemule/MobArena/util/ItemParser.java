package com.garbagemule.MobArena.util;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Dye;
import org.bukkit.material.Wool;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ItemParser
{
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
        if (result == null || result.getType() == Material.AIR) {
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
        return getType(item)
            .map(ItemStack::new)
            .orElse(null);
    }

    private static ItemStack withAmount(String item, String amount) {
        return getType(item)
            .map(type -> new ItemStack(type, getAmount(amount)))
            .orElse(null);
    }

    private static ItemStack withDataAndAmount(String item, String data, String amount) {
        ItemStack stack = withAmount(item, amount);
        if (stack == null) {
            return null;
        }

        Material type = stack.getType();
        if (type == Material.POTION) {
            boolean splash = item.equals("splash_potion");
            return withPotionMeta(stack, data, splash);
        }

        MaterialData md = getData(data, type);
        if (md == null) {
            return null;
        }

        return md.toItemStack(stack.getAmount());
    }

    private static ItemStack withPotionMeta(ItemStack stack, String data, boolean splash) {
        PotionType type = getPotionType(data);
        if (type == null) {
            return null;
        }
        Potion potion = new Potion(type, 1);
        potion.setSplash(splash);
        potion.apply(stack);
        return stack;
    }

    private static PotionType getPotionType(String data) {
        if (data.matches("[0-9]+")) {
            short d = Short.parseShort(data);

            // Compensate for splash potions
            if (d > (2 * 8192)) {
                d -= 8192;
            }

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
                warn(type.name(), data);
            }
            return type;
        }
        try {
            return PotionType.valueOf(data.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static Optional<Material> getType(String item) {
        if (item.matches("(-)?[1-9][0-9]*")) {
            Material type = Material.getMaterial(Integer.parseInt(item));
            if (type == null) {
                return Optional.empty();
            }

            warn(type.name(), item);

            return Optional.of(type);
        }
        if (item.equals("splash_potion")) {
            return Optional.of(Material.POTION);
        }
        return Optional.ofNullable(Material.getMaterial(item.toUpperCase()));
    }

    private static MaterialData getData(String data, Material type) {
        if (type == Material.INK_SACK) {
            return getDyeData(data);
        }
        if (type == Material.WOOL) {
            return getWoolData(data);
        }
        return null;
    }

    private static Wool getWoolData(String data) {
        if (data.matches("(-)?[1-9][0-9]*")) {
            DyeColor color = DyeColor.getByWoolData(Byte.parseByte(data));
            if (color == null) {
                return null;
            }

            warn(color.name(), data);

            return new Wool(color);
        }
        try {
            return new Wool(DyeColor.valueOf(data.toUpperCase()));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static Dye getDyeData(String data) {
        if (data.matches("(-)?[1-9][0-9]*")) {
            DyeColor color = DyeColor.getByDyeData(Byte.parseByte(data));
            if (color == null) {
                return null;
            }

            warn(color.name(), data);

            Dye dye = new Dye();
            dye.setColor(color);
            return dye;
        }
        try {
            Dye dye = new Dye();
            dye.setColor(DyeColor.valueOf(data.toUpperCase()));
            return dye;
        } catch (IllegalArgumentException e) {
            return null;
        }
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
        if (parts.length != 2 || !parts[1].matches("[0-9]*")) {
            return;
        }

        Enchantment enchantment = getEnchantment(parts[0]);
        if (enchantment == null) {
            return;
        }
        int lvl = Integer.parseInt(parts[1]);

        if (stack.getType() == Material.ENCHANTED_BOOK) {
            EnchantmentStorageMeta esm = (EnchantmentStorageMeta) stack.getItemMeta();
            esm.addStoredEnchant(enchantment, lvl, true);
            stack.setItemMeta(esm);
        } else {
            stack.addUnsafeEnchantment(enchantment, lvl);
        }
    }

    private static Enchantment getEnchantment(String ench) {
        if (ench.matches("[1-9][0-9]*")) {
            Enchantment enchantment = Enchantment.getById(Integer.parseInt(ench));
            if (enchantment == null) {
                return null;
            }

            warn(enchantment.getName(), ench);

            return enchantment;
        }
        return Enchantment.getByName(ench.toUpperCase());
    }

    private static void warn(String name, String value) {
        String msg = String.format(
            "Consider using '%s' instead of '%s'",
            name.toLowerCase(),
            value
        );
        Bukkit.getLogger().warning("[MobArena] " + msg);
    }
}
