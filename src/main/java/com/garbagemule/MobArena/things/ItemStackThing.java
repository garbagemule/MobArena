package com.garbagemule.MobArena.things;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemStackThing implements Thing {
    private ItemStack stack;

    public ItemStackThing(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public boolean giveTo(Player player) {
        return player.getInventory().addItem(stack).isEmpty();
    }

    @Override
    public boolean takeFrom(Player player) {
        return player.getInventory().removeItem(stack).isEmpty();
    }

    @Override
    public boolean heldBy(Player player) {
        return player.getInventory().containsAtLeast(stack, stack.getAmount());
    }

    @Override
    public String toString() {
        String name = getName();
        int amount = stack.getAmount();
        if (amount > 1) {
            return amount + "x " + name;
        }
        return name;
    }

    private String getName() {
        ItemMeta meta = stack.getItemMeta();
        if (meta.hasDisplayName()) {
            return meta.getDisplayName();
        }
        return stack.getType()
            .name()
            .replace("_", " ")
            .toLowerCase();
    }
}
