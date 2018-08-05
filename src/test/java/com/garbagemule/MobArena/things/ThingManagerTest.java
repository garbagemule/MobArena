package com.garbagemule.MobArena.things;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.garbagemule.MobArena.MobArena;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InOrder;

public class ThingManagerTest {

    private ThingManager subject;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setup() {
        MobArena plugin = mock(MobArena.class);
        subject = new ThingManager(plugin);
    }

    @Test
    public void afterCoreParsersInOrder() {
        ThingParser first = mock(ThingParser.class);
        ThingParser second = mock(ThingParser.class);
        when(second.parse(anyString())).thenReturn(mock(Thing.class));
        subject.register(first /*, false */);
        subject.register(second /*, false */);

        subject.parse("thing");

        InOrder order = inOrder(first, second);
        order.verify(first).parse("thing");
        order.verify(second).parse("thing");
    }

    @Test
    public void beforeCoreParsersInverseOrder() {
        ThingParser first = mock(ThingParser.class);
        ThingParser second = mock(ThingParser.class);
        when(first.parse(anyString())).thenReturn(mock(Thing.class));
        subject.register(first, true);
        subject.register(second, true);

        subject.parse("thing");

        InOrder order = inOrder(first, second);
        order.verify(second).parse("thing");
        order.verify(first).parse("thing");
    }

    @Test
    public void firstNonNullThingIsReturned() {
        Thing thing = mock(Thing.class);
        ThingParser first = mock(ThingParser.class);
        ThingParser second = mock(ThingParser.class);
        ThingParser third = mock(ThingParser.class);
        when(first.parse("thing")).thenReturn(null);
        when(second.parse("thing")).thenReturn(thing);
        when(third.parse("thing")).thenReturn(thing);
        subject.register(first);
        subject.register(second);
        subject.register(third);

        subject.parse("thing");

        verify(first).parse("thing");
        verify(second).parse("thing");
        verifyZeroInteractions(third);
    }

    @Test
    public void throwsIfNoParsersSucceed() {
        ThingParser first = mock(ThingParser.class);
        ThingParser second = mock(ThingParser.class);
        when(first.parse("thing")).thenReturn(null);
        when(second.parse("thing")).thenReturn(null);
        subject.register(first);
        subject.register(second);

        exception.expect(InvalidThingInputString.class);

        subject.parse("thing");
    }

}
