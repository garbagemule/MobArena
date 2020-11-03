package com.garbagemule.MobArena.things;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ThingGroupPickerTest {

    private ThingGroupPicker subject;
    private List<ThingPicker> pickers;

    @Before
    public void setup() {
        pickers = new ArrayList<>();
        subject = new ThingGroupPicker(pickers);
    }

    @Test
    public void invokesAllPickers() {
        ThingPicker first = mock(ThingPicker.class);
        ThingPicker second = mock(ThingPicker.class);
        ThingPicker third = mock(ThingPicker.class);
        pickers.add(first);
        pickers.add(second);
        pickers.add(third);

        subject.pick();

        verify(first, times(1)).pick();
        verify(second, times(1)).pick();
        verify(third, times(1)).pick();
    }

}
