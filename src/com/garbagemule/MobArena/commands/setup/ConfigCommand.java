package com.garbagemule.MobArena.commands.setup;

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
        // Grab the argument, if any.
        String arg1 = (args.length > 0 ? args[0] : "");
        
        if (arg1.equals("reload")) {
            am.reloadConfig();
            Messenger.tell(sender, "Config reloaded.");
            return true;
        }
        
        if (arg1.equals("save")) {
            am.saveConfig();
            Messenger.tell(sender, "Config saved.");
            return true;
        }

        // Requires an argument.
        Messenger.tell(sender, "Usage: /ma config reload|save");
        return false;
    }
}
