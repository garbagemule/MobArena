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
        // Grab the argument, if any.
        String arg1 = (args.length > 0 ? args[0] : "");

        // Require an argument
        if (!arg1.matches("^[a-zA-Z][a-zA-Z0-9]*$")) {
            Messenger.tellPlayer(sender, "Usage: /ma removecontainer <point name>");
            return false;
        }

        if (am.getSelectedArena().getRegion().removeSpawn(arg1))
            Messenger.tellPlayer(sender, "Container " + arg1 + " removed for arena '" + am.getSelectedArena().configName() + "'");
        else
            Messenger.tellPlayer(sender, "Could not find the container " + arg1 + "for the arena '" + am.getSelectedArena().configName() + "'");
        return true;
    }
}
