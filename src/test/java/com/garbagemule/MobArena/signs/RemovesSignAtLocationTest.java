package com.garbagemule.MobArena.signs;

import static org.mockito.Mockito.*;

import org.bukkit.Location;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

@SuppressWarnings("WeakerAccess")
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class RemovesSignAtLocationTest {

    SignStore signStore;
    SavesSignStore savesSignStore;

    RemovesSignAtLocation subject;

    @Before
    public void setup() {
        signStore = mock(SignStore.class);
        savesSignStore = mock(SavesSignStore.class);

        subject = new RemovesSignAtLocation(
            signStore,
            savesSignStore
        );
    }

    @Test
    public void noSignMeansNoWrite() {
        Location location = mock(Location.class);
        when(signStore.remove(location))
            .thenReturn(Optional.empty());

        subject.remove(location);

        verifyZeroInteractions(savesSignStore);
    }

    @Test
    public void signRemovedWritesStore() {
        Location location = mock(Location.class);
        ArenaSign sign = new ArenaSign(location, "", "", "");
        when(signStore.remove(location))
            .thenReturn(Optional.of(sign));

        subject.remove(location);

        verify(savesSignStore).save(signStore);
    }

}
