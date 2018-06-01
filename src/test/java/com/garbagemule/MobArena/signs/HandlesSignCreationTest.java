package com.garbagemule.MobArena.signs;

import static org.mockito.Mockito.*;

import com.garbagemule.MobArena.Messenger;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@SuppressWarnings("WeakerAccess")
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class HandlesSignCreationTest {

    StoresNewSign storesNewSign;
    RendersTemplateById rendersTemplate;
    Messenger messenger;

    HandlesSignCreation subject;

    @Before
    public void setup() {
        storesNewSign = mock(StoresNewSign.class);

        rendersTemplate = mock(RendersTemplateById.class);
        when(rendersTemplate.render(any(), any()))
            .thenReturn(new String[]{"", "", "", ""});

        messenger = mock(Messenger.class);

        subject = new HandlesSignCreation(
            storesNewSign,
            rendersTemplate,
            messenger
        );
    }

    @Test
    public void noHeaderNoAction() {
        String[] lines = {"why", "so", "serious", "?"};
        SignChangeEvent event = event(lines, null);

        subject.on(event);

        verifyZeroInteractions(storesNewSign, rendersTemplate, messenger);
    }

    @Test
    public void nullLinesHandledGracefully() {
        String[] lines = {"[MA]", null, null, null};
        SignChangeEvent event = event(lines, null);

        subject.on(event);
    }

    @Test
    public void useSignTypeIfTemplateNotAvailable() {
        String arenaId = "castle";
        String type = "join";
        String[] lines = {"[MA]", arenaId, type, null};
        Location location = mock(Location.class);
        SignChangeEvent event = event(lines, location);

        subject.on(event);

        verify(storesNewSign).store(location, arenaId, type, type);
    }

    @Test
    public void useTemplateIfAvailable() {
        String arenaId = "castle";
        String type = "join";
        String templateId = "potato";
        String[] lines = {"[MA]", arenaId, type, templateId};
        Location location = mock(Location.class);
        SignChangeEvent event = event(lines, location);

        subject.on(event);

        verify(storesNewSign).store(location, arenaId, templateId, type);
    }

    @Test
    public void typeIsLowercased() {
        String type = "JOIN";
        String[] lines = {"[MA]", "", type, ""};
        SignChangeEvent event = event(lines, null);

        subject.on(event);

        String lower = type.toLowerCase();
        verify(storesNewSign).store(any(), any(), any(), eq(lower));
    }

    @Test
    public void templateIdIsLowercased() {
        String templateId = "BEST-TEMPLATE";
        String[] lines = {"[MA]", "", "", templateId};
        SignChangeEvent event = event(lines, null);

        subject.on(event);

        String lower = templateId.toLowerCase();
        verify(storesNewSign).store(any(), any(), eq(lower), any());
    }

    @Test
    public void rendersTemplateAfterStoring() {
        String arenaId = "castle";
        String type = "join";
        String templateId = "potato";
        String[] lines = {"[MA]", arenaId, type, templateId};
        Location location = mock(Location.class);
        SignChangeEvent event = event(lines, location);

        subject.on(event);

        verify(rendersTemplate).render(templateId, arenaId);
    }

    @Test
    public void errorPassedToMessenger() {
        String msg = "you messed up";
        doThrow(new IllegalArgumentException(msg))
            .when(storesNewSign).store(any(), any(), any(), any());
        String[] lines = {"[MA]", "", "", ""};
        SignChangeEvent event = event(lines, null);

        subject.on(event);

        verifyZeroInteractions(rendersTemplate);
        verify(messenger).tell(event.getPlayer(), msg);
    }

    private SignChangeEvent event(String[] lines, Location location) {
        Block block = mock(Block.class);
        when(block.getLocation()).thenReturn(location);
        Player player = mock(Player.class);
        return new SignChangeEvent(block, player, lines);
    }

}
