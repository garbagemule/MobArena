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
    name    = "checkspawns",
    pattern = "checkspawn(point)?s",
    usage   = "/ma checkspawns (<arena>)",
    desc    = "show spawnpoints that cover your location",
    permission = "mobarena.setup.checkspawns"
)
public class CheckSpawnsCommand implements Command
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

            if (arena.getRegion().getSpawnpoints().isEmpty()) {
                Messenger.tellPlayer(sender, "There are no spawnpoints in the selected arena.");
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

        arena.getRegion().checkSpawns(p);
        
        return true;
    }
}
