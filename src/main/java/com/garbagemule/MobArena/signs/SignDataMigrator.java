package com.garbagemule.MobArena.signs;

import com.garbagemule.MobArena.util.Slugs;
import org.bukkit.World;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

class SignDataMigrator {

    private static final OpenOption[] WRITE_OPTIONS = {
        StandardOpenOption.CREATE,
        StandardOpenOption.WRITE,
        StandardOpenOption.TRUNCATE_EXISTING,
        StandardOpenOption.SYNC
    };

    private final Path legacyFile;
    private final Path pendingFile;
    private final Yaml yaml;
    private final SignFile signFile;
    private final Logger log;

    SignDataMigrator(
        Path legacyFile,
        Path pendingFile,
        Yaml yaml,
        SignFile signFile,
        Logger log
    ) {
        this.legacyFile = legacyFile;
        this.pendingFile = pendingFile;
        this.yaml = yaml;
        this.signFile = signFile;
        this.log = log;
    }

    void init() throws IOException {
        if (!Files.exists(legacyFile)) {
            return;
        }

        log.info("Legacy sign data found, migrating...");
        List<Map<String, ?>> signs = loadSignsInLegacyFile();
        List<String> lines = new ArrayList<>(signs.size());
        for (Map<String, ?> sign : signs) {
            String line = convert(sign);
            lines.add(line);
        }
        Files.write(pendingFile, lines, WRITE_OPTIONS);

        Files.delete(legacyFile);
        log.info("Legacy sign data migrated to temporary format.");
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, ?>> loadSignsInLegacyFile() throws IOException {
        byte[] bytes = Files.readAllBytes(legacyFile);
        Map<String, ?> map = yaml.load(new String(bytes));
        if (map != null && map.containsKey("signs")) {
            List<Map<String, ?>> signs = (List<Map<String, ?>>) map.get("signs");
            if (signs != null) {
                return signs;
            }
        }
        return Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    private String convert(Map<String, ?> sign) {
        Map<String, ?> location = (Map<String, Object>) sign.get("location");
        String world = (String) location.get("world");
        String x = String.valueOf(((Number) location.get("x")).intValue());
        String y = String.valueOf(((Number) location.get("y")).intValue());
        String z = String.valueOf(((Number) location.get("z")).intValue());
        String arenaId = Slugs.create((String) sign.get("arenaId"));
        String type = (String) sign.get("type");
        String templateId = (String) sign.get("templateId");
        return String.join(";", world, x, y, z, arenaId, type, templateId);
    }

    void migrate(World world) throws IOException {
        if (!Files.exists(pendingFile)) {
            return;
        }

        List<String> lines = Files.readAllLines(pendingFile);

        // Partition the lines into two lists; the ones to take from
        // the file for migration and the ones to skip.
        String name = world.getName();
        String namePrefix = name + ";";
        List<String> take = new ArrayList<>(lines.size());
        List<String> skip = new ArrayList<>(lines.size());
        for (String line : lines) {
            if (line.startsWith(namePrefix)) {
                take.add(line);
            } else {
                skip.add(line);
            }
        }
        if (take.isEmpty()) {
            return;
        }

        // Migrate all matching lines to the new sign data file.
        log.info("Found temporary sign data for world '" + name + "', migrating...");
        String id = world.getUID().toString();
        String idPrefix = id + ";";
        for (String line : take) {
            signFile.append(idPrefix + line);
        }
        signFile.save();
        log.info("Temporary data for " + take.size() + " sign(s) in world '" + name + "' migrated.");

        // Write the remaining lines back down into the pending file
        // if there are any left. Otherwise, migration has completed
        // and we are done.
        if (!skip.isEmpty()) {
            Files.write(pendingFile, skip, WRITE_OPTIONS);
        } else {
            Files.delete(pendingFile);
            log.info("Sign data migration complete.");
        }
    }

}
