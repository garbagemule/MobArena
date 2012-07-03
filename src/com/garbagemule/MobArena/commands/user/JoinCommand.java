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
            Messenger.tellPlayer(sender, Msg.MISC_NOT_FROM_CONSOLE);
            return false;
        }
        
        // Cast the sender, grab the argument, if any.
        Player p    = (Player) sender;
        String arg1 = (args.length > 0 ? args[0] : null);
        
        // Run some rough sanity checks, and grab the arena to join.
        Arena toArena = Commands.getArenaToJoinOrSpec(am, p, arg1);
        Arena fromArena = am.getArenaWithPlayer(p);
        if (toArena == null) {
            return false;
        }
        
        // Deny joining from other arenas
        if (fromArena != null && (fromArena.inArena(p) || fromArena.inLobby(p))) {
            Messenger.tellPlayer(p, Msg.JOIN_ALREADY_PLAYING);
            return false;
        }
        
        // Per-arena sanity checks
        if (!toArena.canJoin(p)) {
            return false;
        }
        
        // Join the arena!
        return toArena.playerJoin(p, p.getLocation());
    }
}
