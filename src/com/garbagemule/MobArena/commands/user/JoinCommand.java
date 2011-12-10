package com.garbagemule.MobArena.commands.user;

import java.util.*;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.garbagemule.MobArena.commands.*;
import com.garbagemule.MobArena.*;

public class JoinCommand implements MACommand
{
    @Override
    public String[] getNames() {
        return new String[] { "join" , "j" };
    }

    @Override
    public String getPermission() {
        return "mobarena.use.join";
    }

    @Override
    public boolean execute(MobArenaPlugin plugin, Player sender, String... args) {
        // Grab the arena master
        ArenaMaster am = plugin.getArenaMaster();
        
        // Get all enabled arenas.
        List<Arena> arenas = am.getEnabledArenas();
        
        // If no arena was specified, and multiple arenas are available, notify.
        if (args.length < 1 && arenas.size() > 1) {
            sender.sendMessage("There are more than one arena. Pick one, damnit!");
            return false;
        }

        // If only one arena, pick it no matter what, otherwise, look for name.
        Arena arena = arenas.size() == 1 ? arenas.get(0) : am.getArenaWithName(args[0]);
        
        // If null, no arena was found.
        if (arena == null) {
            sender.sendMessage("The arena '" + args[0] + "' does not exist.");
            return false;
        }
        
        sender.sendMessage("You've joined arena '" + arena.configName() + "'!");
        return true;
    }

    @Override
    public boolean executeFromConsole(MobArenaPlugin plugin, CommandSender sender, String... args) {
        return false;
    }
}
