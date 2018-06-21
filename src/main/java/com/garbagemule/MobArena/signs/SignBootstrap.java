package com.garbagemule.MobArena.signs;

import com.garbagemule.MobArena.MobArena;

@SuppressWarnings("WeakerAccess")
public class SignBootstrap {

    private final MobArena plugin;
    private final SignStore signStore;
    private final TemplateStore templateStore;

    private InvokesSignAction invokesSignAction;
    private RedrawsArenaSigns redrawsArenaSigns;
    private RemovesSignAtLocation removesSignAtLocation;
    private RendersTemplate rendersTemplate;
    private RendersTemplateById rendersTemplateById;
    private SetsLines setsLines;
    private StoresNewSign storesNewSign;
    private SavesSignStore savesSignStore;

    private SignBootstrap(
        MobArena plugin,
        SignStore signStore,
        TemplateStore templateStore
    ) {
        this.plugin = plugin;
        this.signStore = signStore;
        this.templateStore = templateStore;
    }

    MobArena getPlugin() {
        return plugin;
    }

    SignStore getSignStore() {
        return signStore;
    }

    TemplateStore getTemplateStore() {
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

    RedrawsArenaSigns getRedrawsArenaSigns() {
        if (redrawsArenaSigns == null) {
            redrawsArenaSigns = new RedrawsArenaSigns(
                getSignStore(),
                getTemplateStore(),
                getRendersTemplate(),
                getSetsLines()
            );
        }
        return redrawsArenaSigns;
    }

    RemovesSignAtLocation getRemovesSignAtLocation() {
        if (removesSignAtLocation == null) {
            removesSignAtLocation = new RemovesSignAtLocation(
                getSignStore(),
                getSavesSignStore()
            );
        }
        return removesSignAtLocation;
    }

    RendersTemplate getRendersTemplate() {
        if (rendersTemplate == null) {
            rendersTemplate = new RendersTemplate();
        }
        return rendersTemplate;
    }

    RendersTemplateById getRendersTemplateById() {
        if (rendersTemplateById == null) {
            rendersTemplateById = new RendersTemplateById(
                plugin.getArenaMaster(),
                getTemplateStore(),
                getRendersTemplate()
            );
        }
        return rendersTemplateById;
    }

    SetsLines getSetsLines() {
        if (setsLines == null) {
            setsLines = new SetsLines();
        }
        return setsLines;
    }

    StoresNewSign getStoresNewSign() {
        if (storesNewSign == null) {
            storesNewSign = new StoresNewSign(
                plugin.getArenaMaster(),
                getTemplateStore(),
                getSignStore(),
                getSavesSignStore()
            );
        }
        return storesNewSign;
    }

    SavesSignStore getSavesSignStore() {
        if (savesSignStore == null) {
            savesSignStore = new SavesSignStore(plugin);
        }
        return savesSignStore;
    }

    public static SignBootstrap create(MobArena plugin) {
        TemplateStore templateStore = new LoadsTemplateStore(plugin).read();
        SignStore signStore = new LoadsSignStore(plugin).load();

        SignBootstrap bootstrap = new SignBootstrap(plugin, signStore, templateStore);

        plugin.getArenaMaster().getArenas()
            .forEach(bootstrap.getRedrawsArenaSigns()::redraw);

        return bootstrap;
    }
}
