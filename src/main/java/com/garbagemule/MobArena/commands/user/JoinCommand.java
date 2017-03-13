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
    name    = "join",
    pattern = "j|jo.*|j.*n",
    usage   = "/ma join (<arena>)",
    desc    = "join an arena",
    permission = "mobarena.use.join"
)
public class JoinCommand implements Command
{
    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        if (!Commands.isPlayer(sender)) {
            am.getGlobalMessenger().tell(sender, Msg.MISC_NOT_FROM_CONSOLE);
            return true;
        }
        
        // Unwrap the sender, grab the argument, if any.
        Player p    = Commands.unwrap(sender);
        String arg1 = (args.length > 0 ? args[0] : null);
        
        // Run some rough sanity checks, and grab the arena to join.
        Arena toArena = Commands.getArenaToJoinOrSpec(am, p, arg1);
        if (toArena == null) {
            return true;
        }
        
        // Deny joining from other arenas
        Arena fromArena = am.getArenaWithPlayer(p);
        if (fromArena != null && (fromArena.inArena(p) || fromArena.inLobby(p))) {
            fromArena.getMessenger().tell(p, Msg.JOIN_ALREADY_PLAYING);
            return true;
        }
        
        // Per-arena sanity checks
        if (!toArena.canJoin(p)) {
            return true;
        }

        // Force leave previous arena
        if (fromArena != null) fromArena.playerLeave(p);
        
        // Join the arena!
        return toArena.playerJoin(p, p.getLocation());
    }
}
