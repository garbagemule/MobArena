package com.garbagemule.MobArena.commands.setup;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.garbagemule.MobArena.*;
import com.garbagemule.MobArena.commands.*;
import com.garbagemule.MobArena.framework.ArenaMaster;

@CommandInfo(
    name    = "addspawn",
    pattern = "addspawn(point)?",
    usage   = "/ma addspawn <point name>",
    desc    = "add a new spawnpoint for the selected arena",
    permission = "mobarena.setup.spawnpoints"
)
public class AddSpawnpointCommand implements Command
{
    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        if (!Commands.isPlayer(sender)) {
            Messenger.tellPlayer(sender, Msg.MISC_NOT_FROM_CONSOLE);
            return false;
        }
        
        // Grab the argument, if any.
        String arg1 = (args.length > 0 ? args[0] : "");
        
        // Cast the sender.
        Player p = (Player) sender;
        
        if (!arg1.matches("^[a-zA-Z][a-zA-Z0-9]*$")) {
            Messenger.tellPlayer(sender, "Usage: /ma addspawn <point name>");
            return true;
        }
        
        am.getSelectedArena().getRegion().addSpawn(arg1, p.getLocation());
        Messenger.tellPlayer(sender, "Spawnpoint " + arg1 + " added for arena \"" + am.getSelectedArena().configName() + "\"");
        return true;
    }
}
