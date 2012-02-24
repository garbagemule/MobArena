package com.garbagemule.MobArena.commands.setup;

import org.bukkit.command.CommandSender;

import com.garbagemule.MobArena.*;
import com.garbagemule.MobArena.commands.*;
import com.garbagemule.MobArena.framework.ArenaMaster;
import com.garbagemule.MobArena.util.TextUtils;

@CommandInfo(
    name    = "removeclass",
    pattern = "(del(.)*|r(e)?m(ove)?)class",
    usage   = "/ma removeclass <classname>",
    desc    = "remove the given class",
    permission = "mobarena.setup.classes"
)
public class RemoveClassCommand implements Command
{
    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        // Grab the argument, if any.
        String arg1 = (args.length > 0 ? args[0] : "");

        // Require an argument.
        if (arg1.equals("")) {
            Messenger.tellPlayer(sender, "Usage: /ma removeclass <classname>");
            return false;
        }
        
        // Find the class
        ArenaClass arenaClass = am.getClasses().get(arg1);
        String className = TextUtils.camelCase(arg1);
        if (arenaClass == null) {
            Messenger.tellPlayer(sender, "The class '" + className + "' does not exist.");
            return false;
        }
        
        am.removeClassNode(className);
        Messenger.tellPlayer(sender, "Removed class '" + className + "'.");
        return true;
    }
}
