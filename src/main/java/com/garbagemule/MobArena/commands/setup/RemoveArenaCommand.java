package com.garbagemule.MobArena.commands.setup;

import com.garbagemule.MobArena.commands.Command;
import com.garbagemule.MobArena.commands.CommandInfo;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.framework.ArenaMaster;
import org.bukkit.command.CommandSender;

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
        // Require an arena name
        if (args.length != 1) return false;
        
        if (am.getArenas().size() == 1) {
            am.getGlobalMessenger().tell(sender, "At least one arena must exist.");
            return true;
        }
        
        Arena arena = am.getArenaWithName(args[0]);
        if (arena == null) {
            am.getGlobalMessenger().tell(sender, "There is no arena with that name.");
            return true;
        }
        am.removeArenaNode(arena);
        am.getGlobalMessenger().tell(sender, "Arena '" + arena.configName() + "' deleted.");
        return true;
    }
}
