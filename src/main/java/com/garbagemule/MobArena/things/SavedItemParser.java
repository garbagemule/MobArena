package com.garbagemule.MobArena.things;

import com.garbagemule.MobArena.MobArena;
import com.garbagemule.MobArena.items.SavedItemsManager;
import org.bukkit.inventory.ItemStack;

class SavedItemParser implements ItemStackParser {

    private final MobArena plugin;

    SavedItemParser(MobArena plugin) {
        this.plugin = plugin;
    }

    @Override
    public ItemStack parse(String s) {
        // Note that the saved items manager is not available during
        // the load procedure, so we need to defer the call to the
        // getter until we actually need it. We don't really have to
        // check for null here, but without it, we would need to set
        // up a dummy manager for the ThingManager test suite, which
        // is a bit of a hassle.
        SavedItemsManager items = plugin.getSavedItemsManager();
        if (items == null) {
            return null;
        }

        if (s.contains(":")) {
            return parseWithAmount(s, items);
        } else {
            return parseSimple(s, items);
        }
    }

    private ItemStack parseWithAmount(String s, SavedItemsManager items) {
        String[] parts = s.split(":");
        if (parts.length > 2) {
            return null;
        }

        ItemStack stack = parseSimple(parts[0], items);
        if (stack == null) {
            return null;
        }

        try {
            int amount = Integer.parseInt(parts[1]);
            stack.setAmount(amount);
        } catch (NumberFormatException e) {
            return null;
        }

        return stack;
    }

    private ItemStack parseSimple(String s, SavedItemsManager items) {
        return items.getItem(s);
    }

}
