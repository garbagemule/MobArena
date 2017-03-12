package com.garbagemule.MobArena.commands.setup;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.garbagemule.MobArena.*;
import com.garbagemule.MobArena.commands.*;
import com.garbagemule.MobArena.framework.ArenaMaster;

@CommandInfo(
    name    = "config",
    pattern = "config|cfg",
    usage   = "/ma config reload|save",
    desc    = "reload or save the config-file",
    permission = "mobarena.setup.config"
)
public class ConfigCommand implements Command
{
    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        // Require reload/save
        if (args.length != 1) return false;

        if (args[0].equals("reload")) {
            try {
                am.reloadConfig();
                am.getGlobalMessenger().tell(sender, "Config reloaded.");
            } catch (Exception e) {
                am.getGlobalMessenger().tell(sender, ChatColor.RED + "ERROR:" + ChatColor.RESET + "\n" + e.getMessage());
                am.getGlobalMessenger().tell(sender, "MobArena has been " + ChatColor.RED + "disabled" + ChatColor.RESET + ".");
                am.getGlobalMessenger().tell(sender, "Fix the config-file, then reload it again, and then type " + ChatColor.YELLOW + "/ma enable" + ChatColor.RESET + " to re-enable MobArena.");
            }
        } else if (args[0].equals("save")) {
            am.saveConfig();
            am.getGlobalMessenger().tell(sender, "Config saved.");
        } else {
            return false;
        }
        return true;
    }
}
