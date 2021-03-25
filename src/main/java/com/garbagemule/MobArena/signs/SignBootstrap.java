package com.garbagemule.MobArena.signs;

import com.garbagemule.MobArena.MobArena;
import org.bukkit.World;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SignBootstrap {

    private final MobArena plugin;

    private InvokesSignAction invokesSignAction;
    private RendersTemplate rendersTemplate;
    private SignCreator signCreator;
    private SignDataMigrator signDataMigrator;
    private SignFile signFile;
    private SignReader signReader;
    private SignRenderer signRenderer;
    private SignSerializer signSerializer;
    private SignStore signStore;
    private SignWriter signWriter;
    private TemplateStore templateStore;

    private SignBootstrap(MobArena plugin) {
        this.plugin = plugin;
    }

    MobArena getPlugin() {
        return plugin;
    }

    SignFile getSignFile() {
        if (signFile == null) {
            Path root = plugin.getDataFolder().toPath();
            Path data = root.resolve("data");
            Path file = data.resolve("signs.csv");
            signFile = new SignFile(file);
        }
        return signFile;
    }

    TemplateStore getTemplateStore() {
        if (templateStore == null) {
            templateStore = LoadsTemplateStore.load(plugin);
        }
        return templateStore;
    }

    InvokesSignAction getInvokesSignAction() {
        if (invokesSignAction == null) {
            invokesSignAction = new InvokesSignAction(
                plugin.getArenaMaster(),
                plugin.getGlobalMessenger()
            );
        }
        return invokesSignAction;
    }

    RendersTemplate getRendersTemplate() {
        if (rendersTemplate == null) {
            rendersTemplate = new RendersTemplate();
        }
        return rendersTemplate;
    }

    SignCreator getSignCreator() {
        if (signCreator == null) {
            signCreator = new SignCreator(
                plugin.getArenaMaster(),
                getTemplateStore()
            );
        }
        return signCreator;
    }

    SignDataMigrator getSignDataMigrator() {
        if (signDataMigrator == null) {
            Path root = plugin.getDataFolder().toPath();
            Path data = root.resolve("data");
            Path legacyFile = data.resolve("signs.data");
            Path pendingFile = data.resolve("signs.tmp");
            signDataMigrator = new SignDataMigrator(
                legacyFile,
                pendingFile,
                new Yaml(),
                getSignFile(),
                plugin.getLogger()
            );
        }
        return signDataMigrator;
    }

    SignReader getSignReader() {
        if (signReader == null) {
            signReader = new SignReader(
                getSignFile(),
                getSignSerializer(),
                plugin.getLogger()
            );
        }
        return signReader;
    }

    SignRenderer getSignRenderer() {
        if (signRenderer == null) {
            signRenderer = new SignRenderer(
                getTemplateStore(),
                plugin.getArenaMaster(),
                getRendersTemplate()
            );
        }
        return signRenderer;
    }

    SignSerializer getSignSerializer() {
        if (signSerializer == null) {
            signSerializer = new SignSerializer();
        }
        return signSerializer;
    }

    SignStore getSignStore() {
        if (signStore == null) {
            signStore = new SignStore();
        }
        return signStore;
    }

    SignWriter getSignWriter() {
        if (signWriter == null) {
            signWriter = new SignWriter(
                getSignFile(),
                getSignSerializer(),
                plugin.getLogger()
            );
        }
        return signWriter;
    }

    public static SignBootstrap create(MobArena plugin) {
        SignBootstrap bootstrap = new SignBootstrap(plugin);

        migrateData(bootstrap);
        loadSigns(bootstrap);
        initialRender(bootstrap);

        return bootstrap;
    }

    private static void migrateData(SignBootstrap bootstrap) {
        SignDataMigrator migrator = bootstrap.getSignDataMigrator();
        MobArena plugin = bootstrap.getPlugin();
        Logger log = plugin.getLogger();

        try {
            migrator.init();
        } catch (IOException e) {
            log.log(Level.SEVERE, "Failed initial sign data migration step", e);
            return;
        }

        for (World world : plugin.getServer().getWorlds()) {
            try {
                migrator.migrate(world);
            } catch (IOException e) {
                log.log(Level.SEVERE, "Failed to migrate sign data for world '" + world.getName() + "'", e);
            }
        }
    }

    private static void loadSigns(SignBootstrap bootstrap) {
        SignReader reader = bootstrap.getSignReader();
        SignStore store = bootstrap.getSignStore();
        MobArena plugin = bootstrap.getPlugin();
        Logger log = plugin.getLogger();

        List<ArenaSign> loaded = new ArrayList<>();
        try {
            for (World world : plugin.getServer().getWorlds()) {
                loaded.addAll(reader.read(world));
            }
        } catch (IOException e) {
            log.log(Level.SEVERE, "Failed to read from arena sign data file", e);
        }

        if (!loaded.isEmpty()) {
            loaded.forEach(store::add);
            log.info(loaded.size() + " arena sign(s) loaded.");
        }
    }

    private static void initialRender(SignBootstrap bootstrap) {
        SignStore store = bootstrap.getSignStore();
        SignRenderer renderer = bootstrap.getSignRenderer();

        bootstrap.getPlugin().getArenaMaster().getArenas().forEach(arena -> {
            List<ArenaSign> signs = store.findByArenaId(arena.configName());
            signs.forEach(renderer::render);
        });
    }

}
