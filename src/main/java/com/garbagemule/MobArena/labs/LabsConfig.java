package com.garbagemule.MobArena.labs;

import com.garbagemule.MobArena.housekeeper.HousekeeperConfig;

import java.util.Map;
import java.util.function.Function;

public class LabsConfig {

    public final HousekeeperConfig housekeeper;

    LabsConfig(
        HousekeeperConfig housekeeper
    ) {
        this.housekeeper = housekeeper;
    }

    static LabsConfig parse(Map<String, Object> root) {
        return new LabsConfig(
            parse(root, "housekeeper", HousekeeperConfig::parse)
        );
    }

    @SuppressWarnings("SameParameterValue")
    private static <C> C parse(
        Map<String, Object> root,
        String key,
        Function<Map<String, Object>, C> parse
    ) {
        if (root == null) {
            return parse.apply(null);
        }

        Object raw = root.get(key);

        @SuppressWarnings("unchecked")
        Map<String, Object> section = (Map<String, Object>) raw;

        return parse.apply(section);
    }

}
