package com.garbagemule.MobArena.announce;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class TitleAnnouncer implements Announcer {

    private final int fadeIn;
    private final int stay;
    private final int fadeOut;

    @Override
    public void announce(Player player, String message) {
        player.sendTitle(" ", message, fadeIn, stay, fadeOut);
    }

}
