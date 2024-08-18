package com.garbagemule.MobArena.things;

import com.garbagemule.MobArena.MobArena;
import com.garbagemule.MobArena.finance.Finance;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.mockito.Mockito.*;

public class MoneyThingParserTest {

    private MobArena plugin;
    private MoneyThingParser subject;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setup() {
        plugin = mock(MobArena.class);
        when(plugin.getFinance()).thenReturn(mock(Finance.class));

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
    public void numberFormatForNaughtyValues() {
        exception.expect(NumberFormatException.class);
        subject.parse("$cash");
    }

}
