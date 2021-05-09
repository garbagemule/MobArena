package com.garbagemule.MobArena.signs;

import org.bukkit.World;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class SignReaderTest {

    SignFile file;
    SignSerializer serializer;
    Logger log;

    SignReader subject;

    @Before
    public void setup() {
        file = mock(SignFile.class);
        serializer = mock(SignSerializer.class);
        log = mock(Logger.class);

        subject = new SignReader(
            file,
            serializer,
            log
        );
    }

    @Test
    public void deserializesNothingIfEmptyFile() throws IOException {
        String id = "cafebabe-ea75-dead-beef-deadcafebabe";
        World world = mock(World.class);
        when(world.getUID()).thenReturn(UUID.fromString(id));
        when(file.lines()).thenReturn(Collections.emptyList());

        subject.read(world);

        verifyNoInteractions(serializer);
    }

    @Test
    public void logsNothingIfEmptyFile() throws IOException {
        String id = "cafebabe-ea75-dead-beef-deadcafebabe";
        World world = mock(World.class);
        when(world.getUID()).thenReturn(UUID.fromString(id));
        when(file.lines()).thenReturn(Collections.emptyList());

        subject.read(world);

        verifyNoInteractions(log);
    }

    @Test
    public void deserializesLinesWithMatchingWorldId() throws IOException {
        String id = "cafebabe-ea75-dead-beef-deadcafebabe";
        World world = mock(World.class);
        when(world.getUID()).thenReturn(UUID.fromString(id));
        String line1 = "cafebeef-ea75-dead-babe-deadcafebeef;world;1;2;3;jungle;info;status";
        String line2 = id + ";world;1;2;3;castle;join;cool-sign";
        when(file.lines()).thenReturn(Arrays.asList(line1, line2));
        ArenaSign sign = new ArenaSign(null, "cool-sign", "castle", "join");
        when(serializer.deserialize(line2, world)).thenReturn(sign);

        List<ArenaSign> result = subject.read(world);

        assertThat(result.size(), equalTo(1));
        assertThat(result, hasItem(sign));
        verifyNoMoreInteractions(serializer);
    }

    @Test
    public void logsNothingOnSuccess() throws IOException {
        String id = "cafebabe-ea75-dead-beef-deadcafebabe";
        World world = mock(World.class);
        when(world.getUID()).thenReturn(UUID.fromString(id));
        String line = id + ";world;1;2;3;castle;join;cool-sign";
        when(file.lines()).thenReturn(Collections.singletonList(line));
        ArenaSign sign = new ArenaSign(null, "cool-sign", "castle", "join");
        when(serializer.deserialize(line, world)).thenReturn(sign);

        subject.read(world);

        verifyNoInteractions(log);
    }

    @Test
    public void skipLineIfSerializerThrows() throws IOException {
        String id = "cafebabe-ea75-dead-beef-deadcafebabe";
        World world = mock(World.class);
        when(world.getUID()).thenReturn(UUID.fromString(id));
        String line1 = id + ";world;1;2;3;jungle;info;status";
        String line2 = id + ";world;4;5;6;castle;join;cool-sign";
        when(file.lines()).thenReturn(Arrays.asList(line1, line2));
        IllegalArgumentException exception = new IllegalArgumentException("it's bad");
        ArenaSign sign = new ArenaSign(null, "cool-sign", "castle", "join");
        when(serializer.deserialize(line1, world)).thenThrow(exception);
        when(serializer.deserialize(line2, world)).thenReturn(sign);

        List<ArenaSign> result = subject.read(world);

        assertThat(result.size(), equalTo(1));
        assertThat(result, hasItem(sign));
    }

    @Test
    public void errorMessageIfSerializerThrows() throws IOException {
        String id = "cafebabe-ea75-dead-beef-deadcafebabe";
        World world = mock(World.class);
        when(world.getUID()).thenReturn(UUID.fromString(id));
        String line1 = id + ";world;1;2;3;jungle;info;status";
        String line2 = id + ";world;4;5;6;castle;join;cool-sign";
        when(file.lines()).thenReturn(Arrays.asList(line1, line2));
        IllegalArgumentException exception = new IllegalArgumentException("it's bad");
        ArenaSign sign = new ArenaSign(null, "cool-sign", "castle", "join");
        when(serializer.deserialize(line1, world)).thenThrow(exception);
        when(serializer.deserialize(line2, world)).thenReturn(sign);

        subject.read(world);

        verify(log).log(eq(Level.SEVERE), anyString(), eq(exception));
        verifyNoMoreInteractions(log);
    }

    @Test
    public void propagatesExceptionsFromFile() throws IOException {
        String id = "cafebabe-ea75-dead-beef-deadcafebabe";
        World world = mock(World.class);
        when(world.getUID()).thenReturn(UUID.fromString(id));
        IOException exception = new IOException("it's bad");
        when(file.lines()).thenThrow(exception);

        Assert.assertThrows(
            IOException.class,
            () -> subject.read(world)
        );
    }

}
