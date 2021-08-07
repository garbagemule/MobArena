package com.garbagemule.MobArena.things;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class OffHandThing extends ItemStackThing {

    public OffHandThing(ItemStack stack) {
        super(stack);
    }

    @Override
    public boolean giveTo(Player player) {
        player.getInventory().setItemInOffHand(super.getItemStack());
        return true;
    }

    @Override
    public boolean takeFrom(Player player) {
        player.getInventory().setItemInOffHand(null);
        return true;
    }

    @Override
    public boolean heldBy(Player player) {
        return super.getItemStack().equals(player.getInventory().getItemInOffHand());
    }

}
