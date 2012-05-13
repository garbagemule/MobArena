package com.garbagemule.MobArena.commands.setup;

import java.util.Set;

import org.bukkit.command.CommandSender;

import com.garbagemule.MobArena.*;
import com.garbagemule.MobArena.commands.*;
import com.garbagemule.MobArena.framework.ArenaMaster;

@CommandInfo(
    name    = "listclasses",
    pattern = "(list)?classes(.)*",
    usage   = "/ma listclasses",
    desc    = "list all current classes",
    permission = "mobarena.setup.classes"
)
public class ListClassesCommand implements Command
{
    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        Messenger.tellPlayer(sender, "Current classes:");
        Set<String> classes = am.getClasses().keySet();
        if (classes == null || classes.isEmpty()) {
            Messenger.tellPlayer(sender, "<none>");
            return true;
        }
        
        for (String c : classes) {
            Messenger.tellPlayer(sender, "- " + c);
        }
        return true;
    }
}
