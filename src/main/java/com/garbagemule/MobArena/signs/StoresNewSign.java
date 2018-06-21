package com.garbagemule.MobArena.signs;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.framework.ArenaMaster;
import org.bukkit.Location;

class StoresNewSign {

    private final ArenaMaster arenaMaster;
    private final TemplateStore templateStore;
    private final SignStore signStore;
    private final SavesSignStore savesSignStore;

    StoresNewSign(
        ArenaMaster arenaMaster,
        TemplateStore templateStore,
        SignStore signStore,
        SavesSignStore savesSignStore
    ) {
        this.arenaMaster = arenaMaster;
        this.templateStore = templateStore;
        this.signStore = signStore;
        this.savesSignStore = savesSignStore;
    }

    void store(
        Location location,
        String arenaId,
        String templateId,
        String signType
    ) {
        Arena arena = arenaMaster.getArenaWithName(arenaId);
        if (arena == null) {
            throw new IllegalArgumentException("Arena " + arenaId + " not found");
        }

        templateStore.findById(templateId)
            .orElseThrow(() -> new IllegalArgumentException(
                "Template " + templateId + " not found"
            ));

        switch (signType) {
            case "info":
            case "join":
            case "leave":
                break;
            default:
                throw new IllegalArgumentException("Invalid sign type: " + signType);
        }

        signStore.store(new ArenaSign(location, templateId, arenaId, signType));

        savesSignStore.save(signStore);
    }

}
