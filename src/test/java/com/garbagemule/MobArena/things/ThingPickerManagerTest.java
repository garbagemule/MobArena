package com.garbagemule.MobArena.things;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class ThingPickerManagerTest {

    private ThingPickerManager subject;
    private ThingParser things;

    @Before
    public void setup() {
        things = mock(ThingParser.class);
        subject = new ThingPickerManager(things);
    }

    @Test
    public void invokesParsersUntilOneReturnsNonNull() {
        String input = "all(a, b, c)";
        ThingPickerParser first = mock(ThingPickerParser.class);
        ThingPickerParser second = mock(ThingPickerParser.class);
        ThingPickerParser third = mock(ThingPickerParser.class);
        ThingPicker picker = mock(ThingPicker.class);
        when(first.parse(input)).thenReturn(null);
        when(second.parse(input)).thenReturn(picker);
        subject.register(first);
        subject.register(second);
        subject.register(third);

        subject.parse(input);

        verify(third, never()).parse(input);
        verify(things, never()).parse(input);
    }

    @Test
    public void invokesThingParserIfNothingElseWorks() {
        String input = "random(a, b, c)";
        ThingPickerParser first = mock(ThingPickerParser.class);
        ThingPickerParser second = mock(ThingPickerParser.class);
        when(first.parse(input)).thenReturn(null);
        when(second.parse(input)).thenReturn(null);
        subject.register(first);
        subject.register(second);

        subject.parse(input);

        verify(things, times(1)).parse(input);
    }

}
