package com.garbagemule.MobArena.commands.setup;

import com.garbagemule.MobArena.framework.Arena;
import org.bukkit.command.CommandSender;

import com.garbagemule.MobArena.*;
import com.garbagemule.MobArena.commands.*;
import com.garbagemule.MobArena.framework.ArenaMaster;

@CommandInfo(
    name    = "removecontainer",
    pattern = "(del(.)*|r(e)?m(ove)?)(container|chest)",
    usage   = "/ma removecontainer <arena> <chest>",
    desc    = "remove a container from the selected arena",
    permission = "mobarena.setup.containers"
)
public class RemoveContainerCommand implements Command
{
    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        if (args.length < 1) return false;

        Arena arena;
        String chest;
        if (args.length == 1) {
            if (am.getArenas().size() > 1) {
                am.getGlobalMessenger().tell(sender, "There are multiple arenas.");
                return true;
            }
            arena = am.getArenas().get(0);
            chest = args[0];
        } else {
            arena = am.getArenaWithName(args[0]);
            if (arena == null) {
                am.getGlobalMessenger().tell(sender, "There is no arena named " + args[0]);
                return true;
            }
            chest = args[1];
        }

        if (arena.getRegion().removeChest(chest)) {
            am.getGlobalMessenger().tell(sender, "Container " + chest + " removed for arena '" + arena.configName() + "'");
        } else {
            am.getGlobalMessenger().tell(sender, "Could not find the container " + chest + " for the arena '" + arena.configName() + "'");
        }
        return true;
    }
}
