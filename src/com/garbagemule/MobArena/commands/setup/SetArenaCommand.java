package com.garbagemule.MobArena.commands.setup;

import org.bukkit.command.CommandSender;

import com.garbagemule.MobArena.*;
import com.garbagemule.MobArena.commands.*;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.framework.ArenaMaster;

@CommandInfo(
    name    = "setarena",
    pattern = "(set|select)arena",
    usage   = "/ma setarena <arena>",
    desc    = "set an arena as the selected arena",
    permission = "mobarena.setup.setarena"
)
public class SetArenaCommand implements Command
{
    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        // Grab the argument, if any.
        String arg1 = (args.length > 0 ? args[0] : "");

        // Require an argument
        if (arg1.equals("")) {
            Messenger.tellPlayer(sender, "Usage: /ma setarena <arena>");
            return false;
        }
        
        Arena arena = am.getArenaWithName(arg1);
        if (arena != null) {
            am.setSelectedArena(arena);
            Messenger.tellPlayer(sender, "Currently selected arena: " + arena.configName());
        }
        else {
            Messenger.tellPlayer(sender, Msg.ARENA_DOES_NOT_EXIST);
        }
        
        return true;
    }
}
