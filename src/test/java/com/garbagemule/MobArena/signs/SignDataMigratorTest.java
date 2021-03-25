package com.garbagemule.MobArena.signs;

import org.bukkit.World;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class SignDataMigratorTest {

    Path legacyFile;
    Path pendingFile;
    Yaml yaml;
    SignFile signFile;
    Logger log;

    SignDataMigrator subject;

    @Before
    public void setup() throws IOException {
        legacyFile = Files.createTempFile("SignDataMigratorTest-", ".data.tmp");
        pendingFile = Files.createTempFile("SignDataMigratorTest-", ".tmp.tmp");
        yaml = mock(Yaml.class);
        signFile = mock(SignFile.class);
        log = mock(Logger.class);

        subject = new SignDataMigrator(
            legacyFile,
            pendingFile,
            yaml,
            signFile,
            log
        );
    }

    @After
    public void teardown() throws IOException {
        Files.deleteIfExists(legacyFile);
        Files.deleteIfExists(pendingFile);
    }

    @Test
    public void initDoesNothingIfNoLegacyFile() throws IOException {
        Files.delete(pendingFile);
        Files.delete(legacyFile);

        subject.init();

        verifyNoInteractions(yaml, log);
        assertThat(Files.exists(legacyFile), equalTo(false));
        assertThat(Files.exists(pendingFile), equalTo(false));
    }

    @Test
    public void initCreatesPendingFileAndDeletesLegacyFile() throws IOException {
        Files.delete(pendingFile);
        Map<String, Object> map = new LinkedHashMap<>();
        when(yaml.load(anyString())).thenReturn(map);

        subject.init();

        assertThat(Files.exists(legacyFile), equalTo(false));
        assertThat(Files.exists(pendingFile), equalTo(true));
    }

    @Test
    public void initLogsInfoMessagesOnConversion() throws IOException {
        Files.delete(pendingFile);
        Map<String, Object> map = new LinkedHashMap<>();
        when(yaml.load(anyString())).thenReturn(map);

        subject.init();

        verify(log, times(2)).info(anyString());
    }

    @Test
    public void initMigratesToPartialFormat() throws IOException {
        Files.delete(pendingFile);
        List<Map<String, Object>> signs = new ArrayList<>();
        signs.add(sign("world", 46.0, 101.0, -191.0, "Area 52", "join", "cool-sign"));
        signs.add(sign("lazy", 47.0, 102.0, -192.0, "Mr. Bob's Bus", "info", "status"));
        signs.add(sign("eager", 48.0, 103.0, -194.0, "Mission: Impossible", "leave", "coward"));
        signs.add(sign("world", 49.0, 104.0, -198.0, "castle", "leave", "bye-bye"));
        Map<String, Object> root = new LinkedHashMap<>();
        root.put("signs", signs);
        when(yaml.load(anyString())).thenReturn(root);

        subject.init();

        List<String> lines = Files.readAllLines(pendingFile);
        assertThat(lines.size(), equalTo(signs.size()));
        assertThat(lines, hasItems(
            "world;46;101;-191;area-52;join;cool-sign",
            "lazy;47;102;-192;mr-bobs-bus;info;status",
            "eager;48;103;-194;mission-impossible;leave;coward",
            "world;49;104;-198;castle;leave;bye-bye"
        ));
    }

    @Test
    public void migrateDoesNothingIfNoPendingFile() throws IOException {
        Files.delete(pendingFile);
        World world = mock(World.class);

        subject.migrate(world);

        verifyNoInteractions(signFile, log);
    }

    @Test
    public void migrateDoesNothingIfNoMatchingLines() throws IOException {
        List<String> lines = Arrays.asList(
            "world;46;101;-191;castle;join;cool-sign",
            "lazy;47;102;-192;jungle;info;status",
            "world;48;103;-194;island;leave;coward"
        );
        Files.write(pendingFile, lines);
        String name = "not-world";
        World world = mock(World.class);
        when(world.getName()).thenReturn(name);

        subject.migrate(world);

        verifyNoInteractions(signFile, log);
    }

    @Test
    public void migratePassesMatchingLinesToSignFile() throws IOException {
        List<String> lines = Arrays.asList(
            "world;46;101;-191;castle;join;cool-sign",
            "lazy;47;102;-192;jungle;info;status",
            "world;48;103;-194;island;leave;coward"
        );
        Files.write(pendingFile, lines);
        String name = "world";
        String id = "cafebabe-ea75-dead-beef-deadcafebabe";
        World world = mock(World.class);
        when(world.getName()).thenReturn(name);
        when(world.getUID()).thenReturn(UUID.fromString(id));

        subject.migrate(world);

        verify(signFile).append(id + ";" + lines.get(0));
        verify(signFile).append(id + ";" + lines.get(2));
        verify(signFile).save();
        verifyNoMoreInteractions(signFile);
    }

    @Test
    public void migrateDeletesMatchingLinesFromPendingFile() throws IOException {
        List<String> lines = Arrays.asList(
            "world;46;101;-191;castle;join;cool-sign",
            "lazy;47;102;-192;jungle;info;status",
            "world;48;103;-194;island;leave;coward"
        );
        Files.write(pendingFile, lines);
        String name = "world";
        String id = "cafebabe-ea75-dead-beef-deadcafebabe";
        World world = mock(World.class);
        when(world.getName()).thenReturn(name);
        when(world.getUID()).thenReturn(UUID.fromString(id));

        subject.migrate(world);

        List<String> remaining = Files.readAllLines(pendingFile);
        assertThat(remaining, equalTo(lines.subList(1, 2)));
    }

    @Test
    public void migrateLogsInfoMessageOnMigration() throws IOException {
        List<String> lines = Arrays.asList(
            "world;46;101;-191;castle;join;cool-sign",
            "lazy;47;102;-192;jungle;info;status",
            "world;48;103;-194;island;leave;coward"
        );
        Files.write(pendingFile, lines);
        String name = "world";
        String id = "cafebabe-ea75-dead-beef-deadcafebabe";
        World world = mock(World.class);
        when(world.getName()).thenReturn(name);
        when(world.getUID()).thenReturn(UUID.fromString(id));

        subject.migrate(world);

        verify(log, times(2)).info(anyString());
    }

    @Test
    public void migrateDeletesPendingFileOnCompletion() throws IOException {
        List<String> lines = Arrays.asList(
            "world;46;101;-191;castle;join;cool-sign",
            "world;48;103;-194;island;leave;coward"
        );
        Files.write(pendingFile, lines);
        String name = "world";
        String id = "cafebabe-ea75-dead-beef-deadcafebabe";
        World world = mock(World.class);
        when(world.getName()).thenReturn(name);
        when(world.getUID()).thenReturn(UUID.fromString(id));

        subject.migrate(world);

        assertThat(Files.exists(pendingFile), equalTo(false));
    }

    @Test
    public void migrateLogsAdditionallyOnCompletion() throws IOException {
        List<String> lines = Arrays.asList(
            "world;46;101;-191;castle;join;cool-sign",
            "world;48;103;-194;island;leave;coward"
        );
        Files.write(pendingFile, lines);
        String name = "world";
        String id = "cafebabe-ea75-dead-beef-deadcafebabe";
        World world = mock(World.class);
        when(world.getName()).thenReturn(name);
        when(world.getUID()).thenReturn(UUID.fromString(id));

        subject.migrate(world);

        verify(log, times(3)).info(anyString());
    }

    private Map<String, Object> sign(
        String world,
        double x,
        double y,
        double z,
        String arenaId,
        String type,
        String templateId
    ) {
        Map<String, Object> location = new LinkedHashMap<>();
        location.put("==", "org.bukkit.Location");
        location.put("world", world);
        location.put("x", x);
        location.put("y", y);
        location.put("z", z);
        location.put("pitch", 0.0);
        location.put("yaw", 0.0);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("==", "com.garbagemule.MobArena.signs.ArenaSign");
        result.put("arenaId", arenaId);
        result.put("location", location);
        result.put("templateId", templateId);
        result.put("type", type);
        return result;
    }

}
