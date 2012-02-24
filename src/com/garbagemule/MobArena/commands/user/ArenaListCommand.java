package com.garbagemule.MobArena.commands.user;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.garbagemule.MobArena.*;
import com.garbagemule.MobArena.commands.*;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.framework.ArenaMaster;

@CommandInfo(
    name    = "arenalist",
    pattern = "arenas|arenal.*|lista.*",
    usage   = "/ma arenas",
    desc    = "lists all available arenas",
    permission = "mobarena.use.arenalist"
)
public class ArenaListCommand implements Command
{
    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        List<Arena> arenas;
        
        if (Commands.isPlayer(sender)) {
            Player p = (Player) sender;
            arenas = am.getPermittedArenas(p); 
        } else {
            arenas = am.getArenas();
        }
        
        String list = MAUtils.listToString(arenas, am.getPlugin());
        Messenger.tellPlayer(sender, Msg.MISC_LIST_ARENAS.toString(list));
        return true;
    }
}
