package com.garbagemule.MobArena.commands.setup;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.garbagemule.MobArena.*;
import com.garbagemule.MobArena.commands.*;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.framework.ArenaMaster;

@CommandInfo(
    name    = "showregion",
    pattern = "show(region)?",
    usage   = "/ma showregion (<arena>)",
    desc    = "show an arena region",
    permission = "mobarena.setup.showregion"
)
public class ShowRegionCommand implements Command
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
        
        Arena arena;
        
        if (arg1.equals("")) {
            arena = am.getArenaAtLocation(p.getLocation());
            if (arena == null) {
                arena = am.getSelectedArena();
            }
            
            if (!arena.getRegion().isDefined()) {
                Messenger.tellPlayer(sender, "The region is not defined for the selected arena.");
                return false;
            }
        }
        else {
            arena = am.getArenaWithName(arg1);
            
            if (arena == null) {
                Messenger.tellPlayer(sender, Msg.ARENA_DOES_NOT_EXIST);
                return false;
            }
        }
        
        arena.getRegion().showRegion(p);
        
        return true;
    }
}
