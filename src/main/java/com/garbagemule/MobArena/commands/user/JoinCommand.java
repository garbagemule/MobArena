package com.garbagemule.MobArena.commands.user;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.garbagemule.MobArena.*;
import com.garbagemule.MobArena.commands.*;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.framework.ArenaMaster;

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
            Messenger.tell(sender, Msg.MISC_NOT_FROM_CONSOLE);
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
            Messenger.tell(p, Msg.JOIN_ALREADY_PLAYING);
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
