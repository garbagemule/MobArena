package com.garbagemule.MobArena.signs;

import static org.mockito.Mockito.*;

import com.garbagemule.MobArena.framework.Arena;
import org.bukkit.Location;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("WeakerAccess")
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class RedrawsArenaSignsTest {

    SignStore signStore;
    TemplateStore templateStore;
    RendersTemplate rendersTemplate;
    SetsLines setsSignLines;

    RedrawsArenaSigns subject;

    @Before
    public void setup() {
        signStore = mock(SignStore.class);
        when(signStore.findByArenaId(any()))
            .thenReturn(Collections.emptyList());

        templateStore = mock(TemplateStore.class);
        when(templateStore.findById(any()))
            .thenReturn(Optional.empty());

        rendersTemplate = mock(RendersTemplate.class);
        when(rendersTemplate.render(any(), any()))
            .thenReturn(new String[]{"a", "b", "c", "d"});

        setsSignLines = mock(SetsLines.class);

        subject = new RedrawsArenaSigns(
            signStore,
            templateStore,
            rendersTemplate,
            setsSignLines
        );
    }

    @Test
    public void noSignsMeansNoRenderingOrLineSetting() {
        Arena arena = arena("castle");

        subject.redraw(arena);

        verifyZeroInteractions(rendersTemplate);
        verifyZeroInteractions(setsSignLines);
    }

    @Test
    public void renderFoundTemplate() {
        String arenaId = "castle";
        Arena arena = arena(arenaId);
        ArenaSign sign = sign("join", arenaId);
        when(signStore.findByArenaId(arenaId))
            .thenReturn(Collections.singletonList(sign));
        Template template = template("template", "some", "info", "about", "arena");
        when(templateStore.findById(sign.templateId))
            .thenReturn(Optional.of(template));

        subject.redraw(arena);

        verify(rendersTemplate).render(template, arena);
    }

    @Test
    public void setRenderedTemplateOnSign() {
        String arenaId = "castle";
        Arena arena = arena(arenaId);
        ArenaSign sign = sign("join", arenaId);
        when(signStore.findByArenaId(sign.arenaId))
            .thenReturn(Collections.singletonList(sign));
        Template template = template("template", "try", "with", "more", "fireballs");
        when(templateStore.findById(sign.templateId))
            .thenReturn(Optional.of(template));
        String[] lines = new String[]{"this", "is", "a", "sign"};
        when(rendersTemplate.render(template, arena))
            .thenReturn(lines);

        subject.redraw(arena);

        verify(setsSignLines).set(sign.location, lines);
    }

    @Test
    public void renderEachTemplateOnlyOnce() {
        String arenaId = "castle";
        Arena arena = arena(arenaId);
        String templateId1 = "join";
        String templateId2 = "info";
        List<ArenaSign> signs = new ArrayList<>();
        signs.add(sign(templateId1, arenaId));
        signs.add(sign(templateId1, arenaId));
        signs.add(sign(templateId1, arenaId));
        signs.add(sign(templateId2, arenaId));
        when(signStore.findByArenaId(arenaId))
            .thenReturn(signs);
        Template template1 = template(templateId1, "join", "a", "MobArena", "today!");
        Template template2 = template(templateId2, "join", "another", "MobArena", "tomorrow!");
        when(templateStore.findById(templateId1))
            .thenReturn(Optional.of(template1));
        when(templateStore.findById(templateId2))
            .thenReturn(Optional.of(template2));

        subject.redraw(arena);

        verify(rendersTemplate, times(1)).render(template1, arena);
        verify(rendersTemplate, times(1)).render(template2, arena);
        verify(setsSignLines, times(signs.size())).set(any(), any());
    }

    private Arena arena(String arenaId) {
        Arena arena = mock(Arena.class);
        when(arena.configName()).thenReturn(arenaId);
        return arena;
    }

    private ArenaSign sign(String templateId, String arenaId) {
        Location location = mock(Location.class);
        return new ArenaSign(location, templateId, arenaId, "join");
    }

    private Template template(String id, String l1, String l2, String l3, String l4) {
        return new Template.Builder(id)
            .withBase(new String[]{l1, l2, l3, l4})
            .build();
    }

}
