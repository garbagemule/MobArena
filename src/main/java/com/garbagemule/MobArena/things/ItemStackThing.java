package com.garbagemule.MobArena.things;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
        String item = stack.getType()
            .name()
            .replace("_", " ")
            .toLowerCase();

        if (stack.getAmount() > 1) {
            return stack.getAmount() + "x " + item;
        }
        return item;
    }

    public ItemStack getItem() {
        return stack;
    }
}
