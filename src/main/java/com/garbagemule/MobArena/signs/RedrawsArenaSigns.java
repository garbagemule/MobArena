package com.garbagemule.MobArena.signs;

import com.garbagemule.MobArena.framework.Arena;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class RedrawsArenaSigns {

    private final SignStore signStore;
    private final TemplateStore templateStore;
    private final RendersTemplate rendersTemplate;
    private final SetsLines setsSignLines;

    RedrawsArenaSigns(
        SignStore signStore,
        TemplateStore templateStore,
        RendersTemplate rendersTemplate,
        SetsLines setsSignLines
    ) {
        this.signStore = signStore;
        this.templateStore = templateStore;
        this.rendersTemplate = rendersTemplate;
        this.setsSignLines = setsSignLines;
    }

    void redraw(Arena arena) {
        List<ArenaSign> signs = signStore.findByArenaId(arena.configName());

        Map<String, String[]> rendered = signs.stream()
            .map(sign -> sign.templateId)
            .distinct()
            .collect(Collectors.toMap(
                templateId -> templateId,
                templateId -> render(templateId, arena)
            ));

        signs.forEach(sign -> setsSignLines.set(
            sign.location,
            rendered.get(sign.templateId)
        ));
    }

    private String[] render(String templateId, Arena arena) {
        return templateStore.findById(templateId)
            .map(template -> rendersTemplate.render(template, arena))
            .orElseGet(() -> notFound(templateId));
    }

    private static String[] notFound(String templateId) {
        return new String[]{
            String.join(ChatColor.MAGIC + "b" + ChatColor.RESET, "BROKEN".split("")),
            "Template",
            ChatColor.BOLD + templateId,
            "not found! :("
        };
    }

}
