package com.garbagemule.MobArena.commands.setup;

import com.garbagemule.MobArena.ArenaClass;
import com.garbagemule.MobArena.commands.Command;
import com.garbagemule.MobArena.commands.CommandInfo;
import com.garbagemule.MobArena.framework.ArenaMaster;
import com.garbagemule.MobArena.util.TextUtils;
import org.bukkit.command.CommandSender;

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
        // Require a class name
        if (args.length != 1) return false;
        
        // Find the class
        ArenaClass arenaClass = am.getClasses().get(args[0]);
        String className = TextUtils.camelCase(args[0]);
        if (arenaClass == null) {
            am.getGlobalMessenger().tell(sender, "The class '" + className + "' does not exist.");
            return true;
        }
        
        am.removeClassNode(className);
        am.getGlobalMessenger().tell(sender, "Removed class '" + className + "'.");
        return true;
    }
}
