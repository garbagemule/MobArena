package com.garbagemule.MobArena.commands.setup;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.garbagemule.MobArena.*;
import com.garbagemule.MobArena.commands.*;
import com.garbagemule.MobArena.framework.ArenaMaster;

@CommandInfo(
    name    = "setregion",
    pattern = "set(region|p)",
    usage   = "/ma setregion p1|p2",
    desc    = "set the region points of an arena",
    permission = "mobarena.setup.setregion"
)
public class SetRegionCommand implements Command
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
        
        if (!(arg1.equals("p1") || arg1.equals("p2"))) {
            Messenger.tellPlayer(sender, "Usage: /ma setregion p1|p2");
            return true;
        }
        
        am.getSelectedArena().getRegion().set(arg1, p.getLocation());
        Messenger.tellPlayer(sender, "Region point " + arg1 + " for arena '" + am.getSelectedArena().configName() + "' set.");
        return true;
    }
}
