package com.garbagemule.MobArena.things;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.EnumSet;

class Equippable {
    @FunctionalInterface
    interface Wrapper {
        ItemStackThing wrap(ItemStack stack);
    }

    private static EnumSet<Material> helmets = EnumSet.of(
        Material.LEATHER_HELMET,
        Material.IRON_HELMET,
        Material.CHAINMAIL_HELMET,
        Material.GOLD_HELMET,
        Material.DIAMOND_HELMET
    );
    private static EnumSet<Material> chestplates = EnumSet.of(
        Material.LEATHER_CHESTPLATE,
        Material.IRON_CHESTPLATE,
        Material.CHAINMAIL_CHESTPLATE,
        Material.GOLD_CHESTPLATE,
        Material.DIAMOND_CHESTPLATE
    );
    private static EnumSet<Material> leggings = EnumSet.of(
        Material.LEATHER_LEGGINGS,
        Material.IRON_LEGGINGS,
        Material.CHAINMAIL_LEGGINGS,
        Material.GOLD_LEGGINGS,
        Material.DIAMOND_LEGGINGS
    );
    private static EnumSet<Material> boots = EnumSet.of(
        Material.LEATHER_BOOTS,
        Material.IRON_BOOTS,
        Material.CHAINMAIL_BOOTS,
        Material.GOLD_BOOTS,
        Material.DIAMOND_BOOTS
    );

    static Wrapper getWrapperByPrefix(String prefix) {
        if (prefix.equals("helmet")) {
            return HelmetThing::new;
        }
        if (prefix.equals("chestplate")) {
            return ChestplateThing::new;
        }
        if (prefix.equals("leggings")) {
            return LeggingsThing::new;
        }
        if (prefix.equals("boots")) {
            return BootsThing::new;
        }
        if (prefix.equals("offhand")) {
            return OffHandThing::new;
        }
        return null;
    }

    static Wrapper guessWrapperFromItemStack(ItemStack stack) {
        Material type = stack.getType();
        if (helmets.contains(type)) {
            return HelmetThing::new;
        }
        if (chestplates.contains(type)) {
            return ChestplateThing::new;
        }
        if (leggings.contains(type)) {
            return LeggingsThing::new;
        }
        if (boots.contains(type)) {
            return BootsThing::new;
        }
        return null;
    }
}
