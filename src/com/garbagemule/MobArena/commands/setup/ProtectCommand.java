package com.garbagemule.MobArena.commands.setup;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.garbagemule.MobArena.*;
import com.garbagemule.MobArena.commands.*;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.framework.ArenaMaster;

@CommandInfo(
    name    = "protect",
    pattern = "protect",
    usage   = "/ma protect (<arena>) (true|false)",
    desc    = "set the protection of an arena",
    permission = "mobarena.setup.protect"
)
public class ProtectCommand implements Command
{
    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        // Grab the argument, if any.
        String arg1 = (args.length > 0 ? args[0] : "");
        String arg2 = (args.length > 1 ? args[1] : "");
        
        Arena arena;
        
        // No arguments
        if (arg1.equals("")) {
            arena = am.getSelectedArena();
            arena.setProtected(!arena.isProtected());
        }
        
        // One argument
        else if (arg2.equals("")) {
            // true/false
            if (arg1.matches("true|on") || arg1.matches("false|off")) {
                arena = am.getSelectedArena();
                arena.setProtected(arg1.matches("true|on"));
            }
            // Arena name
            else {
                arena = am.getArenaWithName(arg1);
                if (arena == null) {
                    Messenger.tellPlayer(sender, "There is no arena with that name.");
                    Messenger.tellPlayer(sender, "Usage: /ma protect (true|false)");
                    Messenger.tellPlayer(sender, "    or /ma protect <arena> (true|false)");
                    return true;
                }
                arena.setProtected(!arena.isProtected());
            }
        }
        
        // Two arguments
        else  {
            if (!(arg2.matches("true|on") || arg2.matches("false|off"))) {
                Messenger.tellPlayer(sender, "Usage: /ma protect (true|false)");
                Messenger.tellPlayer(sender, "    or /ma protect <arena name> (true|false)");
                return true;
            }
            arena = am.getArenaWithName(arg1);
            if (arena == null) {
                Messenger.tellPlayer(sender, "There is no arena with that name.");
                Messenger.tellPlayer(sender, "Usage: /ma protect (true|false)");
                Messenger.tellPlayer(sender, "    or /ma protect <arena name> (true|false)");
                return true;
            }
            arena.setProtected(arg2.equals("true"));
        }
        
        arena.getSettings().getParent().save();
        Messenger.tellPlayer(sender, "Protection for arena '" + arena.configName() + "': " + ((arena.isProtected()) ? ChatColor.GREEN + "on" : ChatColor.RED + "off")); 
        return true;
    }
}
