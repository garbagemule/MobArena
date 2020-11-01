package com.garbagemule.MobArena.commands.setup;

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
    name    = "editarena",
    pattern = "edit(arena)?",
    usage   = "/ma editarena <arena> (true|false)",
    desc    = "set edit mode of an arena",
    permission = "mobarena.setup.editarena"
)
public class EditArenaCommand implements Command
{
    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        boolean value;
        Arena arena;
        if (args.length == 0) {
            if (am.getArenas().size() > 1) {
                am.getGlobalMessenger().tell(sender, "There are multiple arenas.");
                return true;
            }
            arena = am.getArenas().get(0);
            value = !arena.inEditMode();
        } else if (args.length == 1) {
            if (args[0].matches("on|off|true|false")) {
                if (am.getArenas().size() > 1) {
                    am.getGlobalMessenger().tell(sender, "There are multiple arenas.");
                    return true;
                }
                arena = am.getArenas().get(0);
                value = args[0].matches("on|true");
            } else {
                arena = am.getArenaWithName(args[0]);
                if (arena == null) {
                    am.getGlobalMessenger().tell(sender, "There is no arena named " + args[0]);
                    return true;
                }
                value = !arena.inEditMode();
            }
        } else {
            arena = am.getArenaWithName(args[0]);
            value = args[1].matches("on|true");
        }
        arena.setEditMode(value);
        am.getGlobalMessenger().tell(sender, "Edit mode for arena '" + arena.configName() + "': " + ((arena.inEditMode()) ? ChatColor.GREEN + "true" : ChatColor.RED + "false"));
        if (arena.inEditMode()) am.getGlobalMessenger().tell(sender, "Remember to turn it back off after editing!");
        return true;
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
