package com.garbagemule.MobArena.commands.setup;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.garbagemule.MobArena.*;
import com.garbagemule.MobArena.commands.*;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.framework.ArenaMaster;

@CommandInfo(
    name    = "autogenerate",
    pattern = "auto(\\-)?generate",
    usage   = "/ma autogenerate <arena>",
    desc    = "autogenerate a new arena",
    permission = "mobarena.setup.autogenerate"
)
public class AutoGenerateCommand implements Command
{
    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        if (!Commands.isPlayer(sender)) {
            Messenger.tellPlayer(sender, Msg.MISC_NOT_FROM_CONSOLE);
            return false;
        }
        
        // Grab the arguments, if any.
        String arg1 = (args.length > 0 ? args[0] : "");
        
        // Cast the sender.
        Player p = (Player) sender;

        // Require an argument
        if (arg1.equals("")) {
            Messenger.tellPlayer(sender, "Usage: /ma autogenerate <arena>");
            return true;
        }
        
        // Check if arena already exists.
        Arena arena = am.getArenaWithName(arg1);
        if (arena != null) {
            Messenger.tellPlayer(sender, "An arena with that name already exists.");
            return true;
        }
        
        if (!MAUtils.doooooItHippieMonster(p.getLocation(), 13, arg1, am.getPlugin())) {
            Messenger.tellPlayer(sender, "Could not auto-generate arena.");
            return true;
        }
        
        Messenger.tellPlayer(sender, "Arena with name '" + arg1 + "' generated.");
        return true;
    }
}
