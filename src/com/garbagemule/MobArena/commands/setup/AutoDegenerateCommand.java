package com.garbagemule.MobArena.commands.setup;

import org.bukkit.command.CommandSender;

import com.garbagemule.MobArena.*;
import com.garbagemule.MobArena.commands.*;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.framework.ArenaMaster;

@CommandInfo(
    name    = "autodegenerate",
    pattern = "auto(\\-)?degenerate",
    usage   = "/ma autodegenerate <arena>",
    desc    = "autodegenerate an existing arena",
    permission = "mobarena.setup.autodegenerate"
)
public class AutoDegenerateCommand implements Command
{
    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        // Grab the arguments, if any.
        String arg1 = (args.length > 0 ? args[0] : "");

        // Require an argument
        if (arg1.equals("")) {
            Messenger.tellPlayer(sender, "Usage: /ma autodegenerate <arena>");
            return true;
        }
        
        if (am.getArenas().size() < 2) {
            Messenger.tellPlayer(sender, "At least one arena must exist!");
            return true;
        }
        
        // Check if arena exists.
        Arena arena = am.getArenaWithName(arg1);
        if (arena == null) {
            Messenger.tellPlayer(sender, Msg.ARENA_DOES_NOT_EXIST);
            return true;
        }
        
        if (!MAUtils.undoItHippieMonster(arg1, am.getPlugin(), true)) {
            Messenger.tellPlayer(sender, "Could not degenerate arena.");
            return true;
        }
        
        Messenger.tellPlayer(sender, "Arena with name '" + arg1 + "' degenerated.");
        return true;
    }
}
