package com.garbagemule.MobArena.commands.setup;

import java.util.Set;

import org.bukkit.command.CommandSender;

import com.garbagemule.MobArena.*;
import com.garbagemule.MobArena.commands.*;
import com.garbagemule.MobArena.framework.ArenaMaster;

@CommandInfo(
    name    = "containers",
    pattern = "(containers|chests)",
    usage   = "/ma containers",
    desc    = "list containers for the selected arena",
    permission = "mobarena.setup.containers"
)
public class ContainersCommand implements Command
{
    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        StringBuffer buffy = new StringBuffer();
        Set<String> containers = am.getPlugin().getMAConfig().getKeys("arenas." + am.getSelectedArena().configName() + ".coords.containers");
        
        if (containers != null) {
            for (String c : containers) {
                buffy.append(c);
                buffy.append(" ");
            }
        }
        else {
            buffy.append(Msg.MISC_NONE);
        }
        
        Messenger.tellPlayer(sender, "Containers for arena '" + am.getSelectedArena().configName() + "': " + buffy.toString());
        return true;
    }
}
