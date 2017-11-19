package com.garbagemule.MobArena.util;

import org.bukkit.inventory.ItemStack;

/**
 * Integrating plugins may implement this interface and register it with
 * ItemParser.registerItemProvider to provide custom items.
 */
public interface ItemProvider {

    /**
     * Create an item based on a custom item key.
     *
     * @param itemKey The full key string for the item as written in configuration.
     * @return A new item, or null if this is not a type of item handled by this provider.
     */
    ItemStack getItem(String itemKey);
}
