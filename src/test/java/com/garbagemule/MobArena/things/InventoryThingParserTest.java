package com.garbagemule.MobArena.things;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.junit.Assert.assertThrows;

public class InventoryThingParserTest {

    private InventoryThingParser subject;

    @Before
    public void setup() {
        subject = new InventoryThingParser(null);
    }

    @Test
    public void returnsNullOnWrongType() {
        String input = "dirt";

        Thing result = subject.parse(input);

        assertThat(result, nullValue());
    }

    @Test
    public void returnsNullOnMismatchedParenthesis() {
        String input = "inv(world -10 20 130 15";

        Thing result = subject.parse(input);

        assertThat(result, nullValue());
    }

    @Test
    public void throwsOnTooFewArguments() {
        String input = "inv(world -10 20 130)";

        assertThrows(
            IllegalArgumentException.class,
            () -> subject.parse(input)
        );
    }

    @Test
    public void throwsOnTooManyArguments() {
        String input = "inv(world -10 20 130 15 16)";

        assertThrows(
            IllegalArgumentException.class,
            () -> subject.parse(input)
        );
    }

    @Test
    public void throwsOnUnknownSlotType() {
        String input = "inv(world -10 20 130 potato)";

        assertThrows(
            IllegalArgumentException.class,
            () -> subject.parse(input)
        );
    }

    @Test
    public void parsesIndexThing() {
        String input = "inv(world -10 20 130 15)";

        Thing result = subject.parse(input);

        assertThat(result, instanceOf(InventoryIndexThing.class));
    }

    @Test
    public void throwsIfIndexIsNegative() {
        String input = "inv(world -10 20 130 -1)";

        assertThrows(
            IllegalArgumentException.class,
            () -> subject.parse(input)
        );
    }

    @Test
    public void parsesRangeThing() {
        String input = "inv(world -10 20 130 0-8)";

        Thing result = subject.parse(input);

        assertThat(result, instanceOf(InventoryRangeThing.class));
    }

    @Test
    public void throwsIfRangeStartIsLessThanZero() {
        String input = "inv(world -10 20 130 -1-8)";

        assertThrows(
            IllegalArgumentException.class,
            () -> subject.parse(input)
        );
    }

    @Test
    public void throwsIfRangeEndIsLessThanRangeStart() {
        String input = "inv(world -10 20 130 5-2)";

        assertThrows(
            IllegalArgumentException.class,
            () -> subject.parse(input)
        );
    }

    @Test
    public void parsesAllGroup() {
        String input = "inv(world -10 20 130 all)";

        Thing result = subject.parse(input);

        assertThat(result, instanceOf(InventoryGroupThing.class));
    }

}
