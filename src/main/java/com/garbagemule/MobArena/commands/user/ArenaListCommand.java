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

import java.util.List;

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
            Player p = Commands.unwrap(sender);
            arenas = am.getPermittedArenas(p);
        } else {
            arenas = am.getArenas();
        }

        String list = MAUtils.listToString(arenas, am.getPlugin());
        am.getGlobalMessenger().tell(sender, Msg.MISC_LIST_ARENAS.format(list));
        return true;
    }
}
