package com.garbagemule.MobArena.commands.setup;

import org.bukkit.command.CommandSender;

import com.garbagemule.MobArena.*;
import com.garbagemule.MobArena.commands.*;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.framework.ArenaMaster;

@CommandInfo(
    name    = "checkdata",
    pattern = "checkdata",
    usage   = "/ma checkdata",
    desc    = "check if all required points are set up",
    permission = "mobarena.setup.checkdata"
)
public class CheckDataCommand implements Command
{
    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        // Grab the argument, if any.
        String arg1 = (args.length > 0 ? args[0] : "");
        
        Arena arena = arg1.equals("") ? am.getSelectedArena() : am.getArenaWithName(arg1);
        if (arena == null) {
            Messenger.tellPlayer(sender, Msg.ARENA_DOES_NOT_EXIST);
            return false;
        }
        
        arena.getRegion().checkData(am.getPlugin(), sender);
        return true;
    }
}
