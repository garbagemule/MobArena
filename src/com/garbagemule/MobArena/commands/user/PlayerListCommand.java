package com.garbagemule.MobArena.commands.user;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.garbagemule.MobArena.*;
import com.garbagemule.MobArena.commands.*;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.framework.ArenaMaster;

@CommandInfo(
    name    = "playerlist",
    pattern = "player.*|listp.*",
    usage   = "/ma players (<arena>)",
    desc    = "lists players in an arena",
    permission = "mobarena.use.playerlist"
)
public class PlayerListCommand implements Command
{
    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        // Grab the argument, if any.
        String arg1 = (args.length > 0 ? args[0] : "");
        
        String list = null;
        if (!arg1.equals("")) {
            Arena arena = am.getArenaWithName(arg1);
            
            if (arena == null) {
                Messenger.tellPlayer(sender, Msg.ARENA_DOES_NOT_EXIST);
                return false;
            }
            
            list = MAUtils.listToString(arena.getPlayersInArena(), am.getPlugin());
        }
        else {
            StringBuffer buffy = new StringBuffer();
            List<Player> players = new LinkedList<Player>();
            
            for (Arena arena : am.getArenas()) {
                players.addAll(arena.getPlayersInArena());
            }
            
            buffy.append(MAUtils.listToString(players, am.getPlugin()));
            list = buffy.toString();
        }
        
        Messenger.tellPlayer(sender, Msg.MISC_LIST_PLAYERS.toString(list));
        return true;
    }
}
