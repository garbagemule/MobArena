package com.garbagemule.MobArena.commands.setup;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.garbagemule.MobArena.*;
import com.garbagemule.MobArena.commands.*;
import com.garbagemule.MobArena.framework.ArenaMaster;
import com.garbagemule.MobArena.util.TextUtils;

@CommandInfo(
    name    = "setclass",
    pattern = "setclass|saveclass",
    usage   = "/ma setclass (safe) <classname>",
    desc    = "save your inventory as a class",
    permission = "mobarena.setup.classes"
)
public class SetClassCommand implements Command
{
    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        if (!Commands.isPlayer(sender)) {
            Messenger.tell(sender, Msg.MISC_NOT_FROM_CONSOLE);
            return true;
        }

        // Require at least a class name
        if (args.length < 1) return false;
        
        // Grab the argument, if any.
        String arg1 = (args.length > 0 ? args[0] : "");
        String arg2 = (args.length > 1 ? args[1] : "");
        
        // Unwrap the sender.
        Player p = Commands.unwrap(sender);
        
        // Check if we're overwriting.
        boolean safe = arg1.equals("safe");
        if (safe && arg2.equals("")) return false;
        
        // If so, use arg2, otherwise, use arg1
        String className = TextUtils.camelCase(safe ? arg2 : arg1);
        
        // Create the class.
        ArenaClass arenaClass = am.createClassNode(className, p.getInventory(), safe);
        
        // If the class is null, it was not created.
        if (arenaClass == null) {
            Messenger.tell(p, "That class already exists!");
            Messenger.tell(p, "To overwrite, omit the 'safe' parameter.");
            return true;
        }
        
        // Otherwise, yay!
        Messenger.tell(p, "Class '" + className + "' set with your current inventory.");
        return true;
    }
}
