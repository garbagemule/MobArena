package com.garbagemule.MobArena.signs;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.framework.ArenaMaster;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class SignRendererTest {

    TemplateStore templateStore;
    ArenaMaster arenaMaster;
    RendersTemplate rendersTemplate;

    SignRenderer subject;

    @Before
    public void setup() {
        templateStore = mock(TemplateStore.class);
        arenaMaster = mock(ArenaMaster.class);
        rendersTemplate = mock(RendersTemplate.class);

        subject = new SignRenderer(
            templateStore,
            arenaMaster,
            rendersTemplate
        );
    }

    @Test
    public void rendersErrorMessageOnSignIfTemplateNotFound() {
        Location location = mock(Location.class);
        Block block = mock(Block.class);
        Sign target = mock(Sign.class);
        when(location.getBlock()).thenReturn(block);
        when(block.getState()).thenReturn(target);
        String templateId = "cool-sign";
        when(templateStore.findById(templateId)).thenReturn(Optional.empty());
        ArenaSign sign = new ArenaSign(location, templateId, "castle", "join");

        subject.render(sign);

        verify(target).setLine(0, ChatColor.RED + "[ERROR]");
        verify(target).setLine(1, "Template");
        verify(target).setLine(2, ChatColor.YELLOW + "cool-sign");
        verify(target).setLine(3, "not found :(");
    }

    @Test
    public void rendersErrorMessageOnSignIfArenaNotFound() {
        Location location = mock(Location.class);
        Block block = mock(Block.class);
        Sign target = mock(Sign.class);
        when(location.getBlock()).thenReturn(block);
        when(block.getState()).thenReturn(target);
        String templateId = "cool-sign";
        Template template = mock(Template.class);
        String arenaId = "castle";
        when(templateStore.findById(templateId)).thenReturn(Optional.of(template));
        when(arenaMaster.getArenaWithName(arenaId)).thenReturn(null);
        ArenaSign sign = new ArenaSign(location, templateId, arenaId, "join");

        subject.render(sign);

        verify(target).setLine(0, ChatColor.RED + "[ERROR]");
        verify(target).setLine(1, "Arena");
        verify(target).setLine(2, ChatColor.YELLOW + arenaId);
        verify(target).setLine(3, "not found :(");
    }

    @Test
    public void rendersResultsFromDelegateOnSign() {
        Location location = mock(Location.class);
        Block block = mock(Block.class);
        Sign target = mock(Sign.class);
        when(location.getBlock()).thenReturn(block);
        when(block.getState()).thenReturn(target);
        String templateId = "cool-sign";
        Template template = mock(Template.class);
        String arenaId = "castle";
        Arena arena = mock(Arena.class);
        String[] lines = new String[]{"this", "is", "a", "sign"};
        when(templateStore.findById(templateId)).thenReturn(Optional.of(template));
        when(arenaMaster.getArenaWithName(arenaId)).thenReturn(arena);
        when(rendersTemplate.render(template, arena)).thenReturn(lines);
        ArenaSign sign = new ArenaSign(location, templateId, arenaId, "join");

        subject.render(sign);

        verify(target).setLine(0, lines[0]);
        verify(target).setLine(1, lines[1]);
        verify(target).setLine(2, lines[2]);
        verify(target).setLine(3, lines[3]);
    }

    @Test
    public void rendersErrorMessageInEventIfTemplateNotFound() {
        String templateId = "cool-sign";
        when(templateStore.findById(templateId)).thenReturn(Optional.empty());
        ArenaSign sign = new ArenaSign(null, templateId, "castle", "join");
        SignChangeEvent event = mock(SignChangeEvent.class);

        subject.render(sign, event);

        verify(event).setLine(0, ChatColor.RED + "[ERROR]");
        verify(event).setLine(1, "Template");
        verify(event).setLine(2, ChatColor.YELLOW + "cool-sign");
        verify(event).setLine(3, "not found :(");
    }

    @Test
    public void rendersErrorMessageInEventIfArenaNotFound() {
        String templateId = "cool-sign";
        Template template = mock(Template.class);
        String arenaId = "castle";
        when(templateStore.findById(templateId)).thenReturn(Optional.of(template));
        when(arenaMaster.getArenaWithName(arenaId)).thenReturn(null);
        ArenaSign sign = new ArenaSign(null, templateId, arenaId, "join");
        SignChangeEvent event = mock(SignChangeEvent.class);

        subject.render(sign, event);

        verify(event).setLine(0, ChatColor.RED + "[ERROR]");
        verify(event).setLine(1, "Arena");
        verify(event).setLine(2, ChatColor.YELLOW + arenaId);
        verify(event).setLine(3, "not found :(");
    }

    @Test
    public void rendersResultsFromDelegateInEvent() {
        String templateId = "cool-sign";
        Template template = mock(Template.class);
        String arenaId = "castle";
        Arena arena = mock(Arena.class);
        String[] lines = new String[]{"this", "is", "a", "sign"};
        when(templateStore.findById(templateId)).thenReturn(Optional.of(template));
        when(arenaMaster.getArenaWithName(arenaId)).thenReturn(arena);
        when(rendersTemplate.render(template, arena)).thenReturn(lines);
        ArenaSign sign = new ArenaSign(null, templateId, arenaId, "join");
        SignChangeEvent event = mock(SignChangeEvent.class);

        subject.render(sign, event);

        verify(event).setLine(0, lines[0]);
        verify(event).setLine(1, lines[1]);
        verify(event).setLine(2, lines[2]);
        verify(event).setLine(3, lines[3]);
    }

}
