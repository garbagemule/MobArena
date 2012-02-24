package com.garbagemule.MobArena.commands.setup;

import org.bukkit.command.CommandSender;

import com.garbagemule.MobArena.*;
import com.garbagemule.MobArena.commands.*;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.framework.ArenaMaster;

@CommandInfo(
    name    = "removearena",
    pattern = "(del(.)*|r(e)?m(ove)?)arena",
    usage   = "/ma removearena <arena>",
    desc    = "remove an arena",
    permission = "mobarena.setup.removearena"
)
public class RemoveArenaCommand implements Command
{
    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        // Grab the argument, if any.
        String arg1 = (args.length > 0 ? args[0] : "");

        // Require an argument
        if (arg1.equals("")) {
            Messenger.tellPlayer(sender, "Usage: /ma removearena <arena>");
            return false;
        }
        
        if (am.getArenas().size() == 1) {
            Messenger.tellPlayer(sender, "At least one arena must exist.");
            return false;
        }
        
        Arena arena = am.getArenaWithName(arg1);
        if (arena == null) {
            Messenger.tellPlayer(sender, "There is no arena with that name.");
            return false;
        }
        
        am.removeArenaNode(arena);
        
        if (am.getSelectedArena().equals(arena)) {
            am.setSelectedArena(am.getArenas().get(0));
        }
        
        Messenger.tellPlayer(sender, "Arena '" + arena.configName() + "' deleted.");
        return true;
    }
}
