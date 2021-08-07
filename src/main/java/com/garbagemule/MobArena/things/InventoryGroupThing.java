package com.garbagemule.MobArena.things;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

class InventoryGroupThing extends InventoryThing {

    InventoryGroupThing(Supplier<Location> location) {
        super(location);
    }

    @Override
    Thing load() {
        Inventory inventory = super.getInventory();
        if (inventory == null) {
            return null;
        }

        List<Thing> things = new ArrayList<>();
        for (ItemStack stack : inventory) {
            if (stack != null && stack.getType() != Material.AIR) {
                things.add(new ItemStackThing(stack));
            }
        }

        if (things.isEmpty()) {
            return null;
        }
        if (things.size() == 1) {
            return things.get(0);
        }

        return new ThingGroup(things);
    }

}
