package com.garbagemule.MobArena.signs;

import org.bukkit.Location;
import org.bukkit.World;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class SignSerializerTest {

    SignSerializer subject;

    @Before
    public void setup() {
        subject = new SignSerializer();
    }

    @Test
    public void serializeReturnsSemicolonSeparatedRepresentation() {
        World world = mock(World.class);
        Location location = new Location(world, 1, 2, 3);
        String arenaId = "castle";
        String type = "join";
        String templateId = "status";
        when(world.getUID()).thenReturn(UUID.fromString("cafebabe-ea75-dead-beef-deadcafebabe"));
        when(world.getName()).thenReturn("world");
        ArenaSign sign = new ArenaSign(location, templateId, arenaId, type);

        String result = subject.serialize(sign);

        String expected = "cafebabe-ea75-dead-beef-deadcafebabe;world;1;2;3;castle;join;status";
        assertThat(result, equalTo(expected));
    }

    @Test
    public void deserializeThrowsIfLengthLessThan8() {
        World world = mock(World.class);
        String input = "1;2;3;4;5;6;7";

        Assert.assertThrows(
            "Invalid input; expected 8 parts, got 7",
            IllegalArgumentException.class,
            () -> subject.deserialize(input, world)
        );
    }

    @Test
    public void deserializeThrowsIfLengthGreaterThan8() {
        World world = mock(World.class);
        String input = "1;2;3;4;5;6;7;8;9";

        Assert.assertThrows(
            "Invalid input; expected 8 parts, got 9",
            IllegalArgumentException.class,
            () -> subject.deserialize(input, world)
        );
    }

    @Test
    public void deserializeThrowsIfWorldDoesNotMatchId() {
        World world = mock(World.class);
        when(world.getUID()).thenReturn(UUID.fromString("deadbeef-ea75-cafe-babe-deadcafebeef"));
        String input = "wrong-id;world;1;2;3;castle;join;status";

        Assert.assertThrows(
            "World mismatch",
            IllegalArgumentException.class,
            () -> subject.deserialize(input, world)
        );
    }

    @Test
    public void deserializeReturnsIfWorldMatchesId() {
        World world = mock(World.class);
        when(world.getUID()).thenReturn(UUID.fromString("cafebabe-ea75-dead-beef-deadcafebabe"));
        String input = "cafebabe-ea75-dead-beef-deadcafebabe;wrong-world;1;2;3;castle;join;status";

        ArenaSign result = subject.deserialize(input, world);

        Location location = new Location(world, 1, 2, 3);
        assertThat(result.location, equalTo(location));
        assertThat(result.arenaId, equalTo("castle"));
        assertThat(result.type, equalTo("join"));
        assertThat(result.templateId, equalTo("status"));
    }

    @Test
    public void deserializeSerializeReflexivity() {
        String id = "cafebabe-ea75-dead-beef-deadcafebabe";
        String name = "world";
        World world = mock(World.class);
        when(world.getName()).thenReturn(name);
        when(world.getUID()).thenReturn(UUID.fromString(id));
        String line = id + ";" + name + ";1;2;3;castle;join;cool-sign";

        String result = subject.serialize(subject.deserialize(line, world));

        assertThat(result, equalTo(line));
    }

    @Test
    public void serializeDeserializeReflexivity() {
        String id = "cafebabe-ea75-dead-beef-deadcafebabe";
        World world = mock(World.class);
        when(world.getUID()).thenReturn(UUID.fromString(id));
        Location location = new Location(world, 1, 2, 3);
        ArenaSign sign = new ArenaSign(location, "cool-sign", "castle", "join");

        ArenaSign result = subject.deserialize(subject.serialize(sign), world);

        assertThat(result.location, equalTo(location));
        assertThat(result.arenaId, equalTo("castle"));
        assertThat(result.type, equalTo("join"));
        assertThat(result.templateId, equalTo("cool-sign"));
    }

    @Test
    public void equalReturnsTrueIfStringsAreEqual() {
        String line = "cafebabe-ea75-dead-beef-deadcafebabe;world;1;2;3;castle;join;status";

        boolean result = subject.equal(line, line);

        assertThat(result, equalTo(true));
    }

    @Test
    public void equalReturnsTrueIfWorldIdAndLocationMatch() {
        String line1 = "cafebabe-ea75-dead-beef-deadcafebabe;right-world;1;2;3;jungle;leave;out";
        String line2 = "cafebabe-ea75-dead-beef-deadcafebabe;wrong-world;1;2;3;castle;join;status";

        boolean result = subject.equal(line1, line2);

        assertThat(result, equalTo(true));
    }

    @Test
    public void equalReturnsFalseIfOnlyWorldIdIsDifferent() {
        String line1 = "cafebabe-ea75-dead-beef-deadcafebabe;right-world;1;2;3;castle;join;status";
        String line2 = "deadbeef-feed-cafe-babe-a70ff1cecafe;right-world;1;2;3;castle;join;status";

        boolean result = subject.equal(line1, line2);

        assertThat(result, equalTo(false));
    }

    @Test
    public void equalReturnsFalseIfOnlyCoordsAreDifferent() {
        String line1 = "cafebabe-ea75-dead-beef-deadcafebabe;right-world;1;2;3;castle;join;status";
        String line2 = "cafebabe-ea75-dead-beef-deadcafebabe;right-world;4;5;6;castle;join;status";

        boolean result = subject.equal(line1, line2);

        assertThat(result, equalTo(false));
    }

}
