package com.garbagemule.MobArena.commands.setup;

import com.garbagemule.MobArena.MAUtils;
import com.garbagemule.MobArena.Msg;
import com.garbagemule.MobArena.commands.Command;
import com.garbagemule.MobArena.commands.CommandInfo;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.framework.ArenaMaster;
import org.bukkit.command.CommandSender;

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
        // Require an arena name
        if (args.length != 1) return false;

        // We have to make sure at least one arena exists before degenerating
        if (am.getArenas().size() < 2) {
            am.getGlobalMessenger().tell(sender, "At least one arena must exist!");
            return true;
        }
        
        // Check if arena exists.
        Arena arena = am.getArenaWithName(args[0]);
        if (arena == null) {
            am.getGlobalMessenger().tell(sender, Msg.ARENA_DOES_NOT_EXIST);
            return true;
        }
        
        if (!MAUtils.undoItHippieMonster(args[0], am.getPlugin(), true)) {
            am.getGlobalMessenger().tell(sender, "Could not degenerate arena.");
            return true;
        }

        am.getGlobalMessenger().tell(sender, "Arena with name '" + args[0] + "' degenerated.");
        return true;
    }
}
