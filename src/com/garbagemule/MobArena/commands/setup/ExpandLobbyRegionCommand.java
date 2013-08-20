package com.garbagemule.MobArena.commands.setup;

import org.bukkit.command.CommandSender;

import com.garbagemule.MobArena.*;
import com.garbagemule.MobArena.commands.*;
import com.garbagemule.MobArena.framework.ArenaMaster;

@CommandInfo(
    name    = "expandlobbyregion",
    pattern = "expandlobby(region)?",
    usage   = "/ma expandlobbyregion <amount> up|down|out",
    desc    = "expand the arena lobby region",
    permission = "mobarena.setup.expandlobbyregion"
)
public class ExpandLobbyRegionCommand implements Command
{
    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        // Require amount and direction
        if (args.length != 2 || !args[0].matches("(-)?[0-9]+")) return false;
        
        if (!am.getSelectedArena().getRegion().isLobbyDefined()) {
            Messenger.tell(sender, "You must first define l1 and l2");
            return true;
        }
        
        if (args[1].equals("up")) {
            am.getSelectedArena().getRegion().expandLobbyUp(Integer.parseInt(args[0]));
        } else if (args[1].equals("down")) {
            am.getSelectedArena().getRegion().expandLobbyDown(Integer.parseInt(args[0]));
        } else if (args[1].equals("out")) {
            am.getSelectedArena().getRegion().expandLobbyOut(Integer.parseInt(args[0]));
        } else {
            return false;
        }
        
        // In case of a "negative" region, fix it!
        am.getSelectedArena().getRegion().fixLobbyRegion();
        
        Messenger.tell(sender, "Lobby region for '" + am.getSelectedArena().configName() + "' expanded " + args[1] + " by " + args[0] + " blocks.");
        am.getSelectedArena().getRegion().save();
        return true;
    }
}
