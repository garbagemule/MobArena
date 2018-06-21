package com.garbagemule.MobArena.signs;

import java.util.Map;
import java.util.Optional;

class TemplateStore {

    static final String FILENAME = "signs.yml";

    private final Map<String, Template> templates;

    TemplateStore(Map<String, Template> templates) {
        this.templates = templates;
    }

    Optional<Template> findById(String templateId) {
        Template template = templates.get(templateId);
        return Optional.ofNullable(template);
    }

}
