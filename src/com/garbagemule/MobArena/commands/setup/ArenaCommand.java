package com.garbagemule.MobArena.commands.setup;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.garbagemule.MobArena.*;
import com.garbagemule.MobArena.commands.*;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.framework.ArenaMaster;

@CommandInfo(
    name    = "arena",
    pattern = "arena",
    usage   = "/ma arena",
    desc    = "list the currently selected arena",
    permission = "mobarena.setup.arena"
)
public class ArenaCommand implements Command
{
    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        Messenger.tellPlayer(sender, "Currently selected arena: " + ChatColor.GREEN + am.getSelectedArena().configName());

        StringBuffer buffy = new StringBuffer();
        if (am.getArenas().size() > 1) {
            for (Arena arena : am.getArenas()) {
                if (!arena.equals(am.getSelectedArena())) {
                    buffy.append(arena.configName() + " ");
                }
            }
        }
        else buffy.append(Msg.MISC_NONE);
        
        Messenger.tellPlayer(sender, "Other arenas: " + buffy.toString());
        return true;
    }
}
