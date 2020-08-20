package com.garbagemule.MobArena.commands;

import com.garbagemule.MobArena.Msg;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.framework.ArenaMaster;
import com.garbagemule.MobArena.util.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class Commands
{
    /**
     * Unwrap the given CommandSender reference, in case it is a proxy.
     * <p>
     * Because plugins like CommandSigns use horrible proxy hacks to do what
     * they need to do, a Player reference is not necessarily a real Player,
     * and using that reference brings MobArena into an inconsistent state.
     * <p>
     * The method returns the "real" Player reference by making a UUID lookup.
     *
     * @param sender a CommandSender reference, possibly a proxy, non-null
     * @return the real Player reference, possibly the same as the argument
     */
    public static Player unwrap(CommandSender sender) {
        Player proxy = (Player) sender;
        UUID id = proxy.getUniqueId();
        return Bukkit.getPlayer(id);
    }

    public static boolean isPlayer(CommandSender sender) {
        return (sender instanceof Player);
    }

    public static Arena getArenaToJoinOrSpec(ArenaMaster am, Player p, String arg1) {
        // Check if MobArena is enabled first.
        if (!am.isEnabled()) {
            am.getGlobalMessenger().tell(p, Msg.JOIN_NOT_ENABLED);
            return null;
        }

        // Then check if we have permission at all.
        List<Arena> arenas = am.getPermittedArenas(p);
        if (arenas.isEmpty()) {
            am.getGlobalMessenger().tell(p, Msg.JOIN_NO_PERMISSION);
            return null;
        }

        // Then check if we have any enabled arenas.
        arenas = am.getEnabledArenas(arenas);
        if (arenas.isEmpty()) {
            am.getGlobalMessenger().tell(p, Msg.JOIN_NOT_ENABLED);
            return null;
        }

        // The arena to join.
        Arena arena = null;

        // Branch on whether there's an argument or not.
        if (arg1 != null) {
            arena = am.getArenaWithName(arg1);
            if (arena == null) {
                am.getGlobalMessenger().tell(p, Msg.ARENA_DOES_NOT_EXIST);
                return null;
            }

            if (!arenas.contains(arena)) {
                am.getGlobalMessenger().tell(p, Msg.JOIN_ARENA_NOT_ENABLED);
                return null;
            }
        }
        else {
            if (arenas.size() > 1) {
                am.getGlobalMessenger().tell(p, Msg.JOIN_ARG_NEEDED);
                am.getGlobalMessenger().tell(p, Msg.MISC_LIST_ARENAS.format(TextUtils.listToString(arenas)));
                return null;
            }
            arena = arenas.get(0);
        }

        // If player is in a boat/minecart, eject!
        if (p.isInsideVehicle()) {
            p.leaveVehicle();
        }

        // If player is in a bed, unbed!
        if (p.isSleeping()) {
            p.kickPlayer("Banned for life... Nah, just don't join from a bed ;)");
            return null;
        }

        return arena;
    }
}
