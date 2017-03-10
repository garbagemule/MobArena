package com.garbagemule.MobArena;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.garbagemule.MobArena.framework.Arena;

public class Messenger
{
    private final String prefix;

    public Messenger(String prefix) {
        if (prefix.contains("&")) {
            prefix = ChatColor.translateAlternateColorCodes('&', prefix);
        }
        this.prefix = prefix;
    }

    public boolean tell(CommandSender p, String msg) {
        // If the input sender is null or the string is empty, return.
        if (p == null || msg.equals("")) {
            return false;
        }

        // Otherwise, send the message with the [MobArena] tag.
        p.sendMessage(prefix + ChatColor.RESET + msg);
        return true;
    }

    public boolean tell(CommandSender p, Msg msg, String s) {
        return tell(p, msg.format(s));
    }

    public boolean tell(CommandSender p, Msg msg) {
        return tell(p, msg.toString());
    }

    public void announce(Arena arena, String msg) {
        List<Player> players = new ArrayList<Player>();
        players.addAll(arena.getPlayersInArena());
        players.addAll(arena.getPlayersInLobby());
        players.addAll(arena.getSpectators());
        for (Player p : players) {
            tell(p, msg);
        }
    }

    public void announce(Arena arena, Msg msg, String s) {
        announce(arena, msg.format(s));
    }

    public void announce(Arena arena, Msg msg) {
        announce(arena, msg.toString());
    }
}
