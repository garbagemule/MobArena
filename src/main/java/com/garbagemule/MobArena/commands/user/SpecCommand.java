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
    name    = "spec",
    pattern = "s|spec.*",
    usage   = "/ma spec (<arena>)",
    desc    = "spec an arena",
    permission = "mobarena.use.spec"
)
public class SpecCommand implements Command
{
    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        if (!Commands.isPlayer(sender)) {
            am.getGlobalMessenger().tell(sender, Msg.MISC_NOT_FROM_CONSOLE);
            return false;
        }
        
        // Unwrap the sender, grab the argument, if any.
        Player p    = Commands.unwrap(sender);
        String arg1 = (args.length > 0 ? args[0] : null);
        
        // Run some rough sanity checks, and grab the arena to spec.
        Arena toArena = Commands.getArenaToJoinOrSpec(am, p, arg1);
        if (toArena == null || !canSpec(p, toArena)) {
            return true;
        }

        // Spec the arena!
        int seconds = toArena.getSettings().getInt("join-interrupt-timer", 0);
        if (seconds > 0) {
            boolean started = am.getJoinInterruptTimer().start(p, toArena, seconds, () -> trySpec(p, toArena));
            if (started) {
                toArena.getMessenger().tell(p, Msg.JOIN_AFTER_DELAY, String.valueOf(seconds));
            } else {
                toArena.getMessenger().tell(p, Msg.SPEC_ALREADY_PLAYING);
            }
        } else {
            trySpec(p, toArena);
        }
        return true;
    }

    private boolean canSpec(Player player, Arena arena) {
        MobArena plugin = arena.getPlugin();
        ArenaMaster am = plugin.getArenaMaster();
        if (am.getJoinInterruptTimer().isWaiting(player)) {
            plugin.getGlobalMessenger().tell(player, Msg.SPEC_ALREADY_PLAYING);
            return false;
        }
        Arena current = arena.getPlugin().getArenaMaster().getArenaWithPlayer(player);
        if (current != null) {
            current.getMessenger().tell(player, Msg.SPEC_ALREADY_PLAYING);
            return false;
        }
        return arena.canSpec(player);
    }

    private void trySpec(Player player, Arena arena) {
        if (canSpec(player, arena)) {
            arena.playerSpec(player, player.getLocation());
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
            .filter(arena -> arena.configName().toLowerCase().startsWith(prefix))
            .filter(arena -> arena.getRegion().isSetup())
            .map(Arena::configName)
            .collect(Collectors.toList());
    }
}
