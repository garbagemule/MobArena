package com.garbagemule.MobArena.labs;

import com.garbagemule.MobArena.MobArena;
import com.garbagemule.MobArena.housekeeper.Housekeeper;
import com.garbagemule.MobArena.housekeeper.HousekeeperConfig;
import com.garbagemule.MobArena.housekeeper.Housekeepers;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.Logger;

public class Labs {

    public final LabsConfig config;
    public final Housekeeper housekeeper;

    private Labs(
        LabsConfig config,
        Housekeeper housekeeper
    ) {
        this.config = config;
        this.housekeeper = housekeeper;
    }

    public static Labs create(MobArena plugin) throws IOException {
        Path labsFile = plugin.getDataFolder().toPath().resolve("labs.yml");
        if (!Files.exists(labsFile)) {
            return createDefault();
        }

        Logger log = plugin.getLogger();
        String[] lines = {
            "---==[ MobArena Labs ]==---",
            "Labs is a set of experimental opt-in features that are exempt from",
            "the goals of stability and robustness that are usually imposed on",
            "functionality in the plugin. This means that breaking changes are",
            "to be expected. No effort is made to ensure compatibility between",
            "different iterations of Labs features.",
        };
        Arrays.stream(lines).forEach(log::info);

        Yaml yaml = new Yaml();
        byte[] bytes = Files.readAllBytes(labsFile);
        Map<String, Object> map = yaml.load(new String(bytes));

        LabsConfig config = LabsConfig.parse(map);
        Housekeeper housekeeper = createHousekeeper(config, log);

        log.info("---");

        return new Labs(config, housekeeper);
    }

    private static Housekeeper createHousekeeper(LabsConfig root, Logger log) {
        HousekeeperConfig config = root.housekeeper;
        if (config == null || !config.enabled) {
            return Housekeepers.getDefault();
        }

        Housekeeper housekeeper = Housekeepers.create(config, log);
        log.info("Custom housekeeper created.");

        return housekeeper;
    }

    public static Labs createDefault() {
        return new Labs(
            LabsConfig.parse(null),
            Housekeepers.getDefault()
        );
    }

}
