package com.garbagemule.MobArena.announce;

import com.garbagemule.MobArena.Messenger;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class MessengerAnnouncer implements Announcer {

    private final Messenger messenger;

    @Override
    public void announce(Player player, String message) {
        messenger.tell(player, message);
    }

}
