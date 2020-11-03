package com.garbagemule.MobArena.commands.user;

import com.garbagemule.MobArena.Msg;
import com.garbagemule.MobArena.commands.Command;
import com.garbagemule.MobArena.commands.CommandInfo;
import com.garbagemule.MobArena.commands.Commands;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.framework.ArenaMaster;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(
    name    = "leave",
    pattern = "l|le((.*))?",
    usage   = "/ma leave",
    desc    = "leave the arena",
    permission = "mobarena.use.leave"
)
public class LeaveCommand implements Command
{
    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        if (!Commands.isPlayer(sender)) {
            am.getGlobalMessenger().tell(sender, Msg.MISC_NOT_FROM_CONSOLE);
            return true;
        }

        // Unwrap the sender.
        Player p = Commands.unwrap(sender);

        Arena arena = am.getArenaWithPlayer(p);
        if (arena == null) {
            arena = am.getArenaWithSpectator(p);
            if (arena == null) {
                am.getGlobalMessenger().tell(p, Msg.LEAVE_NOT_PLAYING);
                return true;
            }
        }

        if (arena.playerLeave(p)) {
            arena.getMessenger().tell(p, Msg.LEAVE_PLAYER_LEFT);
        }
        return true;
    }
}
