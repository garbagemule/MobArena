package com.garbagemule.MobArena.commands.user;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.garbagemule.MobArena.*;
import com.garbagemule.MobArena.commands.*;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.framework.ArenaMaster;

@CommandInfo(
    name    = "notready",
    pattern = "notr.*|ready",
    usage   = "/ma notready (<arena>)",
    desc    = "lists all available arenas",
    permission = "mobarena.use.arenalist"
)
public class NotReadyCommand implements Command
{
    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        // Grab the argument, if any
        String arg1 = (args.length > 0 ? args[0] : "");
        
        // The arena to query.
        Arena arena = null;
        
        if (!arg1.equals("")) {
            arena = am.getArenaWithName(arg1);
            if (arena == null) {
                Messenger.tellPlayer(sender, Msg.ARENA_DOES_NOT_EXIST);
                return false;
            }
        }
        else if (Commands.isPlayer(sender)) {
            Player p = (Player) sender;
            arena = am.getArenaWithPlayer(p);
            
            if (arena == null) {
                Messenger.tellPlayer(sender, Msg.LEAVE_NOT_PLAYING);
                return false;
            }
        }
        else {
            Messenger.tellPlayer(sender, "Usage: /ma notready <arena name>");
            return false;
        }
        
        String list = MAUtils.listToString(arena.getNonreadyPlayers(), am.getPlugin());
        Messenger.tellPlayer(sender, Msg.MISC_LIST_PLAYERS.toString(list));
        return true;
    }
}
