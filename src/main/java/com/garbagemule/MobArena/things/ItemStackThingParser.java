package com.garbagemule.MobArena.things;

import com.garbagemule.MobArena.things.Equippable.Wrapper;
import com.garbagemule.MobArena.util.ItemParser;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class ItemStackThingParser implements ThingParser {
    private List<ItemStackParser> parsers;

    ItemStackThingParser() {
        this.parsers = new ArrayList<>();
    }

    public void register(ItemStackParser parser) {
        parsers.add(parser);
    }

    @Override
    public ItemStackThing parse(String s) {
        String[] parts = s.split(":", 2);
        if (parts.length == 1) {
            return genericItem(s);
        }

        String prefix = parts[0];
        String rest = parts[1];

        if (prefix.equals("armor")) {
            return genericArmor(rest);
        }

        return specificSlot(s, prefix, rest);
    }

    private ItemStackThing genericItem(String s) {
        ItemStack stack = parseItemStack(s);
        if (stack == null) {
            return null;
        }
        return new ItemStackThing(stack);
    }

    private ItemStackThing genericArmor(String s) {
        ItemStack stack = parseItemStack(s);
        if (stack == null) {
            return null;
        }
        Wrapper wrapper = Equippable.guessWrapperFromItemStack(stack);
        if (wrapper == null) {
            return new ItemStackThing(stack);
        }
        return wrapper.wrap(stack);
    }

    private ItemStackThing specificSlot(String s, String prefix, String rest) {
        Wrapper wrapper = Equippable.getWrapperByPrefix(prefix);
        if (wrapper == null) {
            return genericItem(s);
        }
        ItemStack stack = parseItemStack(rest);
        if (stack == null) {
            return null;
        }
        return wrapper.wrap(stack);
    }

    private ItemStack parseItemStack(String s) {
        return parsers.stream()
            .map(p -> p.parse(s))
            .filter(Objects::nonNull)
            .findFirst()
            .orElseGet(() -> ItemParser.parseItem(s, false));
    }
}
