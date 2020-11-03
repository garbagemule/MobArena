package com.garbagemule.MobArena.announce;

import org.bukkit.entity.Player;

public interface Announcer {

    /**
     * Announce the given message to the given player.
     *
     * @param player a player to send a message to, non-null
     * @param message the message to send, non-null
     */
    void announce(Player player, String message);

}
