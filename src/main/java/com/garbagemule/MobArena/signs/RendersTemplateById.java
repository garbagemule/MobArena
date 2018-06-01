package com.garbagemule.MobArena.signs;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.framework.ArenaMaster;

class RendersTemplateById {

    private final ArenaMaster arenaMaster;
    private final TemplateStore templateStore;
    private final RendersTemplate rendersTemplate;

    RendersTemplateById(
        ArenaMaster arenaMaster,
        TemplateStore templateStore,
        RendersTemplate rendersTemplate
    ) {
        this.arenaMaster = arenaMaster;
        this.templateStore = templateStore;
        this.rendersTemplate = rendersTemplate;
    }

    String[] render(String templateId, String arenaId) {
        Template template = templateStore.findById(templateId)
            .orElseThrow(() -> new IllegalArgumentException(
                "Template " + templateId + " not found"
            ));

        Arena arena = arenaMaster.getArenaWithName(arenaId);
        if (arena == null) {
            throw new IllegalStateException("Arena " + arenaId + " not found");
        }

        return rendersTemplate.render(template, arena);
    }

}
