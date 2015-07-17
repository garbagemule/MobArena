package com.garbagemule.MobArena.commands.setup;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.garbagemule.MobArena.*;
import com.garbagemule.MobArena.commands.*;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.framework.ArenaMaster;

@CommandInfo(
    name    = "addarena",
    pattern = "(add|new)arena",
    usage   = "/ma addarena <arena>",
    desc    = "add a new arena",
    permission = "mobarena.setup.addarena"
)
public class AddArenaCommand implements Command
{
    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        if (!Commands.isPlayer(sender)) {
            Messenger.tell(sender, Msg.MISC_NOT_FROM_CONSOLE);
            return true;
        }

        // Require an arena name
        if (args.length != 1) return false;
        
        // Unwrap the sender.
        Player p = Commands.unwrap(sender);
        
        Arena arena = am.getArenaWithName(args[0]);
        if (arena != null) {
            Messenger.tell(sender, "An arena with that name already exists.");
            return true;
        }
        am.createArenaNode(args[0], p.getWorld());
        Messenger.tell(sender, "New arena with name '" + args[0] + "' created!");
        return true;
    }
}
