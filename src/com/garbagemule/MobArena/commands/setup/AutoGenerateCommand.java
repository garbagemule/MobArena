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
            Messenger.tell(sender, Msg.MISC_NOT_FROM_CONSOLE);
            return true;
        }

        // Require an arena name
        if (args.length != 1) return false;
        
        // Cast the sender.
        Player p = (Player) sender;
        
        // Check if arena already exists.
        Arena arena = am.getArenaWithName(args[0]);
        if (arena != null) {
            Messenger.tell(sender, "An arena with that name already exists.");
            return true;
        }
        
        if (!MAUtils.doooooItHippieMonster(p.getLocation(), 13, args[0], am.getPlugin())) {
            Messenger.tell(sender, "Could not auto-generate arena.");
            return true;
        }
        
        Messenger.tell(sender, "Arena with name '" + args[0] + "' generated.");
        return true;
    }
}
