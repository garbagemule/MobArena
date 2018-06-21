package com.garbagemule.MobArena.signs;

import com.garbagemule.MobArena.Messenger;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import java.util.stream.IntStream;

class HandlesSignCreation implements Listener {

    private final StoresNewSign storesNewSign;
    private final RendersTemplateById rendersTemplate;
    private final Messenger messenger;

    HandlesSignCreation(
        StoresNewSign storesNewSign,
        RendersTemplateById rendersTemplate,
        Messenger messenger
    ) {
        this.storesNewSign = storesNewSign;
        this.rendersTemplate = rendersTemplate;
        this.messenger = messenger;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void on(SignChangeEvent event) {
        if (!trim(event, 0).equalsIgnoreCase("[MA]")) {
            return;
        }

        Location location = event.getBlock().getLocation();
        String arenaId = trim(event, 1);
        String signType = trim(event, 2).toLowerCase();
        String templateId = trim(event, 3).toLowerCase();

        if (templateId.isEmpty()) {
            templateId = signType;
        }

        Player player = event.getPlayer();
        try {
            storesNewSign.store(location, arenaId, templateId, signType);
            messenger.tell(player, "New " + signType + " sign created for arena " + arenaId);

            String[] lines = rendersTemplate.render(templateId, arenaId);
            IntStream.range(0, 4)
                .forEach(i -> event.setLine(i, lines[i]));
        } catch (IllegalArgumentException e) {
            messenger.tell(player, e.getMessage());
        }
    }

    private String trim(SignChangeEvent event, int index) {
        String line = event.getLine(index);
        if (line == null) {
            return "";
        }
        return line.trim();
    }

}
