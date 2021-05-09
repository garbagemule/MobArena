package com.garbagemule.MobArena.signs;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.framework.ArenaMaster;
import org.bukkit.Location;
import org.bukkit.event.block.SignChangeEvent;

class SignCreator {

    private final ArenaMaster arenaMaster;
    private final TemplateStore templateStore;

    SignCreator(
        ArenaMaster arenaMaster,
        TemplateStore templateStore
    ) {
        this.arenaMaster = arenaMaster;
        this.templateStore = templateStore;
    }

    ArenaSign create(SignChangeEvent event) {
        if (!trim(event, 0).equalsIgnoreCase("[MA]")) {
            return null;
        }

        Location location = event.getBlock().getLocation();
        String arenaId = getArenaId(event);
        String signType = getSignType(event);
        String templateId = getTemplateId(event, signType);

        return new ArenaSign(location, templateId, arenaId, signType);
    }

    private String getArenaId(SignChangeEvent event) {
        String arenaId = trim(event, 1);
        if (arenaId.isEmpty()) {
            throw new IllegalArgumentException("Missing arena name on line 2");
        }

        Arena arena = arenaMaster.getArenaWithName(arenaId);
        if (arena == null) {
            throw new IllegalArgumentException("Arena " + arenaId + " not found");
        }

        return arena.getSlug();
    }

    private String getSignType(SignChangeEvent event) {
        String signType = trim(event, 2).toLowerCase();
        if (signType.isEmpty()) {
            throw new IllegalArgumentException("Missing sign type on line 3");
        }

        switch (signType) {
            case "info":
            case "join":
            case "leave": {
                return signType;
            }
            default: {
                throw new IllegalArgumentException("Invalid sign type: " + signType);
            }
        }
    }

    private String getTemplateId(SignChangeEvent event, String signType) {
        String line = trim(event, 3);
        String templateId = !line.isEmpty() ? line : signType;

        templateStore
            .findById(templateId)
            .orElseThrow(() -> new IllegalArgumentException("Template " + templateId + " not found"));

        return templateId;
    }

    private String trim(SignChangeEvent event, int index) {
        String line = event.getLine(index);
        if (line == null) {
            return "";
        }
        return line.trim();
    }

}
