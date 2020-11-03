package com.garbagemule.MobArena.commands.admin;

import com.garbagemule.MobArena.Msg;
import com.garbagemule.MobArena.commands.Command;
import com.garbagemule.MobArena.commands.CommandInfo;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.framework.ArenaMaster;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@CommandInfo(
    name    = "disable",
    pattern = "disable|off",
    usage   = "/ma disable (<arena>|all)",
    desc    = "disable MobArena or individual arenas",
    permission = "mobarena.admin.enable"
)
public class DisableCommand implements Command
{
    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        // Grab the argument, if any.
        String arg1 = (args.length > 0 ? args[0] : "");

        if (arg1.equals("all")) {
            for (Arena arena : am.getArenas()) {
                disable(arena, sender);
            }
            return true;
        }

        if (!arg1.equals("")) {
            Arena arena = am.getArenaWithName(arg1);
            if (arena == null) {
                am.getGlobalMessenger().tell(sender, Msg.ARENA_DOES_NOT_EXIST);
                return true;
            }
            disable(arena, sender);
            return true;
        }

        am.setEnabled(false);
        am.saveConfig();
        am.getGlobalMessenger().tell(sender, "MobArena " + ChatColor.RED + "disabled");
        return true;
    }

    private void disable(Arena arena, CommandSender sender) {
        arena.setEnabled(false);
        arena.getPlugin().saveConfig();
        arena.getGlobalMessenger().tell(sender, "Arena '" + arena.configName() + "' " + ChatColor.RED + "disabled");
    }

    @Override
    public List<String> tab(ArenaMaster am, Player player, String... args) {
        if (args.length > 1) {
            return Collections.emptyList();
        }

        String prefix = args[0].toLowerCase();

        List<Arena> arenas = am.getArenas();

        return arenas.stream()
            .filter(arena -> arena.getSlug().startsWith(prefix))
            .map(Arena::getSlug)
            .collect(Collectors.toList());
    }
}
