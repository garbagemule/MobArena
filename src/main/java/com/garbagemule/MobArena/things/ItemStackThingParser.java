package com.garbagemule.MobArena.things;

import com.garbagemule.MobArena.util.ItemParser;
import org.bukkit.inventory.ItemStack;

class ItemStackThingParser implements ThingParser {
    @Override
    public ItemStackThing parse(String s) {
        ItemStack stack = ItemParser.parseItem(s, false);
        if (stack == null) {
            return null;
        }
        return new ItemStackThing(stack);
    }
}
