package com.garbagemule.MobArena.things;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.function.Supplier;

abstract class InventoryThing implements Thing {

    private final Supplier<Location> location;

    protected InventoryThing(Supplier<Location> location) {
        this.location = location;
    }

    @Override
    public boolean giveTo(Player player) {
        Thing thing = load();
        if (thing == null) {
            return false;
        }
        return thing.giveTo(player);
    }

    @Override
    public boolean takeFrom(Player player) {
        Thing thing = load();
        if (thing == null) {
            return false;
        }
        return thing.takeFrom(player);
    }

    @Override
    public boolean heldBy(Player player) {
        Thing thing = load();
        if (thing == null) {
            return false;
        }
        return thing.heldBy(player);
    }

    abstract Thing load();

    Inventory getInventory() {
        Location location = this.location.get();
        if (location != null) {
            Block block = location.getBlock();
            BlockState state = block.getState();
            if (state instanceof InventoryHolder) {
                InventoryHolder holder = (InventoryHolder) state;
                return holder.getInventory();
            }
        }
        return null;
    }

    @Override
    public String toString() {
        Thing thing = load();
        return (thing != null) ? thing.toString() : "nothing";
    }

}
