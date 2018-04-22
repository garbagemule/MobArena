package com.garbagemule.MobArena.things;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

public class ItemStackThingParserTest {

    private ItemStackThingParser subject;

    @Before
    public void setup() {
        subject = new ItemStackThingParser();
    }

    @Test
    public void emptyStringReturnsNull() {
        ItemStackThing result = subject.parse("");

        assertThat(result, is(nullValue()));
    }

    @Test
    public void gibberishReturnsNull() {
        ItemStackThing result = subject.parse("I can't believe you've done this");

        assertThat(result, is(nullValue()));
    }

    @Test
    public void fiveDirt() {
        ItemStackThing result = subject.parse("dirt:5");

        // ItemStack's equals() method does naughty things, so verify manually
        ItemStack stack = result.getItemStack();
        assertThat(stack.getType(), equalTo(Material.DIRT));
        assertThat(stack.getAmount(), equalTo(5));
    }

    @Test
    public void bareLederhosenReturnsGenericItemStackThing() {
        ItemStackThing result = subject.parse("leather_leggings");

        assertThat(result.getClass(), equalTo(ItemStackThing.class));
    }

    @Test
    public void prefixedIronChestplateReturnsLeggingsThing() {
        ItemStackThing result = subject.parse("leggings:leather_leggings");

        assertThat(result.getClass(), equalTo(LeggingsThing.class));
    }

    @Test
    public void genericArmorPrefixGuessesChestplateThing() {
        ItemStackThing result = subject.parse("armor:iron_chestplate");

        assertThat(result.getClass(), equalTo(ChestplateThing.class));
    }

    @Test
    public void returnsNonNullCustomParserResult() {
        String input = "something:soft";
        ItemStack stack = new ItemStack(Material.SPONGE, 3);
        ItemStackParser parser = mock(ItemStackParser.class);
        when(parser.parse(input)).thenReturn(stack);
        subject.register(parser);

        ItemStackThing result = subject.parse(input);

        assertThat(result.getItemStack(), equalTo(stack));
    }

    @Test
    public void customParsersInvokedInOrder() {
        String input = "unicorns and rainbows";
        ItemStackParser first = mock(ItemStackParser.class);
        ItemStackParser second = mock(ItemStackParser.class);
        subject.register(first);
        subject.register(second);

        subject.parse(input);

        InOrder order = inOrder(first, second);
        order.verify(first).parse(input);
        order.verify(second).parse(input);
    }

    @Test
    public void customParsersInvokedUntilNonNullResult() {
        String input = "unicorns and rainbows";
        ItemStackParser first = mock(ItemStackParser.class);
        ItemStackParser second = mock(ItemStackParser.class);
        ItemStackParser third = mock(ItemStackParser.class);
        ItemStack stack = new ItemStack(Material.GLOWSTONE, 9);
        when(second.parse(input)).thenReturn(stack);
        subject.register(first);
        subject.register(second);
        subject.register(third);

        subject.parse(input);

        verify(first).parse(input);
        verifyZeroInteractions(third);
    }

}
