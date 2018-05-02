package com.garbagemule.MobArena.things;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

import net.milkbowl.vault.economy.Economy;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class MoneyThingParserTest {

    private MoneyThingParser subject;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setup() {
        Economy economy = mock(Economy.class);
        subject = new MoneyThingParser(economy);
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
