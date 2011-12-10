package com.garbagemule.MobArena.commands.user;

import java.util.*;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.garbagemule.MobArena.commands.*;
import com.garbagemule.MobArena.*;

public class JoinCommand implements MACommand
{
    @Override
    public String[] getNames() {
        return new String[] { "join" , "j" };
    }

    @Override
    public String getPermission() {
        return "mobarena.use.join";
    }

    @Override
    public boolean execute(MobArenaPlugin plugin, Player sender, String... args) {
        // Grab the arena master and get the argument.
        ArenaMaster   am = plugin.getArenaMaster();
        String arenaName = (args.length == 1) ? args[0] : null;
        
        // Get all arenas this player is eligible for.
        List<Arena> arenas = am.getEnabledAndPermittedArenas(sender);
        
        // If no arenas found, just tell the player it's not enabled.
        if (arenas.size() == 0) {
            plugin.tell(sender, Msg.JOIN_ARENA_NOT_ENABLED);
            return false;
        }
        
        // Require an argument in case of multiple arenas.
        if (arenaName == null && arenas.size() > 1) {
            plugin.tell(sender, Msg.JOIN_ARG_NEEDED);
            return false;
        }
        
        /* At this point in time, if there was no argument, there must be only
         * a single arena in the list. If there was an argument, we can safely
         * get the arena with that name, because canJoin() will do the rest of
         * the sanity checks. */
        Arena a = (arenaName == null) ? arenas.get(0) : am.getArenaWithName(arenaName);
        
        // If the arena is null, it doesn't exist.
        if (a == null) {
            plugin.tell(sender, Msg.ARENA_DOES_NOT_EXIST);
            return false;
        }
        
        // Let the arena itself do the rest of the sanity checking.
        if (!a.canJoin(sender)) {
            return false;
        }
        
        // If no problems, let the guy join and notify.
        a.playerJoin(sender, sender.getLocation());
        plugin.tell(sender, Msg.JOIN_PLAYER_JOINED);
        
        return true;
    }

    @Override
    public boolean executeFromConsole(MobArenaPlugin plugin, CommandSender sender, String... args) {
        return false;
    }
}
