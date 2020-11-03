package com.garbagemule.MobArena.things;

import org.bukkit.inventory.ItemStack;

class Equippable {
    @FunctionalInterface
    interface Wrapper {
        ItemStackThing wrap(ItemStack stack);
    }

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
        return null;
    }

    static Wrapper guessWrapperFromItemStack(ItemStack stack) {
        String name = stack.getType().name();
        String[] parts = name.split("_");
        String suffix = parts[parts.length - 1];
        if (suffix.equals("HELMET")) {
            return HelmetThing::new;
        }
        if (suffix.equals("CHESTPLATE") || name.equals("ELYTRA")) {
            return ChestplateThing::new;
        }
        if (suffix.equals("LEGGINGS")) {
            return LeggingsThing::new;
        }
        if (suffix.equals("BOOTS")) {
            return BootsThing::new;
        }
        return null;
    }
}
