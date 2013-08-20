package com.garbagemule.MobArena.commands.setup;

import org.bukkit.command.CommandSender;

import com.garbagemule.MobArena.*;
import com.garbagemule.MobArena.commands.*;
import com.garbagemule.MobArena.framework.ArenaMaster;

@CommandInfo(
    name    = "removecontainer",
    pattern = "(del(.)*|r(e)?m(ove)?)(container|chest)",
    usage   = "/ma removecontainer <point name>",
    desc    = "remove a container from the selected arena",
    permission = "mobarena.setup.containers"
)
public class RemoveContainerCommand implements Command
{
    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        // Require a point name
        if (args.length != 1 || !args[0].matches("^[a-zA-Z][a-zA-Z0-9]*$")) return false;

        if (am.getSelectedArena().getRegion().removeChest(args[0])) {
            Messenger.tell(sender, "Container " + args[0] + " removed for arena '" + am.getSelectedArena().configName() + "'");
        } else {
            Messenger.tell(sender, "Could not find the container " + args[0] + "for the arena '" + am.getSelectedArena().configName() + "'");
        }
        return true;
    }
}
