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
        // Require classname and permission
        if (args.length != 2) return false;

        // Grab the arena class
        ArenaClass arenaClass = am.getClasses().get(args[0]);
        if (arenaClass == null) {
            Messenger.tell(sender, "The class '" + TextUtils.camelCase(args[0]) + "' does not exist.");
            return true;
        }
        
        // Try to add the permission.
        if (am.addClassPermission(args[0], args[1])) {
            Messenger.tell(sender, "Added permission '" + args[1] + "' to class '" + TextUtils.camelCase(args[0]) + "'.");
            return true;
        }
        
        // If it wasn't added, notify.
        Messenger.tell(sender, "Permission '" + args[1] + "' was NOT added to class '" + TextUtils.camelCase(args[0]) + "'.");
        return true;
    }
}
