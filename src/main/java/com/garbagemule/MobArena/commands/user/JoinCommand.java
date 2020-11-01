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

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
        if (toArena == null || !canJoin(p, toArena)) {
            return true;
        }

        // Join the arena!
        int seconds = toArena.getSettings().getInt("join-interrupt-timer", 0);
        if (seconds > 0) {
            boolean started = am.getJoinInterruptTimer().start(p, toArena, seconds, () -> tryJoin(am, p, toArena));
            if (started) {
                toArena.getMessenger().tell(p, Msg.JOIN_AFTER_DELAY, String.valueOf(seconds));
            } else {
                toArena.getMessenger().tell(p, Msg.JOIN_ALREADY_PLAYING);
            }
        } else {
            tryJoin(am, p, toArena);
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

    private void tryJoin(ArenaMaster am, Player player, Arena arena) {
        // If the player is currently in an arena, leave it first
        Arena current = am.getArenaWithPlayer(player);
        if (current != null) {
            if (current.inArena(player) || current.inLobby(player)) {
                current.getMessenger().tell(player, Msg.JOIN_ALREADY_PLAYING);
                return;
            }
            if (!current.playerLeave(player)) {
                return;
            }
        }
        if (canJoin(player, arena)) {
            arena.playerJoin(player, player.getLocation());
        }
    }

    @Override
    public List<String> tab(ArenaMaster am, Player player, String... args) {
        if (args.length > 1) {
            return Collections.emptyList();
        }

        String prefix = args[0].toLowerCase();

        List<Arena> arenas = am.getPermittedArenas(player);

        return arenas.stream()
            .filter(Arena::isEnabled)
            .filter(arena -> arena.getSlug().startsWith(prefix))
            .filter(arena -> arena.getRegion().isSetup())
            .map(Arena::getSlug)
            .collect(Collectors.toList());
    }
}
