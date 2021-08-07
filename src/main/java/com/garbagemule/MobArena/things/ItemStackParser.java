package com.garbagemule.MobArena.things;

import org.bukkit.inventory.ItemStack;

/**
 * An ItemStack parser takes a string as input and returns either an instance
 * of {@link ItemStack} or null.
 */
@FunctionalInterface
public interface ItemStackParser {

    /**
     * Parse the given string, returning an {@link ItemStack} instance on
     * success, otherwise null.
     *
     * @param s a string to parse
     * @return an instance of {@link ItemStack}, or null
     */
    ItemStack parse(String s);

}
