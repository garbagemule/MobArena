package com.garbagemule.MobArena.commands.user;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.garbagemule.MobArena.commands.*;
import com.garbagemule.MobArena.*;

public class ArenaListCommand implements MACommand
{
    @Override
    public String[] getNames() {
        return new String[] { "arenas" , "list" };
    }

    @Override
    public String getPermission() {
        return "mobarena.use.arenalist";
    }

    @Override
    public boolean execute(MobArenaPlugin plugin, Player sender, String... args) {
        // Grab the arena master.
        ArenaMaster am = plugin.getArenaMaster();
        
        // Get the arenas.
        List<Arena> arenas = am.getEnabledArenas();
        
        String msg = "Available arenas: ";
        if (arenas.size() == 0) {
            sender.sendMessage(msg + "<none>");
            return true;
        }
        
        // Enumerate arenas.
        for (Arena a : arenas) {
            msg += a.configName() + ", ";
        }
        
        // Trim off the trailing comma.
        msg = msg.substring(0, msg.length() - 2);
        
        // Send the message!
        sender.sendMessage(msg);
        return true;
    }

    @Override
    public boolean executeFromConsole(MobArenaPlugin plugin, CommandSender sender, String... args) {
        return false;
    }
}
