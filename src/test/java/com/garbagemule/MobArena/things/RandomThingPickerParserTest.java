package com.garbagemule.MobArena.things;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Random;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RandomThingPickerParserTest {

    private RandomThingPickerParser subject;
    private ThingPickerParser parser;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setup() {
        parser = mock(ThingPickerParser.class);
        subject = new RandomThingPickerParser(parser, new Random());
    }

    @Test
    public void returnsNullIfRandomIsMissing() {
        String input = "(a, b, c)";

        ThingPicker result = subject.parse(input);

        assertThat(result, nullValue());
    }

    @Test
    public void returnsNullIfParenthesesAreMissing() {
        String input = "random[a, b, c]";

        ThingPicker result = subject.parse(input);

        assertThat(result, nullValue());
    }

    @Test
    public void returnsNullIfNotRandom() {
        String input = "all(a, b, c)";

        ThingPicker result = subject.parse(input);

        assertThat(result, nullValue());
    }

    @Test
    public void invokesUnderlyingParserForEachItem() {
        String input = "random(a, b, c)";

        subject.parse(input);

        verify(parser, times(1)).parse("a");
        verify(parser, times(1)).parse("b");
        verify(parser, times(1)).parse("c");
    }

    @Test
    public void returnsRandomThingPickerMultipleThings() {
        String input = "random(a, b)";

        ThingPicker result = subject.parse(input);

        assertThat(result, instanceOf(RandomThingPicker.class));
    }

    @Test
    public void returnsOnlyPickerInsteadOfWrapping() {
        String input = "random(a)";
        ThingPicker picker = mock(ThingPicker.class);
        when(parser.parse("a")).thenReturn(picker);

        ThingPicker result = subject.parse(input);

        assertThat(result, is(picker));
    }

    @Test
    public void throwsIfZeroThings() {
        String input = "random()";
        exception.expect(IllegalArgumentException.class);

        subject.parse(input);
    }

}
