package com.garbagemule.MobArena.formula;

import com.garbagemule.MobArena.MobArena;
import lombok.RequiredArgsConstructor;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class FormulaMacros {

    private static final String FILENAME = "formulas.yml";

    private final Path file;
    private final Map<String, Map<String, String>> macros = new HashMap<>();

    public void reload() throws IOException {
        byte[] bytes = Files.readAllBytes(file);
        String content = new String(bytes);

        Yaml yaml = new Yaml();
        Map<?, ?> raw = yaml.load(content);
        Map<String, Map<String, String>> converted = convert(raw);

        macros.clear();
        macros.putAll(converted);
    }

    private Map<String, Map<String, String>> convert(Map<?, ?> raw) {
        Map<String, Map<String, String>> result = new HashMap<>();

        for (Map.Entry<?, ?> entry : raw.entrySet()) {
            String section = String.valueOf(entry.getKey());
            Map<String, String> macros = convert(entry.getValue());
            if (macros != null) {
                result.put(section, macros);
            }
        }

        return result;
    }

    private Map<String, String> convert(Object raw) {
        if (raw instanceof Map) {
            Map<String, String> macros = new HashMap<>();
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) raw).entrySet()) {
                String macro = String.valueOf(entry.getKey());
                String formula = String.valueOf(entry.getValue());
                macros.put(macro, formula);
            }
            return macros;
        }
        return null;
    }

    public String get(String key) {
        return get(null, key);
    }

    public String get(String section, String key) {
        if (key == null) {
            return null;
        }
        if (section != null) {
            String macro = lookup(section, key);
            if (macro != null) {
                return macro;
            }
        }
        return lookup("global", key);
    }

    private String lookup(String section, String key) {
        Map<String, String> specific = macros.get(section);
        if (specific == null) {
            return null;
        }
        return specific.get(key);
    }

    public static FormulaMacros create(MobArena plugin) {
        File file = new File(plugin.getDataFolder(), FILENAME);
        if (!file.exists()) {
            plugin.getLogger().info(FILENAME + " not found, creating default...");
            plugin.saveResource(FILENAME, false);
        }
        return new FormulaMacros(file.toPath());
    }

}
