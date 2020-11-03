package com.garbagemule.MobArena.announce;

import com.garbagemule.MobArena.Messenger;
import org.bukkit.entity.Player;

public class MessengerAnnouncer implements Announcer {

    private final Messenger messenger;

    public MessengerAnnouncer(Messenger messenger) {
        this.messenger = messenger;
    }

    @Override
    public void announce(Player player, String message) {
        messenger.tell(player, message);
    }

}
