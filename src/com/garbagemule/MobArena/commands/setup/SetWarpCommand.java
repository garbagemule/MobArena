package com.garbagemule.MobArena.commands.setup;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.garbagemule.MobArena.*;
import com.garbagemule.MobArena.commands.*;
import com.garbagemule.MobArena.framework.ArenaMaster;

@CommandInfo(
    name    = "setwarp",
    pattern = "set(warp|point)",
    usage   = "/ma setwarp arena|lobby|spectator",
    desc    = "set a warp point for an arena",
    permission = "mobarena.setup.setwarp"
)
public class SetWarpCommand implements Command
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
        
        if (!(arg1.equals("arena") || arg1.equals("lobby") || arg1.equals("spectator"))) {
            Messenger.tellPlayer(sender, "Usage: /ma setwarp arena|lobby|spectator");
            return true;
        }
        
        am.getSelectedArena().getRegion().set(arg1, p.getLocation());
        Messenger.tellPlayer(sender, "Warp point " + arg1 + " was set for arena '" + am.getSelectedArena().configName() + "'");
        Messenger.tellPlayer(sender, "Type /ma checkdata to see if you're missing anything...");
        return true;
    }
}
