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
        // Require a point name
        if (args.length != 1 || !args[0].matches("^[a-zA-Z][a-zA-Z0-9]*$")) return false;

        if (am.getSelectedArena().getRegion().removeSpawn(args[0])) {
            Messenger.tell(sender, "Spawnpoint " + args[0] + " removed for arena '" + am.getSelectedArena().configName() + "'");
        } else {
            Messenger.tell(sender, "Could not find the spawnpoint " + args[0] + "for the arena '" + am.getSelectedArena().configName() + "'");
        }
        return true;
    }
}
