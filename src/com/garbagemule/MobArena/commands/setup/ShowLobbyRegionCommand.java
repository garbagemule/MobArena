package com.garbagemule.MobArena.commands.setup;

import com.garbagemule.MobArena.Messenger;
import com.garbagemule.MobArena.Msg;
import com.garbagemule.MobArena.commands.Command;
import com.garbagemule.MobArena.commands.CommandInfo;
import com.garbagemule.MobArena.commands.Commands;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.framework.ArenaMaster;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(
    name    = "showlobbyregion",
    pattern = "showlobbyregion",
    usage   = "/ma showlobbyregion (<arena>)",
    desc    = "show a lobby region",
    permission = "mobarena.setup.showlobbyregion"
)
public class ShowLobbyRegionCommand implements Command
{
    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        if (!Commands.isPlayer(sender)) {
            Messenger.tell(sender, Msg.MISC_NOT_FROM_CONSOLE);
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
        }
        else {
            arena = am.getArenaWithName(arg1);
            
            if (arena == null) {
                Messenger.tell(sender, Msg.ARENA_DOES_NOT_EXIST);
                return false;
            }
        }

        if (!arena.getRegion().isLobbyDefined()) {
            Messenger.tell(sender, "The lobby region is not defined for the selected arena.");
            return false;
        }
        
        // Show an error message if we aren't in the right world
        if (!arena.getWorld().getName().equals(arena.getWorld().getName())) {
            Messenger.tell(sender, "Arena '" + arena.configName() +
                    "' is in world '" + arena.getWorld().getName() +
                    "' and you are in world '" + p.getWorld().getName() + "'");
            return false;
        }
        
        arena.getRegion().showLobbyRegion(p);
        
        return true;
    }
}
