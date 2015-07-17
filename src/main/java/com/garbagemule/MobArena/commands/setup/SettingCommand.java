package com.garbagemule.MobArena.commands.setup;

import com.garbagemule.MobArena.Messenger;
import com.garbagemule.MobArena.commands.Command;
import com.garbagemule.MobArena.commands.CommandInfo;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.framework.ArenaMaster;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Map;

@CommandInfo(
    name    = "setting",
    pattern = "sett(ing)?",
    usage   = "/ma setting <arena> (<setting> (<value>))",
    desc    = "show or change arena settings",
    permission = "mobarena.setup.setting"
)
public class SettingCommand implements Command {
    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        // Require at least an arena
        if (args.length < 1) return false;

        // Find the arena first
        Arena arena = am.getArenaWithName(args[0]);
        if (arena == null) {
            Messenger.tell(sender, "There's no arena with the name '" + args[0] + "'.");
            return true;
        }

        // If we have no more args, just show all settings
        if (args.length == 1) {
            StringBuilder buffy = new StringBuilder();
            buffy.append("Settings for ").append(ChatColor.GREEN).append(args[0]).append(ChatColor.RESET).append(":");
            for (Map.Entry<String,Object> entry : arena.getSettings().getValues(false).entrySet()) {
                buffy.append("\n").append(ChatColor.RESET);
                buffy.append(ChatColor.AQUA).append(entry.getKey()).append(ChatColor.RESET).append(": ");
                buffy.append(ChatColor.YELLOW).append(entry.getValue());
            }
            Messenger.tell(sender, buffy.toString());
            return true;
        }

        // Otherwise, find the setting
        Object val = arena.getSettings().get(args[1], null);
        if (val == null) {
            StringBuilder buffy = new StringBuilder();
            buffy.append(ChatColor.RED).append(" is not a valid setting.");
            buffy.append("Type ").append(ChatColor.YELLOW).append("/ma setting ").append(args[0]);
            buffy.append(ChatColor.RESET).append(" to see all settings.");
            Messenger.tell(sender, buffy.toString());
            return true;
        }

        // If there are no more args, show the value
        if (args.length == 2) {
            StringBuilder buffy = new StringBuilder();
            buffy.append(ChatColor.AQUA).append(args[1]).append(ChatColor.RESET).append(": ");
            buffy.append(ChatColor.YELLOW).append(val);
            Messenger.tell(sender, buffy.toString());
            return true;
        }

        // Otherwise, determine the value of the setting
        if (val instanceof Boolean) {
            if (!args[2].matches("on|off|yes|no|true|false")) {
                Messenger.tell(sender, "Expected a boolean value for that setting");
                return true;
            }
            boolean value = args[2].matches("on|yes|true");
            args[2] = String.valueOf(value);
            arena.getSettings().set(args[1], value);
        } else if (val instanceof Number) {
            try {
                arena.getSettings().set(args[1], Integer.parseInt(args[2]));
            } catch (NumberFormatException e) {
                Messenger.tell(sender, "Expected a numeric value for that setting.");
                return true;
            }
        } else {
            arena.getSettings().set(args[1], args[2]);
        }

        // Save config-file and reload arena
        am.saveConfig();
        am.reloadArena(args[0]);

        // Notify the sender
        StringBuilder buffy = new StringBuilder();
        buffy.append("Setting ").append(ChatColor.AQUA).append(args[1]).append(ChatColor.RESET);
        buffy.append(" for arena ").append(ChatColor.GREEN).append(args[0]).append(ChatColor.RESET);
        buffy.append(" set to ").append(ChatColor.YELLOW).append(args[2]).append(ChatColor.RESET);
        buffy.append("!");
        Messenger.tell(sender, buffy.toString());
        return true;
    }
}
