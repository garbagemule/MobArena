package com.garbagemule.MobArena.things;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class ThingGroupPickerTest {

    private ThingGroupPicker subject;
    private List<ThingPicker> pickers;

    @Before
    public void setup() {
        pickers = new ArrayList<>();
        subject = new ThingGroupPicker(pickers);
    }

    @Test
    public void returnsNullIfResultIsEmpty() {
        Thing result = subject.pick();

        assertThat(result, nullValue());
    }

    @Test
    public void returnsSingletonItemIfResultSizeIsOne() {
        Thing thing = mock(Thing.class);
        ThingPicker picker = mock(ThingPicker.class);
        when(picker.pick()).thenReturn(thing);
        pickers.add(picker);

        Thing result = subject.pick();

        assertThat(result, sameInstance(thing));
    }

    @Test
    public void returnsThingGroupIfResultSizeIsGreaterThanOne() {
        Thing thing1 = mock(Thing.class);
        Thing thing2 = mock(Thing.class);
        ThingPicker picker1 = mock(ThingPicker.class);
        ThingPicker picker2 = mock(ThingPicker.class);
        when(picker1.pick()).thenReturn(thing1);
        when(picker2.pick()).thenReturn(thing2);
        pickers.add(picker1);
        pickers.add(picker2);

        Thing result = subject.pick();

        assertThat(result, instanceOf(ThingGroup.class));
    }

    @Test
    public void skipsNullPickInTwoPickersAndReturnsSingleton() {
        // This test is kind of "cheating" in that we're building off
        // the knowledge that a singleton result is returned as-is,
        // but at the time of writing, there's no straightforward way
        // to verify that nulls are omitted.
        Thing thing2 = mock(Thing.class);
        ThingPicker picker1 = mock(ThingPicker.class);
        ThingPicker picker2 = mock(ThingPicker.class);
        when(picker1.pick()).thenReturn(null);
        when(picker2.pick()).thenReturn(thing2);
        pickers.add(picker1);
        pickers.add(picker2);

        Thing result = subject.pick();

        assertThat(result, sameInstance(thing2));
    }

    @Test
    public void toStringConcatenatesWithAnd() {
        ThingPicker picker1 = mock(ThingPicker.class);
        ThingPicker picker2 = mock(ThingPicker.class);
        when(picker1.toString()).thenReturn("p1");
        when(picker2.toString()).thenReturn("p2");
        pickers.add(picker1);
        pickers.add(picker2);

        String result = subject.toString();

        assertThat(result, equalTo("(p1 and p2)"));
    }

}
