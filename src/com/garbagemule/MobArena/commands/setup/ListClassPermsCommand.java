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
        // Require a class name
        if (args.length != 1) return false;
        
        ArenaClass arenaClass = am.getClasses().get(args[0]);
        String className = TextUtils.camelCase(args[0]);
        
        if (arenaClass == null) {
            Messenger.tell(sender, "The class '" + className + "' does not exist.");
            return true;
        }
        
        Messenger.tell(sender, "Permissions for '" + className + "':");
        Map<String,Boolean> perms = arenaClass.getPermissions();
        if (perms.isEmpty()) {
            Messenger.tell(sender, "<none>");
            return true;
        }
        
        for (Entry<String,Boolean> entry : arenaClass.getPermissions().entrySet()) {
            String perm = entry.getKey();
            if (!entry.getValue()) {
                perm = "^" + perm;
            }
            Messenger.tell(sender, "- " + perm);
        }
        return true;
    }
}
