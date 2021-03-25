package com.garbagemule.MobArena.signs;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class SignFileTest {

    Path file;

    SignFile subject;

    @Before
    public void setup() throws IOException {
        file = Files.createTempFile("SignFileTest-", ".tmp");
        subject = new SignFile(file);
    }

    @After
    public void teardown() throws IOException {
        Files.delete(file);
    }

    @Test
    public void emptyFileEmptyLines() throws IOException {
        List<String> result = subject.lines();

        assertThat(result.isEmpty(), equalTo(true));
    }

    @Test
    public void emptyLinesIfAppendWithoutSave() throws IOException {
        String line1 = "We will";
        String line2 = "not be saved!";

        subject.append(line1);
        subject.append(line2);
        List<String> result = subject.lines();

        assertThat(result.isEmpty(), equalTo(true));
    }

    @Test
    public void linePersistedOnSave() throws IOException {
        String line1 = "We will";
        String line2 = "be saved!";

        subject.append(line1);
        subject.append(line2);
        subject.save();
        List<String> result = subject.lines();

        assertThat(result, equalTo(Arrays.asList(line1, line2)));
    }

    @Test
    public void lineLingersWithoutSave() throws IOException {
        String line1 = "We will";
        String line2 = "be saved!";

        subject.append(line1);
        subject.append(line2);
        subject.save();
        subject.erase(line1);
        List<String> result = subject.lines();

        assertThat(result, equalTo(Arrays.asList(line1, line2)));
    }

    @Test
    public void lineRemovedOnSave() throws IOException {
        String line1 = "We will";
        String line2 = "be saved!";

        subject.append(line1);
        subject.append(line2);
        subject.save();
        subject.erase(line1);
        subject.save();
        List<String> result = subject.lines();

        assertThat(result, equalTo(Collections.singletonList(line2)));
    }

}
