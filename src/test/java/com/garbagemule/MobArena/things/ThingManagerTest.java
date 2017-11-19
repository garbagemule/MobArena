package com.garbagemule.MobArena.things;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.garbagemule.MobArena.MobArena;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

public class ThingManagerTest {

    private ThingManager subject;

    @Before
    public void setup() {
        MobArena plugin = mock(MobArena.class);
        subject = new ThingManager(plugin);
    }

    @Test
    public void afterCoreParsersInOrder() {
        ThingParser first = mock(ThingParser.class);
        ThingParser second = mock(ThingParser.class);
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
    public void returnsNullIfNoParsersSucceed() {
        ThingParser first = mock(ThingParser.class);
        ThingParser second = mock(ThingParser.class);
        when(first.parse("thing")).thenReturn(null);
        when(second.parse("thing")).thenReturn(null);
        subject.register(first);
        subject.register(second);

        Thing result = subject.parse("thing");

        assertThat(result, nullValue());
    }

}
