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
    usage   = "/ma editarena <arena> (true|false)",
    desc    = "set edit mode of an arena",
    permission = "mobarena.setup.editarena"
)
public class EditArenaCommand implements Command
{
    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        boolean value;
        Arena arena;
        if (args.length == 0) {
            if (am.getArenas().size() > 1) {
                am.getGlobalMessenger().tell(sender, "There are multiple arenas.");
                return true;
            }
            arena = am.getArenas().get(0);
            value = !arena.inEditMode();
        } else if (args.length == 1) {
            if (args[0].matches("on|off|true|false")) {
                if (am.getArenas().size() > 1) {
                    am.getGlobalMessenger().tell(sender, "There are multiple arenas.");
                    return true;
                }
                arena = am.getArenas().get(0);
                value = args[0].matches("on|true");
            } else {
                arena = am.getArenaWithName(args[0]);
                if (arena == null) {
                    am.getGlobalMessenger().tell(sender, "There is no arena named " + args[0]);
                    return true;
                }
                value = !arena.inEditMode();
            }
        } else {
            arena = am.getArenaWithName(args[0]);
            value = args[1].matches("on|true");
        }
        arena.setEditMode(value);
        am.getGlobalMessenger().tell(sender, "Edit mode for arena '" + arena.configName() + "': " + ((arena.inEditMode()) ? ChatColor.GREEN + "true" : ChatColor.RED + "false"));
        if (arena.inEditMode()) am.getGlobalMessenger().tell(sender, "Remember to turn it back off after editing!");
        return true;
    }
}
