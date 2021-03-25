package com.garbagemule.MobArena.signs;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class SignWriterTest {

    SignFile file;
    SignSerializer serializer;
    Logger log;

    SignWriter subject;

    @Before
    public void setup() {
        file = mock(SignFile.class);
        serializer = mock(SignSerializer.class);
        log = mock(Logger.class);

        subject = new SignWriter(
            file,
            serializer,
            log
        );
    }

    @Test
    public void writeCallsAppendWithSerializedSign() throws IOException {
        ArenaSign sign = new ArenaSign(null, "cool-sign", "castle", "join");
        String line = "some arbitrary serialization";
        when(serializer.serialize(sign)).thenReturn(line);

        subject.write(sign);

        verify(file).append(line);
        verify(file).save();
    }

    @Test
    public void writeCallsEraseOnConflicts() throws IOException {
        ArenaSign sign = new ArenaSign(null, "cool-sign", "castle", "join");
        String line = "some arbitrary serialization";
        String c1 = "some conflicting line";
        String c2 = "some non-conflicting line";
        String c3 = "some other conflicting line";
        when(serializer.serialize(sign)).thenReturn(line);
        when(serializer.equal(c1, line)).thenReturn(true);
        when(serializer.equal(c2, line)).thenReturn(false);
        when(serializer.equal(c3, line)).thenReturn(true);
        when(file.lines()).thenReturn(Arrays.asList(c1, c2, c3));

        subject.write(sign);

        verify(file).erase(c1);
        verify(file).erase(c3);
    }

    @Test
    public void writeLogsWarningOnConflicts() throws IOException {
        ArenaSign sign = new ArenaSign(null, "cool-sign", "castle", "join");
        String line = "some arbitrary serialization";
        String c1 = "some conflicting line";
        String c2 = "some non-conflicting line";
        String c3 = "some other conflicting line";
        when(serializer.serialize(sign)).thenReturn(line);
        when(serializer.equal(c1, line)).thenReturn(true);
        when(serializer.equal(c2, line)).thenReturn(false);
        when(serializer.equal(c3, line)).thenReturn(true);
        when(file.lines()).thenReturn(Arrays.asList(c1, c2, c3));

        subject.write(sign);

        verify(log, times(2)).warning(anyString());
    }

    @Test
    public void successfulWriteLogsNothing() throws IOException {
        ArenaSign sign = new ArenaSign(null, "cool-sign", "castle", "join");
        String line = "some arbitrary serialization";
        when(serializer.serialize(sign)).thenReturn(line);

        subject.write(sign);

        verifyNoInteractions(log);
    }

    @Test
    public void eraseCallsEraseWithIdentifiedLineOnly() throws IOException {
        ArenaSign sign = new ArenaSign(null, "cool-sign", "castle", "join");
        String line = "right-id;wrong-name;some other stuff";
        String c1 = "wrong-id;wrong-name;some stuff";
        String c2 = "right-id;right-name;some other stuff";
        String c3 = "wrong-id;wrong-name;some more stuff";
        when(serializer.serialize(sign)).thenReturn(line);
        when(serializer.equal(anyString(), anyString())).thenReturn(false);
        when(serializer.equal(c2, line)).thenReturn(true);
        when(file.lines()).thenReturn(Arrays.asList(c1, c2, c3));

        subject.erase(sign);

        verify(file).erase(c2);
        verify(file).save();
        verifyNoMoreInteractions(file);
    }

    @Test
    public void successfulEraseLogsNothing() throws IOException {
        ArenaSign sign = new ArenaSign(null, "cool-sign", "castle", "join");
        String line = "right-id;wrong-name;some other stuff";
        String candiate1 = "wrong-id;wrong-name;some stuff";
        String candiate2 = "right-id;right-name;some other stuff";
        String candiate3 = "wrong-id;wrong-name;some more stuff";
        when(serializer.serialize(sign)).thenReturn(line);
        when(serializer.equal(anyString(), anyString())).thenReturn(false);
        when(serializer.equal(candiate2, line)).thenReturn(true);
        when(file.lines()).thenReturn(Arrays.asList(candiate1, candiate2, candiate3));

        subject.erase(sign);

        verifyNoInteractions(log);
    }

    @Test
    public void eraseBailsOnNoMatch() throws IOException {
        ArenaSign sign = new ArenaSign(null, "cool-sign", "castle", "join");
        String line = "right-id;right-name;some right stuff";
        List<String> candidates = Arrays.asList("a", "b", "c");
        when(serializer.serialize(sign)).thenReturn(line);
        when(serializer.equal(anyString(), anyString())).thenReturn(false);
        when(file.lines()).thenReturn(candidates);

        subject.erase(sign);

        verify(file, never()).erase(anyString());
    }

    @Test
    public void eraseLogsWarningOnNoMatch() throws IOException {
        ArenaSign sign = new ArenaSign(null, "cool-sign", "castle", "join");
        String line = "right-id;right-name;some right stuff";
        List<String> candidates = Arrays.asList("a", "b", "c");
        when(serializer.serialize(sign)).thenReturn(line);
        when(serializer.equal(anyString(), anyString())).thenReturn(false);
        when(file.lines()).thenReturn(candidates);

        subject.erase(sign);

        verify(log).warning(anyString());
    }

}
