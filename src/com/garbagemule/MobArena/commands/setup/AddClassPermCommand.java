package com.garbagemule.MobArena.commands.setup;

import org.bukkit.command.CommandSender;

import com.garbagemule.MobArena.*;
import com.garbagemule.MobArena.commands.*;
import com.garbagemule.MobArena.framework.ArenaMaster;
import com.garbagemule.MobArena.util.TextUtils;

@CommandInfo(
    name    = "addclassperm",
    pattern = "add(class)?perm(.*)",
    usage   = "/ma addclassperm <classname> <permission>",
    desc    = "add a per-class permission",
    permission = "mobarena.setup.classes"
)
public class AddClassPermCommand implements Command
{
    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        // Grab the argument, if any.
        String arg1 = (args.length > 0 ? args[0] : "");
        String arg2 = (args.length > 1 ? args[1] : "");
        
        if (arg1.equals("") || arg2.equals("")) {
            Messenger.tellPlayer(sender, "Usage: /ma addclassperm <classname> <permission>");
            return false;
        }
        
        // Grab the arena class
        ArenaClass arenaClass = am.getClasses().get(arg1);
        if (arenaClass == null) {
            Messenger.tellPlayer(sender, "The class '" + TextUtils.camelCase(arg1) + "' does not exist.");
            return false;
        }
        
        // Try to add the permission.
        if (am.addClassPermission(arg1, arg2)) {
            Messenger.tellPlayer(sender, "Added permission '" + arg2 + "' to class '" + TextUtils.camelCase(arg1) + "'.");
            return true;
        }
        
        // If it wasn't added, notify.
        Messenger.tellPlayer(sender, "Permission '" + arg2 + "' was NOT added to class '" + TextUtils.camelCase(arg1) + "'.");
        return false;
    }
}
