package com.garbagemule.MobArena.things;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HelmetThing extends ItemStackThing {
    public HelmetThing(ItemStack stack) {
        super(stack);
    }

    @Override
    public boolean giveTo(Player player) {
        player.getInventory().setHelmet(super.getItemStack());
        return true;
    }

    @Override
    public boolean takeFrom(Player player) {
        player.getInventory().setHelmet(null);
        return true;
    }

    @Override
    public boolean heldBy(Player player) {
        return super.getItemStack().equals(player.getInventory().getHelmet());
    }
}
