package com.garbagemule.MobArena.things;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.garbagemule.MobArena.MobArena;
import net.milkbowl.vault.economy.Economy;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.logging.Logger;

public class MoneyThingParserTest {

    private MoneyThingParser subject;
    private MobArena plugin;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setup() {
        plugin = mock(MobArena.class);
        Economy economy = mock(Economy.class);
        when(plugin.getEconomy()).thenReturn(economy);

        subject = new MoneyThingParser(plugin);
    }

    @Test
    public void noPrefixNoBenjamins() {
        MoneyThing result = subject.parse("500");

        assertThat(result, is(nullValue()));
    }

    @Test
    public void shortPrefix() {
        MoneyThing result = subject.parse("$500");

        assertThat(result, not(nullValue()));
    }

    @Test
    public void longPrefix() {
        MoneyThing result = subject.parse("money:500");

        assertThat(result, not(nullValue()));
    }

    @Test
    public void nullEconomyNullMoney() {
        Logger logger = mock(Logger.class);
        when(plugin.getEconomy()).thenReturn(null);
        when(plugin.getLogger()).thenReturn(logger);

        subject.parse("$500");

        verify(logger).severe(anyString());
    }

    @Test
    public void numberFormatForNaughtyValues() {
        exception.expect(NumberFormatException.class);
        subject.parse("$cash");
    }

}
