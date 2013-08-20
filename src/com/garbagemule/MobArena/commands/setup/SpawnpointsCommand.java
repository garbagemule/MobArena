package com.garbagemule.MobArena.commands.setup;

import java.util.Set;

import org.bukkit.command.CommandSender;

import com.garbagemule.MobArena.*;
import com.garbagemule.MobArena.commands.*;
import com.garbagemule.MobArena.framework.ArenaMaster;

@CommandInfo(
    name    = "spawnpoints",
    pattern = "spawn(point)?s",
    usage   = "/ma spawnpoints",
    desc    = "list spawnpoints for the selected arena",
    permission = "mobarena.setup.spawnpoints"
)
public class SpawnpointsCommand implements Command
{
    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        StringBuilder buffy = new StringBuilder();
        Set<String> spawnpoints = am.getPlugin().getConfig().getConfigurationSection("arenas." + am.getSelectedArena().configName() + ".coords.spawnpoints").getKeys(false);
        
        if (spawnpoints != null) {
            for (String s : spawnpoints) {
                buffy.append(s);
                buffy.append(" ");
            }
        } else {
            buffy.append(Msg.MISC_NONE);
        }
        Messenger.tell(sender, "Spawnpoints for arena '" + am.getSelectedArena().configName() + "': " + buffy.toString());
        return true;
    }
}
