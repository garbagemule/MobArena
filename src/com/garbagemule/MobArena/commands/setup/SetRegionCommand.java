package com.garbagemule.MobArena.commands.setup;

import com.garbagemule.MobArena.framework.Arena;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.garbagemule.MobArena.*;
import com.garbagemule.MobArena.commands.*;
import com.garbagemule.MobArena.framework.ArenaMaster;

@CommandInfo(
    name    = "setregion",
    pattern = "set(region|p)",
    usage   = "/ma setregion p1|p2",
    desc    = "set the region points of an arena",
    permission = "mobarena.setup.setregion"
)
public class SetRegionCommand implements Command
{
    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        if (!Commands.isPlayer(sender)) {
            Messenger.tell(sender, Msg.MISC_NOT_FROM_CONSOLE);
            return true;
        }

        // Require a region point
        if (args.length != 1 || !args[0].matches("p1|p2")) return false;
        
        // Cast the sender.
        Player p = (Player) sender;

        Arena arena = am.getSelectedArena();
        World aw = arena.getWorld();
        World pw = p.getLocation().getWorld();

        if (!aw.getName().equals(pw.getName())) {
            String msg = String.format("Changing world of arena '%s' from '%s' to '%s'", arena.configName(), aw.getName(), pw.getName());
            Messenger.tell(sender, msg);
        }

        arena.setWorld(p.getWorld());
        arena.getRegion().set(args[0], p.getLocation());
        Messenger.tell(sender, "Region point " + args[0] + " for arena '" + am.getSelectedArena().configName() + "' set.");
        arena.getRegion().checkData(am.getPlugin(), sender, true, true, false, false);
        return true;
    }
}
