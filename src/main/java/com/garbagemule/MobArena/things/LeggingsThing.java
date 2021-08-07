package com.garbagemule.MobArena.things;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class LeggingsThing extends ItemStackThing {

    public LeggingsThing(ItemStack stack) {
        super(stack);
    }

    @Override
    public boolean giveTo(Player player) {
        player.getInventory().setLeggings(super.getItemStack());
        return true;
    }

    @Override
    public boolean takeFrom(Player player) {
        player.getInventory().setLeggings(null);
        return true;
    }

    @Override
    public boolean heldBy(Player player) {
        return super.getItemStack().equals(player.getInventory().getLeggings());
    }

}
