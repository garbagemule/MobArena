package com.garbagemule.MobArena.things;

import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.function.Supplier;

class InventoryIndexThing extends InventoryThing {

    private final int index;

    InventoryIndexThing(
        Supplier<Location> location,
        int index
    ) {
        super(location);
        this.index = index;
    }

    @Override
    Thing load() {
        Inventory inventory = super.getInventory();
        if (inventory == null) {
            return null;
        }
        if (inventory.getSize() <= index) {
            return null;
        }

        ItemStack stack = inventory.getItem(index);
        if (stack == null) {
            return null;
        }

        ItemStack clone = stack.clone();
        return new ItemStackThing(clone);
    }

}
