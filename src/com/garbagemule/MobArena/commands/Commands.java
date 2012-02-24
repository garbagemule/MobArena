package com.garbagemule.MobArena.commands;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.garbagemule.MobArena.Messenger;
import com.garbagemule.MobArena.Msg;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.framework.ArenaMaster;

public class Commands
{
    public static boolean isPlayer(CommandSender sender) {
        return (sender instanceof Player);
    }
    
    public static boolean sanityChecks(Player p, ArenaMaster am, Arena arena, String arg1, List<Arena> arenas) {
        if (arenas.size() > 1 && arg1.isEmpty())
            Messenger.tellPlayer(p, Msg.JOIN_ARG_NEEDED);
        else if (arena == null)
            Messenger.tellPlayer(p, Msg.ARENA_DOES_NOT_EXIST);
        else if (am.getArenaWithPlayer(p) != null && !arena.equals(am.getArenaWithPlayer(p)))
            Messenger.tellPlayer(p, Msg.JOIN_IN_OTHER_ARENA);
        else
            return true;
        
        return false;
    }
}
