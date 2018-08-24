package com.garbagemule.MobArena.signs;

import com.garbagemule.MobArena.Messenger;
import com.garbagemule.MobArena.Msg;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.framework.ArenaMaster;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

class InvokesSignAction {

    private final ArenaMaster arenaMaster;
    private final Messenger messenger;

    InvokesSignAction(ArenaMaster arenaMaster, Messenger messenger) {
        this.arenaMaster = arenaMaster;
        this.messenger = messenger;
    }

    void invoke(ArenaSign sign, Player player) {
        if (sign.type.equals("join")) {
            Arena current = arenaMaster.getArenaWithPlayer(player);
            if (current != null) {
                if (current.inArena(player) || current.inLobby(player)) {
                    current.getMessenger().tell(player, Msg.JOIN_ALREADY_PLAYING);
                    return;
                }
                if (!current.playerLeave(player)) {
                    return;
                }
            }
            withArena(sign, player, arena -> {
                if (arena.canJoin(player)) {
                    // Join message is sent in playerJoin
                    arena.playerJoin(player, player.getLocation());
                }
            });
        } else if (sign.type.equals("leave")) {
            withArena(sign, player, arena -> {
                if (arena.inArena(player) || arena.inLobby(player) || arena.inSpec(player)) {
                    // Leave message is not sent in playerLeave
                    if (arena.playerLeave(player)) {
                        arena.getMessenger().tell(player, Msg.LEAVE_PLAYER_LEFT);
                    }
                }
            });
        }
    }

    private void withArena(ArenaSign sign, Player player, Consumer<Arena> action) {
        Arena arena = arenaMaster.getArenaWithName(sign.arenaId);
        if (arena == null) {
            messenger.tell(player, "Arena " + sign.arenaId + " not found");
            return;
        }
        action.accept(arena);
    }

}
