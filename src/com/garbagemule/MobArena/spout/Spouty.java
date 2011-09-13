package com.garbagemule.MobArena.spout;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginManager;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.garbagemule.MobArena.Arena;
import com.garbagemule.MobArena.MobArena;
import com.garbagemule.MobArena.listeners.MASpoutScreenListener;

public class Spouty
{
    public static void registerEvents(MobArena plugin)
    {
        PluginManager pm = Bukkit.getServer().getPluginManager();
        pm.registerEvent(Event.Type.CUSTOM_EVENT, new MASpoutScreenListener(plugin), Priority.Normal, plugin);
    }
    
    public static void classSelectionScreen(MobArena plugin, Arena arena, Player p)
    {
        SpoutPlayer sp = SpoutManager.getPlayer(p);
        if (sp == null) return;
        
        List<ClassButton> buttons = new LinkedList<ClassButton>();
        for (String s : arena.getClasses())
            buttons.add(new ClassButton(s));
        
        ClassPopup popup = new ClassPopup(plugin, sp, buttons);
        sp.getMainScreen().attachPopupScreen(popup);
    }
}
