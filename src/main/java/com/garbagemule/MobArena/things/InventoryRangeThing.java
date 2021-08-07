package com.garbagemule.MobArena.things;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

class InventoryRangeThing extends InventoryThing {

    private final int first;
    private final int last;

    InventoryRangeThing(
        Supplier<Location> location,
        int first,
        int last
    ) {
        super(location);
        this.first = first;
        this.last = last;
    }

    @Override
    Thing load() {
        Inventory inventory = super.getInventory();
        if (inventory == null) {
            return null;
        }
        if (inventory.getSize() <= last) {
            return null;
        }

        List<Thing> things = new ArrayList<>();
        ItemStack[] content = inventory.getContents();
        for (int i = first; i <= last; i++) {
            ItemStack stack = content[i];
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
