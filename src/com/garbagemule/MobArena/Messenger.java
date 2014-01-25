package com.garbagemule.MobArena;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.garbagemule.MobArena.framework.Arena;

public class Messenger
{
    private static final Logger log = Logger.getLogger("Minecraft");
    
    private static final String prefix = "[MobArena] ";
    
    private Messenger() {}

    public static boolean tell(CommandSender p, String msg) {
        // If the input sender is null or the string is empty, return.
        if (p == null || msg.equals("")) {
            return false;
        }

        // Otherwise, send the message with the [MobArena] tag.
        p.sendMessage(ChatColor.GREEN + "[MobArena] " + ChatColor.RESET + msg);
        return true;
    }

    public static boolean tell(CommandSender p, Msg msg, String s) {
        return tell(p, msg.format(s));
    }

    public static boolean tell(CommandSender p, Msg msg) {
        return tell(p, msg.toString());
    }

    public static void announce(Arena arena, String msg) {
        List<Player> players = new ArrayList<Player>();
        players.addAll(arena.getPlayersInArena());
        players.addAll(arena.getPlayersInLobby());
        players.addAll(arena.getSpectators());
        for (Player p : players) {
            tell(p, msg);
        }
    }

    public static void announce(Arena arena, Msg msg, String s) {
        announce(arena, msg.format(s));
    }

    public static void announce(Arena arena, Msg msg) {
        announce(arena, msg.toString());
    }
    
    public static void info(String msg) {
        log.info(prefix + msg);
    }
    
    public static void warning(String msg) {
        log.warning(prefix + msg);
    }
    
    public static void severe(String msg) {
        log.severe(prefix + msg);
    }
}
