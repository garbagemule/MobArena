package com.garbagemule.MobArena.signs;

import com.garbagemule.MobArena.MobArena;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class LoadsTemplateStore {

    private final MobArena plugin;

    LoadsTemplateStore(MobArena plugin) {
        this.plugin = plugin;
    }

    TemplateStore read() {
        YamlConfiguration yaml = new YamlConfiguration();
        try {
            File file = new File(plugin.getDataFolder(), TemplateStore.FILENAME);
            if (!file.exists()) {
                plugin.getLogger().info(TemplateStore.FILENAME + " not found, creating default...");
                plugin.saveResource(TemplateStore.FILENAME, false);
            }
            yaml.load(file);
        } catch (InvalidConfigurationException e) {
            throw new IllegalStateException(TemplateStore.FILENAME + " is invalid!", e);
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(TemplateStore.FILENAME + " is missing!", e);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        Map<String, Template> map = new HashMap<>();
        for (String key : yaml.getKeys(false)) {
            validateTemplateNode(key, yaml);
            String templateId = stripStateSuffix(key);
            map.computeIfAbsent(templateId, id -> loadTemplate(id, yaml));
        }

        plugin.getLogger().info("Loaded " + map.size() + " sign templates.");

        return new TemplateStore(map);
    }

    private void validateTemplateNode(String key, YamlConfiguration yaml) {
        List<?> list = yaml.getList(key);
        if (list == null) {
            String msg = "Template " + key + " in " + TemplateStore.FILENAME + " is not a list!";
            throw new IllegalStateException(msg);
        }
        list.forEach(element -> {
            if (!(element instanceof String)) {
                String msg = "Template " + key + " in " + TemplateStore.FILENAME + " is not a valid list of strings!";
                throw new IllegalStateException(msg);
            }
        });
    }

    private String stripStateSuffix(String id) {
        if (hasStateSuffix(id)) {
            return id.split("-", -1)[0];
        }
        return id;
    }

    private boolean hasStateSuffix(String id) {
        return id.endsWith("-idle")
            || id.endsWith("-joining")
            || id.endsWith("-ready")
            || id.endsWith("-running");
    }

    private Template loadTemplate(String id, YamlConfiguration yaml) {
        String[] base = getLines(yaml, id);
        String[] idle = getLines(yaml, id + "-idle");
        String[] joining = getLines(yaml, id + "-joining");
        String[] ready = getLines(yaml, id + "-ready");
        String[] running = getLines(yaml, id + "-running");

        return new Template.Builder(id)
            .withBase(base)
            .withIdle(idle)
            .withJoining(joining)
            .withReady(ready)
            .withRunning(running)
            .build();
    }

    private String[] getLines(YamlConfiguration config, String id) {
        List<String> list = config.getStringList(id);
        if (list.isEmpty()) {
            return null;
        }
        while (list.size() < 4) {
            list.add("");
        }
        if (list.size() > 4) {
            list = list.subList(0, 4);
        }
        return list.toArray(new String[0]);
    }

}
