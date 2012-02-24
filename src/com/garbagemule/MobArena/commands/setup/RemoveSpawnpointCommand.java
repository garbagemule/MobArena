package com.garbagemule.MobArena.commands.setup;

import org.bukkit.command.CommandSender;

import com.garbagemule.MobArena.*;
import com.garbagemule.MobArena.commands.*;
import com.garbagemule.MobArena.framework.ArenaMaster;

@CommandInfo(
    name    = "delspawn",
    pattern = "(del(.)*|r(e)?m(ove)?)spawn(point)?",
    usage   = "/ma delspawn <point name>",
    desc    = "add a new arena",
    permission = "mobarena.setup.spawnpoints"
)
public class RemoveSpawnpointCommand implements Command
{
    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        // Grab the argument, if any.
        String arg1 = (args.length > 0 ? args[0] : "");

        // Require an argument
        if (!arg1.matches("^[a-zA-Z][a-zA-Z0-9]*$")) {
            Messenger.tellPlayer(sender, "Usage: /ma removespawn <point name>");
            return true;
        }

        if (am.getSelectedArena().getRegion().removeSpawn(arg1))
            Messenger.tellPlayer(sender, "Spawnpoint " + arg1 + " removed for arena '" + am.getSelectedArena().configName() + "'");
        else
            Messenger.tellPlayer(sender, "Could not find the spawnpoint " + arg1 + "for the arena '" + am.getSelectedArena().configName() + "'");
        return true;
    }
}
