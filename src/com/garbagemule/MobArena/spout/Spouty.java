package com.garbagemule.MobArena.spout;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.entity.Player;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.garbagemule.MobArena.MobArena;
import com.garbagemule.MobArena.framework.Arena;

public class Spouty
{
    public static void classSelectionScreen(MobArena plugin, Arena arena, Player p)
    {
        SpoutPlayer sp = SpoutManager.getPlayer(p);
        if (sp == null) return;
        
        List<ClassButton> buttons = new LinkedList<ClassButton>();
        for (String s : arena.getClasses().keySet())
            buttons.add(new ClassButton(s));
        
        ClassPopup popup = new ClassPopup(plugin, sp, buttons);
        sp.getMainScreen().attachPopupScreen(popup);
    }
}
