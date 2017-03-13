package com.garbagemule.MobArena.commands.user;

import com.garbagemule.MobArena.MAUtils;
import com.garbagemule.MobArena.Msg;
import com.garbagemule.MobArena.commands.Command;
import com.garbagemule.MobArena.commands.CommandInfo;
import com.garbagemule.MobArena.commands.Commands;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.framework.ArenaMaster;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(
    name    = "notready",
    pattern = "notr.*|ready",
    usage   = "/ma notready (<arena>)",
    desc    = "see which players aren't ready",
    permission = "mobarena.use.notready"
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
                am.getGlobalMessenger().tell(sender, Msg.ARENA_DOES_NOT_EXIST);
                return false;
            }
        } else if (Commands.isPlayer(sender)) {
            Player p = Commands.unwrap(sender);
            arena = am.getArenaWithPlayer(p);
            
            if (arena == null) {
                am.getGlobalMessenger().tell(sender, Msg.LEAVE_NOT_PLAYING);
                return true;
            }
        } else {
            return false;
        }
        
        String list = MAUtils.listToString(arena.getNonreadyPlayers(), am.getPlugin());
        arena.getMessenger().tell(sender, Msg.MISC_LIST_PLAYERS.format(list));
        return true;
    }
}
