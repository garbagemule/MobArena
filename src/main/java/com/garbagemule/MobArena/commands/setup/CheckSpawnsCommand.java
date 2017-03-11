package com.garbagemule.MobArena.commands.setup;

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
    usage   = "/ma checkspawns <arena>",
    desc    = "show spawnpoints that cover your location",
    permission = "mobarena.setup.checkspawns"
)
public class CheckSpawnsCommand implements Command
{
    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        if (!Commands.isPlayer(sender)) {
            am.getGlobalMessenger().tell(sender, Msg.MISC_NOT_FROM_CONSOLE);
            return true;
        }

        Arena arena;
        if (args.length == 0) {
            if (am.getArenas().size() > 1) {
                am.getGlobalMessenger().tell(sender, "There are multiple arenas.");
                return true;
            } else {
                arena = am.getArenas().get(0);
            }
        } else {
            arena = am.getArenaWithName(args[0]);
            if (arena == null) {
                am.getGlobalMessenger().tell(sender, "There is no arena named " + args[0]);
                return true;
            }
        }

        if (arena.getRegion().getSpawnpoints().isEmpty()) {
            am.getGlobalMessenger().tell(sender, "There are no spawnpoints in the selected arena.");
            return true;
        }
        Player p = Commands.unwrap(sender);
        arena.getRegion().checkSpawns(p);
        return true;
    }
}
