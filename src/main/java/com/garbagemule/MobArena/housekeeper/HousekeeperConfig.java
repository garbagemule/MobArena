package com.garbagemule.MobArena.housekeeper;

import com.garbagemule.MobArena.labs.LabsConfigSection;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class HousekeeperConfig extends LabsConfigSection {

    public final Set<String> entities;

    private HousekeeperConfig(boolean enabled, Set<String> entities) {
        super(enabled);
        this.entities = Collections.unmodifiableSet(entities);
    }

    public static HousekeeperConfig parse(Map<String, Object> section) {
        boolean enabled = parseEnabled(section);
        Set<String> entities = parseEntities(section);

        return new HousekeeperConfig(enabled, entities);
    }

    private static boolean parseEnabled(Map<String, Object> section) {
        if (section == null) {
            return false;
        }

        Object raw = section.get("enabled");
        if (raw == null) {
            return false;
        }

        if (raw instanceof Boolean) {
            return (Boolean) raw;
        }
        if (raw instanceof String) {
            return Boolean.parseBoolean((String) raw);
        }

        throw new IllegalArgumentException("Unexpected 'enabled' value in housekeeper config");
    }

    private static Set<String> parseEntities(Map<String, Object> section) {
        if (section == null) {
            return Collections.emptySet();
        }

        Object raw = section.get("entities");
        if (raw == null) {
            return Collections.emptySet();
        }

        if (raw instanceof List) {
            return ((List<?>) raw)
                .stream()
                .map(String::valueOf)
                .collect(Collectors.toSet());
        }
        if (raw instanceof String) {
            String value = (String) raw;
            String[] parts = value.split(",");
            return Arrays.stream(parts)
                .map(String::trim)
                .collect(Collectors.toSet());
        }

        throw new IllegalArgumentException("Unexpected 'entities' value in housekeeper config");
    }

}
