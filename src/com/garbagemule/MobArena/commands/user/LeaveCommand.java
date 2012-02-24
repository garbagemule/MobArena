package com.garbagemule.MobArena.commands.user;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.garbagemule.MobArena.*;
import com.garbagemule.MobArena.commands.*;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.framework.ArenaMaster;

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
            Messenger.tellPlayer(sender, Msg.MISC_NOT_FROM_CONSOLE);
            return false;
        }
        
        // Cast the sender.
        Player p = (Player) sender;

        Arena arena = am.getArenaWithPlayer(p);  
        if (arena == null) {
            arena = am.getArenaWithSpectator(p);
            if (arena == null) {
                Messenger.tellPlayer(p, Msg.LEAVE_NOT_PLAYING);
                return false;
            }
        }
        
        if (arena.playerLeave(p)) {
            Messenger.tellPlayer(p, Msg.LEAVE_PLAYER_LEFT);
        }
        return true;
    }
}
