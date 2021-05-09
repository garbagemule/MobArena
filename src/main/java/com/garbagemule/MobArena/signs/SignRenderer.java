package com.garbagemule.MobArena.signs;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.framework.ArenaMaster;
import org.bukkit.ChatColor;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;

class SignRenderer {

    private final TemplateStore templateStore;
    private final ArenaMaster arenaMaster;
    private final RendersTemplate rendersTemplate;

    SignRenderer(
        TemplateStore templateStore,
        ArenaMaster arenaMaster,
        RendersTemplate rendersTemplate
    ) {
        this.templateStore = templateStore;
        this.arenaMaster = arenaMaster;
        this.rendersTemplate = rendersTemplate;
    }

    void render(ArenaSign sign) {
        BlockState state = sign.location.getBlock().getState();
        if (state instanceof Sign) {
            Sign target = (Sign) state;
            String[] lines = renderLines(sign);
            for (int i = 0; i < lines.length; i++) {
                target.setLine(i, lines[i]);
            }
            target.update();
        }
    }

    void render(ArenaSign sign, SignChangeEvent event) {
        String[] lines = renderLines(sign);
        for (int i = 0; i < lines.length; i++) {
            event.setLine(i, lines[i]);
        }
    }

    private String[] renderLines(ArenaSign sign) {
        String templateId = sign.templateId;
        String arenaId = sign.arenaId;

        Template template = templateStore.findById(templateId).orElse(null);
        if (template == null) {
            return templateNotFound(templateId);
        }

        Arena arena = arenaMaster.getArenaWithName(arenaId);
        if (arena == null) {
            return arenaNotFound(arenaId);
        }

        return rendersTemplate.render(template, arena);
    }

    private String[] templateNotFound(String templateId) {
        return new String[]{
            ChatColor.RED + "[ERROR]",
            "Template",
            ChatColor.YELLOW + templateId,
            "not found :("
        };
    }

    private String[] arenaNotFound(String arenaId) {
        return new String[]{
            ChatColor.RED + "[ERROR]",
            "Arena",
            ChatColor.YELLOW + arenaId,
            "not found :("
        };
    }

}
