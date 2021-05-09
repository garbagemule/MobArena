package com.garbagemule.MobArena.signs;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.framework.ArenaMaster;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class SignCreatorTest {

    ArenaMaster arenaMaster;
    TemplateStore templateStore;

    SignCreator subject;

    @Before
    public void setup() {
        arenaMaster = mock(ArenaMaster.class);
        templateStore = mock(TemplateStore.class);

        subject = new SignCreator(
            arenaMaster,
            templateStore
        );
    }

    @Test
    public void noHeaderNoAction() {
        String[] lines = {"ma", "castle", "join", "cool-sign"};
        SignChangeEvent event = new SignChangeEvent(null, null, lines);

        ArenaSign result = subject.create(event);

        assertThat(result, nullValue());
        verifyNoInteractions(arenaMaster, templateStore);
    }

    @Test
    public void throwsOnMissingArena() {
        String[] lines = {"[MA]", null, "join", "cool-sign"};
        SignChangeEvent event = event(lines, null);

        assertThrows(
            IllegalArgumentException.class,
            () -> subject.create(event)
        );
        verifyNoInteractions(arenaMaster);
    }

    @Test
    public void throwsIfArenaNotFound() {
        String arenaId = "castle";
        String[] lines = {"[MA]", arenaId, "join", "cool-sign"};
        when(arenaMaster.getArenaWithName(arenaId)).thenReturn(null);
        SignChangeEvent event = event(lines, null);

        assertThrows(
            IllegalArgumentException.class,
            () -> subject.create(event)
        );
    }

    @Test
    public void throwsOnMissingType() {
        String arenaId = "castle";
        String[] lines = {"[MA]", arenaId, null, "cool-sign"};
        Arena arena = mock(Arena.class);
        when(arena.getSlug()).thenReturn(arenaId);
        when(arenaMaster.getArenaWithName(arenaId)).thenReturn(arena);
        SignChangeEvent event = event(lines, null);

        assertThrows(
            IllegalArgumentException.class,
            () -> subject.create(event)
        );
    }

    @Test
    public void throwsOnInvalidType() {
        String arenaId = "castle";
        String[] lines = {"[MA]", arenaId, "bob", "cool-sign"};
        Arena arena = mock(Arena.class);
        when(arena.getSlug()).thenReturn(arenaId);
        when(arenaMaster.getArenaWithName(arenaId)).thenReturn(arena);
        SignChangeEvent event = event(lines, null);

        assertThrows(
            IllegalArgumentException.class,
            () -> subject.create(event)
        );
    }

    @Test
    public void throwsIfTemplateNotFound() {
        String arenaId = "castle";
        String templateId = "cool-sign";
        String[] lines = {"[MA]", arenaId, "join", templateId};
        Arena arena = mock(Arena.class);
        when(arena.getSlug()).thenReturn(arenaId);
        when(arenaMaster.getArenaWithName(arenaId)).thenReturn(arena);
        when(templateStore.findById(templateId)).thenReturn(Optional.empty());
        SignChangeEvent event = event(lines, null);

        assertThrows(
            IllegalArgumentException.class,
            () -> subject.create(event)
        );
    }

    @Test
    public void completeSignDefinition() {
        String arenaId = "castle";
        String signType = "join";
        String templateId = "cool-sign";
        String[] lines = {"[MA]", arenaId, signType, templateId};
        Location location = mock(Location.class);
        Arena arena = mock(Arena.class);
        when(arena.getSlug()).thenReturn(arenaId);
        when(arenaMaster.getArenaWithName(arenaId)).thenReturn(arena);
        when(templateStore.findById(templateId)).thenReturn(Optional.of(mock(Template.class)));
        SignChangeEvent event = event(lines, location);

        ArenaSign result = subject.create(event);

        assertThat(result.location, equalTo(location));
        assertThat(result.arenaId, equalTo(arenaId));
        assertThat(result.type, equalTo(signType));
        assertThat(result.templateId, equalTo(templateId));
    }

    @Test
    public void slugifiedArenaId() {
        String arenaId = "Area 52";
        String arenaSlug = "area-52";
        String signType = "join";
        String templateId = "cool-sign";
        String[] lines = {"[MA]", arenaId, signType, templateId};
        Location location = mock(Location.class);
        Arena arena = mock(Arena.class);
        when(arena.getSlug()).thenReturn(arenaSlug);
        when(arenaMaster.getArenaWithName(arenaId)).thenReturn(arena);
        when(templateStore.findById(templateId)).thenReturn(Optional.of(mock(Template.class)));
        SignChangeEvent event = event(lines, location);

        ArenaSign result = subject.create(event);

        assertThat(result.location, equalTo(location));
        assertThat(result.arenaId, equalTo(arenaSlug));
        assertThat(result.type, equalTo(signType.toLowerCase()));
        assertThat(result.templateId, equalTo(templateId));
    }

    @Test
    public void caseInsensitiveSignType() {
        String arenaId = "castle";
        String signType = "jOiN";
        String templateId = "cool-sign";
        String[] lines = {"[MA]", arenaId, signType, templateId};
        Location location = mock(Location.class);
        Arena arena = mock(Arena.class);
        when(arena.getSlug()).thenReturn(arenaId);
        when(arenaMaster.getArenaWithName(arenaId)).thenReturn(arena);
        when(templateStore.findById(templateId)).thenReturn(Optional.of(mock(Template.class)));
        SignChangeEvent event = event(lines, location);

        ArenaSign result = subject.create(event);

        assertThat(result.location, equalTo(location));
        assertThat(result.arenaId, equalTo(arenaId));
        assertThat(result.type, equalTo(signType.toLowerCase()));
        assertThat(result.templateId, equalTo(templateId));
    }

    @Test
    public void signWithoutTemplateUsesType() {
        String arenaId = "castle";
        String signType = "join";
        String[] lines = {"[MA]", arenaId, signType, null};
        Location location = mock(Location.class);
        Arena arena = mock(Arena.class);
        when(arena.getSlug()).thenReturn(arenaId);
        when(arenaMaster.getArenaWithName(arenaId)).thenReturn(arena);
        when(templateStore.findById(signType)).thenReturn(Optional.of(mock(Template.class)));
        SignChangeEvent event = event(lines, location);

        ArenaSign result = subject.create(event);

        assertThat(result.templateId, equalTo(signType));
    }

    private SignChangeEvent event(String[] lines, Location location) {
        Block block = mock(Block.class);
        when(block.getLocation()).thenReturn(location);
        Player player = mock(Player.class);
        return new SignChangeEvent(block, player, lines);
    }

}
