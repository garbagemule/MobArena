package com.garbagemule.MobArena.commands;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.garbagemule.MobArena.Messenger;
import com.garbagemule.MobArena.Msg;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.framework.ArenaMaster;
import com.garbagemule.MobArena.util.TextUtils;

public class Commands
{
    public static boolean isPlayer(CommandSender sender) {
        return (sender instanceof Player);
    }
    
    public static Arena getArenaToJoinOrSpec(ArenaMaster am, Player p, String arg1) {
        // Check if MobArena is enabled first.
        if (!am.isEnabled()) {
            Messenger.tellPlayer(p, Msg.JOIN_NOT_ENABLED);
            return null;
        }

        // Then check if we have permission at all.
        List<Arena> arenas = am.getPermittedArenas(p);
        if (arenas.isEmpty()) {
            Messenger.tellPlayer(p, Msg.JOIN_NO_PERMISSION);
            return null;
        }
        
        // Then check if we have any enabled arenas.
        arenas = am.getEnabledArenas(arenas);
        if (arenas.isEmpty()) {
            Messenger.tellPlayer(p, Msg.JOIN_NOT_ENABLED);
            return null;
        }
        
        // The arena to join.
        Arena arena = null;
        
        // Branch on whether there's an argument or not.
        if (arg1 != null) {
            arena = am.getArenaWithName(arg1);
            if (arena == null) {
                Messenger.tellPlayer(p, Msg.ARENA_DOES_NOT_EXIST);
                return null;
            }
            
            if (!arenas.contains(arena)) {
                Messenger.tellPlayer(p, Msg.JOIN_ARENA_NOT_ENABLED);
                return null;
            }
        }
        else {
            if (arenas.size() > 1) {
                Messenger.tellPlayer(p, Msg.JOIN_ARG_NEEDED);
                Messenger.tellPlayer(p, Msg.MISC_LIST_ARENAS.toString(TextUtils.listToString(arenas)));
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
