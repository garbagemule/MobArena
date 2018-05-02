package com.garbagemule.MobArena.things;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BootsThing extends ItemStackThing {
    public BootsThing(ItemStack stack) {
        super(stack);
    }

    @Override
    public boolean giveTo(Player player) {
        player.getInventory().setBoots(super.getItemStack());
        return true;
    }

    @Override
    public boolean takeFrom(Player player) {
        player.getInventory().setBoots(null);
        return true;
    }

    @Override
    public boolean heldBy(Player player) {
        return super.getItemStack().equals(player.getInventory().getBoots());
    }
}
