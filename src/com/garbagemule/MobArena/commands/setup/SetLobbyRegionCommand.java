package com.garbagemule.MobArena.commands.setup;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.garbagemule.MobArena.*;
import com.garbagemule.MobArena.commands.*;
import com.garbagemule.MobArena.framework.ArenaMaster;

@CommandInfo(
    name    = "setlobbyregion",
    pattern = "set(lobbyregion|l)",
    usage   = "/ma setlobbyregion l1|l2",
    desc    = "set the lobby region points of an arena",
    permission = "mobarena.setup.setlobbyregion"
)
public class SetLobbyRegionCommand implements Command
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
        
        if (!(arg1.equals("l1") || arg1.equals("l2"))) {
            Messenger.tellPlayer(sender, "Usage: /ma setlobbyregion l1|l2");
            return true;
        }
        
        am.getSelectedArena().getRegion().set(arg1, p.getLocation());
        Messenger.tellPlayer(sender, "Lobby region point " + arg1 + " for arena '" + am.getSelectedArena().configName() + "' set.");
        return true;
    }
}
