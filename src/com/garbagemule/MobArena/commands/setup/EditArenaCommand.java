package com.garbagemule.MobArena.commands.setup;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.garbagemule.MobArena.*;
import com.garbagemule.MobArena.commands.*;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.framework.ArenaMaster;

@CommandInfo(
    name    = "editarena",
    pattern = "edit(arena)?",
    usage   = "/ma editarena (<arena>) (true|false)",
    desc    = "set edit mode of an arena",
    permission = "mobarena.setup.editarena"
)
public class EditArenaCommand implements Command
{
    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        // Grab the argument, if any.
        String arg1 = (args.length > 0 ? args[0] : "");
        String arg2 = (args.length > 1 ? args[1] : "");

        Arena arena;
        
        // No arguments.
        if (arg1.equals("")) {
            arena = am.getSelectedArena();
            arena.setEditMode(!arena.inEditMode());
        }
        
        // One argument.
        else if (arg2.equals("")) {
            // Argument is [true|false]
            if (arg1.matches("true|on") || arg1.matches("false|off")) {
                arena = am.getSelectedArena();
                arena.setEditMode(arg1.equals("true"));
            }
            // Argument is <arena name>
            else {
                arena = am.getArenaWithName(arg1);
                if (arena == null) {
                    Messenger.tellPlayer(sender, "There is no arena with that name.");
                    Messenger.tellPlayer(sender, "Usage: /ma editarena (true|false)");
                    Messenger.tellPlayer(sender, "    or /ma editarena <arena> (true|false)");
                    return true;
                }
                arena.setEditMode(!arena.inEditMode());
            }
        }
        
        // Two arguments
        else {
            if (!(arg2.matches("true|on") || arg2.matches("false|off"))) {
                Messenger.tellPlayer(sender, "Usage: /ma editarena (true|false)");
                Messenger.tellPlayer(sender, "    or /ma editarena <arena> (true|false)");
                return true;
            }
            arena = am.getArenaWithName(arg1);
            if (arena == null) {
                Messenger.tellPlayer(sender, "There is no arena with that name.");
                Messenger.tellPlayer(sender, "Usage: /ma editarena (true|false)");
                Messenger.tellPlayer(sender, "    or /ma editarena <arena> (true|false)");
                return true;
            }
            arena.setEditMode(arg2.equals("true"));
        }
        
        Messenger.tellPlayer(sender, "Edit mode for arena '" + arena.configName() + "': " + ((arena.inEditMode()) ? ChatColor.GREEN + "true" : ChatColor.RED + "false"));
        if (arena.inEditMode()) Messenger.tellPlayer(sender, "Remember to turn it back off after editing!"); 
        return true;
    }
}
