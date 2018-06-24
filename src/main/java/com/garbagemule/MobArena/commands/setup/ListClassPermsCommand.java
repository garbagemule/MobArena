package com.garbagemule.MobArena.commands.setup;

import com.garbagemule.MobArena.ArenaClass;
import com.garbagemule.MobArena.commands.Command;
import com.garbagemule.MobArena.commands.CommandInfo;
import com.garbagemule.MobArena.framework.ArenaMaster;
import com.garbagemule.MobArena.things.Thing;
import com.garbagemule.MobArena.util.TextUtils;
import org.bukkit.command.CommandSender;

import java.util.List;

@CommandInfo(
    name    = "listclassperms",
    pattern = "(list)?classperm(.*)s",
    usage   = "/ma listclassperms <classname>",
    desc    = "list per-class permissions",
    permission = "mobarena.setup.classes"
)
public class ListClassPermsCommand implements Command
{
    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        // Require a class name
        if (args.length != 1) return false;
        
        ArenaClass arenaClass = am.getClasses().get(args[0]);
        String className = TextUtils.camelCase(args[0]);
        
        if (arenaClass == null) {
            am.getGlobalMessenger().tell(sender, "The class '" + className + "' does not exist.");
            return true;
        }
        
        am.getGlobalMessenger().tell(sender, "Permissions for '" + className + "':");
        List<Thing> perms = arenaClass.getPermissions();
        if (perms.isEmpty()) {
            am.getGlobalMessenger().tell(sender, "<none>");
            return true;
        }
        
        for (Thing perm : perms) {
            am.getGlobalMessenger().tell(sender, perm.toString());
        }
        return true;
    }
}
