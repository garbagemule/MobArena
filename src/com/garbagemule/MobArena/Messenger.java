package com.garbagemule.MobArena;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.garbagemule.MobArena.framework.Arena;

public class Messenger
{
    private static final Logger log = Logger.getLogger("Minecraft");
    
    private static final String prefix = "[MobArena] ";
    
    private Messenger() {}
    
    public static boolean tellSpoutPlayer(Player p, Msg msg, String s, Material logo) {
        // Grab the SpoutPlayer.
        SpoutPlayer sp = MobArena.hasSpout ? SpoutManager.getPlayer(p) : null;

        if (msg.hasSpoutMsg() && sp != null && sp.isSpoutCraftEnabled()) {
            // Grab the message text.
            String text = msg.toSpoutString(s);

            // If more than 26 characters, truncate.
            if (text.length() > 26)
                text = text.substring(0, 26);

            // If the logo is null, use an iron sword.
            if (logo == null)
                logo = msg.getLogo();

            // Send the notification.
            sp.sendNotification("MobArena", text, logo, (short) 0, 2000);
            return true;
        }
        else {
            return tellPlayer(p, msg.toString(s));
        }
    }

    public static boolean tellSpoutPlayer(Player p, Msg msg, Material logo) {
        return tellSpoutPlayer(p, msg, null, logo);
    }

    public static boolean tellSpoutPlayer(Player p, Msg msg, String s) {
        return tellSpoutPlayer(p, msg, s, null);
    }

    public static boolean tellSpoutPlayer(Player p, Msg msg) {
        return tellSpoutPlayer(p, msg, null, null);
    }

    public static boolean tellPlayer(CommandSender p, String msg) {
        // If the input sender is null or the string is empty, return.
        if (p == null || msg.equals(" "))
            return false;

        // Otherwise, send the message with the [MobArena] tag.
        p.sendMessage(ChatColor.GREEN + "[MobArena] " + ChatColor.WHITE + msg);
        return true;
    }

    public static boolean tellPlayer(CommandSender p, Msg msg, String s, boolean spout, Material logo) {
        if (spout && p instanceof Player)
            return tellSpoutPlayer((Player) p, msg, s, logo);

        return tellPlayer(p, msg.toString(s));
    }

    public static boolean tellPlayer(CommandSender p, Msg msg, String s, Material logo) {
        return tellPlayer(p, msg, s, MobArena.hasSpout, logo);
    }

    public static boolean tellPlayer(CommandSender p, Msg msg, String s) {
        return tellPlayer(p, msg, s, MobArena.hasSpout, null);
    }

    public static boolean tellPlayer(CommandSender p, Msg msg) {
        return tellPlayer(p, msg, null, MobArena.hasSpout, null);
    }

    public static void tellAll(Arena arena, Msg msg, String s, boolean notifyPlayers) {
        List<Player> players = new ArrayList<Player>();
        players.addAll(arena.getPlayersInArena());
        players.addAll(arena.getPlayersInLobby());
        players.addAll(arena.getSpectators());
        for (Player p : players)
            tellPlayer(p, msg, s);
    }

    public static void tellAll(Arena arena, Msg msg, String s) {
        tellAll(arena, msg, s, false);
    }

    public static void tellAll(Arena arena, Msg msg, boolean notifyPlayers) {
        tellAll(arena, msg, null, notifyPlayers);
    }

    public static void tellAll(Arena arena, Msg msg) {
        tellAll(arena, msg, null, false);
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
