package com.garbagemule.MobArena.signs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

class SignFile {

    private static final OpenOption[] WRITE_OPTIONS = {
        StandardOpenOption.WRITE,
        StandardOpenOption.TRUNCATE_EXISTING,
        StandardOpenOption.SYNC
    };

    private final Path file;
    private final List<String> lines;

    private boolean stale;

    SignFile(Path file) {
        this.file = file;
        this.lines = new ArrayList<>();
        this.stale = true;
    }

    List<String> lines() throws IOException {
        if (stale) {
            load();
        }
        return new ArrayList<>(lines);
    }

    void append(String line) {
        lines.add(line);
    }

    void erase(String line) {
        lines.remove(line);
    }

    void load() throws IOException {
        if (Files.notExists(file)) {
            return;
        }

        lines.clear();
        lines.addAll(Files.readAllLines(file));

        stale = false;
    }

    void save() throws IOException {
        if (Files.notExists(file)) {
            Files.createDirectories(file.getParent());
            Files.createFile(file);
        }

        Files.write(file, lines, WRITE_OPTIONS);

        stale = true;
    }

}
