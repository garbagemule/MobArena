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
        // Grab the arena master and the args.
        ArenaMaster am = plugin.getArenaMaster();
        
        // Get the arenas.
        List<Arena> arenas = am.getEnabledAndPermittedArenas(sender);
        
        // Turn the list into a string.
        String msg = Msg.MISC_LIST_ARENAS.toString(listToString(arenas));
        
        plugin.tell(sender, msg);
        return true;
    }

    @Override
    public boolean executeFromConsole(MobArenaPlugin plugin, CommandSender sender, String... args) {
        return false;
    }
    
    private <E> String listToString(List<E> list) {
        String result = "";
        
        for (E e : list) {
            result += e + ", ";
        }
        
        if (result.equals(""))
            return Msg.MISC_NONE.toString();
        
        return result.substring(0, result.length() - 2);
    }
}
