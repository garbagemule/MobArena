package com.garbagemule.MobArena.commands.setup;

import org.bukkit.command.CommandSender;

import com.garbagemule.MobArena.*;
import com.garbagemule.MobArena.commands.*;
import com.garbagemule.MobArena.framework.ArenaMaster;

@CommandInfo(
    name    = "expandregion",
    pattern = "expand(region)?",
    usage   = "/ma expandregion <amount> up|down|out",
    desc    = "expand the arena region",
    permission = "mobarena.setup.expandregion"
)
public class ExpandRegionCommand implements Command
{
    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        // Require amount and direction
        if (args.length != 2 || !args[0].matches("(-)?[0-9]+")) return false;
        
        if (!am.getSelectedArena().getRegion().isDefined()) {
            Messenger.tell(sender, "You must first define p1 and p2");
            return true;
        }

        if (args[1].equals("up")) {
            am.getSelectedArena().getRegion().expandUp(Integer.parseInt(args[0]));
        } else if (args[1].equals("down")) {
            am.getSelectedArena().getRegion().expandDown(Integer.parseInt(args[0]));
        } else if (args[1].equals("out")) {
            am.getSelectedArena().getRegion().expandOut(Integer.parseInt(args[0]));
        } else {
            return false;
        }
        
        // In case of a "negative" region, fix it!
        am.getSelectedArena().getRegion().fixRegion();
        
        Messenger.tell(sender, "Region for '" + am.getSelectedArena().configName() + "' expanded " + args[1] + " by " + args[0] + " blocks.");
        am.getSelectedArena().getRegion().save();
        return true;
    }
}
