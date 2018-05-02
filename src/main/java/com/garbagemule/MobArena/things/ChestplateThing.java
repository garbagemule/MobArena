package com.garbagemule.MobArena.things;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ChestplateThing extends ItemStackThing {
    public ChestplateThing(ItemStack stack) {
        super(stack);
    }

    @Override
    public boolean giveTo(Player player) {
        player.getInventory().setChestplate(super.getItemStack());
        return true;
    }

    @Override
    public boolean takeFrom(Player player) {
        player.getInventory().setChestplate(null);
        return true;
    }

    @Override
    public boolean heldBy(Player player) {
        return super.getItemStack().equals(player.getInventory().getChestplate());
    }
}
