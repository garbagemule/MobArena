package com.garbagemule.MobArena.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemParser
{
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
        if (type == Material.POTION || type == Material.LINGERING_POTION || type == Material.SPLASH_POTION|| type == Material.TIPPED_ARROW) {
            return withPotionMeta(stack, data);
        }

        return stack;
    }

    private static ItemStack withPotionMeta(ItemStack stack, String data) {
        PotionType type;
        boolean extended = false;
        boolean upgraded = false;

        if (data.startsWith("long_")) {
            extended = true;
            data = data.substring(5);
        }
        if (data.startsWith("strong_")) {
            upgraded = true;
            data = data.substring(7);
        }
        try {
            type = PotionType.valueOf(data.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }

        PotionMeta meta = (PotionMeta) stack.getItemMeta();
        meta.setBasePotionData(new PotionData(type, extended, upgraded));
        stack.setItemMeta(meta);
        return stack;
    }

    private static Optional<Material> getType(String item) {
        return Optional.ofNullable(Material.getMaterial(item.toUpperCase()));
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
        if (parts.length != 2) {
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
        return Enchantment.getByKey(NamespacedKey.minecraft(ench.toLowerCase()));
    }
}
