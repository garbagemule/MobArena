package com.garbagemule.MobArena.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.gui.Button;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.spout.ClassButton;
import com.garbagemule.MobArena.MobArena;

public class SpoutScreenListener implements Listener
{
    private MobArena plugin;
    
    public SpoutScreenListener(MobArena plugin)
    {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onButtonClick(ButtonClickEvent event)
    {
        Button b = event.getButton();
        if (!(b instanceof ClassButton)) return;
        
        Player p = event.getPlayer();
        Arena arena = plugin.getArenaMaster().getArenaWithPlayer(p);
        if (arena == null) return;
        
        arena.assignClass(p, b.getText());
        event.getPlayer().getMainScreen().closePopup();
    }
}
