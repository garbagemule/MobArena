package com.garbagemule.MobArena.things;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class CommandThingParserTest {

    private CommandThingParser subject;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setup() {
        subject = new CommandThingParser();
    }

    @Test
    public void emptyStringReturnsNull() {
        Thing result = subject.parse("");

        assertThat(result, is(nullValue()));
    }

    @Test
    public void commandWithoutPrefixReturnsNull() {
        Thing result = subject.parse("/give <player> dirt");

        assertThat(result, is(nullValue()));
    }

    @Test
    public void barePrefixReturnsNull() {
        Thing result = subject.parse("cmd");

        assertThat(result, is(nullValue()));
    }

    @Test
    public void commandWithShortPrefix() {
        String command = "/give <player> dirt";

        Thing result = subject.parse("cmd:" + command);

        Thing expected = new CommandThing(command);
        assertThat(result, equalTo(expected));
    }

    @Test
    public void commandWithLongPrefix() {
        String command = "/give <player> dirt";

        Thing result = subject.parse("command:" + command);

        Thing expected = new CommandThing(command);
        assertThat(result, equalTo(expected));
    }

    @Test
    public void commandWithTitle() {
        String command = "/give <player> dirt";
        String title = "the best command";

        Thing result = subject.parse("cmd(" + title + "):" + command);

        Thing expected = new CommandThing(command, title);
        assertThat(result, equalTo(expected));
    }

    @Test
    public void missingCloseParenThrows() {
        exception.expect(IllegalArgumentException.class);

        subject.parse("cmd(name:/give <player> dirt");
    }

    @Test
    public void missingColonAfterTitleThrows() {
        exception.expect(IllegalArgumentException.class);

        subject.parse("cmd(name)");
    }

}
