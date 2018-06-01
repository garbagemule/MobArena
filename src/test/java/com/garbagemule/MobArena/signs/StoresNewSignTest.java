package com.garbagemule.MobArena.signs;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.framework.ArenaMaster;
import org.bukkit.Location;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

@SuppressWarnings("WeakerAccess")
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class StoresNewSignTest {

    ArenaMaster arenaMaster;
    TemplateStore templateStore;
    SignStore signStore;
    SavesSignStore savesSignStore;

    StoresNewSign subject;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setup() {
        arenaMaster = mock(ArenaMaster.class);
        when(arenaMaster.getArenaWithName(any()))
            .thenReturn(null);

        templateStore = mock(TemplateStore.class);
        when(templateStore.findById(any()))
            .thenReturn(Optional.empty());

        signStore = mock(SignStore.class);

        savesSignStore = mock(SavesSignStore.class);

        subject = new StoresNewSign(
            arenaMaster,
            templateStore,
            signStore,
            savesSignStore
        );
    }

    @Test
    public void throwOnNonExistentArena() {
        Location location = mock(Location.class);
        String arenaId = "castle";
        exception.expect(IllegalArgumentException.class);

        subject.store(location, arenaId, "", "");
    }

    @Test
    public void throwOnNonExistentTemplate() {
        Location location = mock(Location.class);
        String arenaId = "castle";
        Arena arena = mock(Arena.class);
        when(arenaMaster.getArenaWithName(arenaId))
            .thenReturn(arena);
        String templateId = "template";
        exception.expect(IllegalArgumentException.class);

        subject.store(location, arenaId, templateId, "");
    }

    @Test
    public void throwOnNonInvalidSignType() {
        Location location = mock(Location.class);
        String arenaId = "castle";
        Arena arena = mock(Arena.class);
        when(arenaMaster.getArenaWithName(arenaId))
            .thenReturn(arena);
        String templateId = "a very nice template";
        Template template = template(templateId);
        when(templateStore.findById(templateId))
            .thenReturn(Optional.of(template));
        String signType = "not a real sign type";
        exception.expect(IllegalArgumentException.class);

        subject.store(location, arenaId, templateId, signType);
    }

    @Test
    public void storesSignAndWritesToDisk() {
        Location location = mock(Location.class);
        String arenaId = "castle";
        Arena arena = mock(Arena.class);
        when(arenaMaster.getArenaWithName(arenaId))
            .thenReturn(arena);
        String templateId = "a very nice template";
        Template template = template(templateId);
        when(templateStore.findById(templateId))
            .thenReturn(Optional.of(template));
        String signType = "join";

        subject.store(location, arenaId, templateId, signType);

        ArgumentCaptor<ArenaSign> captor = ArgumentCaptor.forClass(ArenaSign.class);
        verify(signStore).store(captor.capture());
        verify(savesSignStore).save(signStore);
        ArenaSign sign = captor.getValue();
        assertThat(sign.location, equalTo(location));
        assertThat(sign.arenaId, equalTo(arenaId));
        assertThat(sign.templateId, equalTo(templateId));
        assertThat(sign.type, equalTo(signType));
    }

    private Template template(String id) {
        return new Template.Builder(id)
            .withBase(new String[]{"", "", "", ""})
            .build();
    }

}
