package com.garbagemule.MobArena.commands.setup;

import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.command.CommandSender;

import com.garbagemule.MobArena.*;
import com.garbagemule.MobArena.commands.*;
import com.garbagemule.MobArena.framework.ArenaMaster;
import com.garbagemule.MobArena.util.TextUtils;

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
        // Grab the argument, if any.
        String arg1 = (args.length > 0 ? args[0] : "");
        
        // Require an argument.
        if (arg1.equals("")) {
            Messenger.tellPlayer(sender, "Usage: /ma listclassperms <classname>");
            return true;
        }
        
        ArenaClass arenaClass = am.getClasses().get(arg1);
        String className = TextUtils.camelCase(arg1);
        
        if (arenaClass == null) {
            Messenger.tellPlayer(sender, "The class '" + className + "' does not exist.");
            return true;
        }
        
        Messenger.tellPlayer(sender, "Permissions for '" + className + "':");
        Map<String,Boolean> perms = arenaClass.getPermissions();
        if (perms.isEmpty()) {
            Messenger.tellPlayer(sender, "<none>");
            return true;
        }
        
        for (Entry<String,Boolean> entry : arenaClass.getPermissions().entrySet()) {
            String perm = entry.getKey();
            if (!entry.getValue()) {
                perm = "^" + perm;
            }
            Messenger.tellPlayer(sender, "- " + perm);
        }
        return true;
    }
}
