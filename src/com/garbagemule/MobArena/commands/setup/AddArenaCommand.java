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
            Messenger.tellPlayer(sender, Msg.MISC_NOT_FROM_CONSOLE);
            return false;
        }
        
        // Grab the argument, if any.
        String arg1 = (args.length > 0 ? args[0] : "");
        
        // Cast the sender.
        Player p = (Player) sender;

        // Require an argument
        if (arg1.equals("")) {
            Messenger.tellPlayer(sender, "Usage: /ma addarena <arena>");
            return true;
        }
        
        Arena arena = am.getArenaWithName(arg1);
        if (arena != null) {
            Messenger.tellPlayer(sender, "An arena with that name already exists.");
            return true;
        }
        
        arena = am.createArenaNode(arg1, p.getWorld());
        am.setSelectedArena(arena);
        
        Messenger.tellPlayer(sender, "New arena with name '" + arg1 + "' created!");
        return true;
    }
}
