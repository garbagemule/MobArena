package com.garbagemule.MobArena.commands.setup;

import org.bukkit.command.CommandSender;

import com.garbagemule.MobArena.*;
import com.garbagemule.MobArena.commands.*;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.framework.ArenaMaster;

@CommandInfo(
    name    = "checkdata",
    pattern = "checkdata",
    usage   = "/ma checkdata <arena>",
    desc    = "check if all required points are set up",
    permission = "mobarena.setup.checkdata"
)
public class CheckDataCommand implements Command
{
    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        Arena arena;
        if (args.length == 1) {
            if (am.getArenas().size() > 1) {
                Messenger.tell(sender, "There are multiple arenas.");
                return true;
            } else {
                arena = am.getArenas().get(0);
            }
        } else {
            arena = am.getArenaWithName(args[0]);
            if (arena == null) {
                Messenger.tell(sender, "There is no arena named " + args[0]);
                return true;
            }
        }
        arena.getRegion().checkData(am.getPlugin(), sender, true, true, true, true);
        return true;
    }
}
