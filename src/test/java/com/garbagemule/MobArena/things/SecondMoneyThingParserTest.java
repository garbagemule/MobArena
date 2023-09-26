package com.garbagemule.MobArena.things;

import com.garbagemule.MobArena.MobArena;
import net.milkbowl.vault.economy.Economy;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.logging.Logger;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class SecondMoneyThingParserTest {

    private MoneyThingParser subject;
    private MobArena plugin;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setup() {
        plugin = mock(MobArena.class);
        Economy economy = mock(Economy.class);

        subject = new MoneyThingParser(plugin);
    }

    @Test
    public void noPrefixNoBenjamins() {
        MoneyThing result = subject.parse("500");

        assertThat(result, is(nullValue()));
    }


    @Test
    public void nullEconomyNullMoney() {
        Logger logger = mock(Logger.class);
        when(plugin.getEconomy()).thenReturn(null);
        when(plugin.getLogger()).thenReturn(logger);

        subject.parse("$500");

        verify(logger).severe(anyString());
    }


}
