package com.garbagemule.MobArena.things;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.mockito.Mockito.*;

public class RandomThingPickerTest {

    private RandomThingPicker subject;
    private List<ThingPicker> pickers;
    private Random random;

    @Before
    public void setup() {
        pickers = new ArrayList<>();
        random = mock(Random.class);
        subject = new RandomThingPicker(pickers, random);
    }

    @Test
    public void invokesOnlyChosenPicker() {
        ThingPicker decoy = mock(ThingPicker.class);
        ThingPicker chosen = mock(ThingPicker.class);
        pickers.add(decoy);
        pickers.add(decoy);
        pickers.add(chosen);
        pickers.add(decoy);
        when(random.nextInt(pickers.size())).thenReturn(2);

        subject.pick();

        verify(decoy, never()).pick();
        verify(chosen, times(1)).pick();
    }

}
