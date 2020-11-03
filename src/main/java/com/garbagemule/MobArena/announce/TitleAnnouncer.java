package com.garbagemule.MobArena.announce;

import org.bukkit.entity.Player;

public class TitleAnnouncer implements Announcer {

    private final int fadeIn;
    private final int stay;
    private final int fadeOut;

    public TitleAnnouncer(int fadeIn, int stay, int fadeOut) {
        this.fadeIn = fadeIn;
        this.stay = stay;
        this.fadeOut = fadeOut;
    }

    @Override
    public void announce(Player player, String message) {
        player.sendTitle("", message);
    }

}
