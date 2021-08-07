package com.garbagemule.MobArena.things;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.mockito.Mockito.*;

public class ThingGroupPickerParserTest {

    private ThingGroupPickerParser subject;
    private ThingPickerParser parser;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setup() {
        parser = mock(ThingPickerParser.class);
        subject = new ThingGroupPickerParser(parser);
    }

    @Test
    public void returnsNullIfAllIsMissing() {
        String input = "(a, b, c)";

        ThingPicker result = subject.parse(input);

        assertThat(result, nullValue());
    }

    @Test
    public void returnsNullIfParenthesesAreMissing() {
        String input = "all[a, b, c]";

        ThingPicker result = subject.parse(input);

        assertThat(result, nullValue());
    }

    @Test
    public void returnsNullIfNotAll() {
        String input = "random(a, b, c)";

        ThingPicker result = subject.parse(input);

        assertThat(result, nullValue());
    }

    @Test
    public void invokesUnderlyingParserForEachItem() {
        String input = "all(a, b, c)";

        subject.parse(input);

        verify(parser, times(1)).parse("a");
        verify(parser, times(1)).parse("b");
        verify(parser, times(1)).parse("c");
    }

    @Test
    public void returnsRandomThingPickerMultipleThings() {
        String input = "all(a, b)";

        ThingPicker result = subject.parse(input);

        assertThat(result, instanceOf(ThingGroupPicker.class));
    }

    @Test
    public void returnsSingleThingPickerIfOnlyOneThing() {
        String input = "all(a)";
        ThingPicker picker = mock(ThingPicker.class);
        when(parser.parse("a")).thenReturn(picker);

        ThingPicker result = subject.parse(input);

        assertThat(result, is(picker));
    }

    @Test
    public void throwsIfZeroThings() {
        String input = "all()";
        exception.expect(IllegalArgumentException.class);

        subject.parse(input);
    }

}
