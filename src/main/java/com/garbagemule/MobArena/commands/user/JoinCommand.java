package com.garbagemule.MobArena.commands.user;

import com.garbagemule.MobArena.MobArena;
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
        
        // If the player is currently in an arena, leave it first
        Arena current = am.getArenaWithPlayer(p);
        if (current != null) {
            if (current.inArena(p) || current.inLobby(p)) {
                current.getMessenger().tell(p, Msg.JOIN_ALREADY_PLAYING);
                return true;
            }
            if (!current.playerLeave(p)) {
                return true;
            }
        }

        // Run some rough sanity checks, and grab the arena to join.
        Arena toArena = Commands.getArenaToJoinOrSpec(am, p, arg1);
        if (toArena == null || !canJoin(p, toArena)) {
            return true;
        }
        
        // Join the arena!
        int seconds = toArena.getSettings().getInt("join-interrupt-timer", 0);
        if (seconds > 0) {
            boolean started = am.getJoinInterruptTimer().start(p, toArena, seconds, () -> tryJoin(p, toArena));
            if (started) {
                toArena.getMessenger().tell(p, Msg.JOIN_AFTER_DELAY, String.valueOf(seconds));
            } else {
                toArena.getMessenger().tell(p, Msg.JOIN_ALREADY_PLAYING);
            }
        } else {
            tryJoin(p, toArena);
        }
        return true;
    }

    private boolean canJoin(Player player, Arena arena) {
        MobArena plugin = arena.getPlugin();
        ArenaMaster am = plugin.getArenaMaster();
        if (am.getJoinInterruptTimer().isWaiting(player)) {
            plugin.getGlobalMessenger().tell(player, Msg.JOIN_ALREADY_PLAYING);
            return false;
        }
        return arena.canJoin(player);
    }

    private void tryJoin(Player player, Arena arena) {
        if (canJoin(player, arena)) {
            arena.playerJoin(player, player.getLocation());
        }
    }
}
