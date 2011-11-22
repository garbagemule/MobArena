package com.prosicraft.MobArena.listeners;

import org.bukkit.entity.Player;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.event.screen.ScreenListener;
import org.getspout.spoutapi.gui.Button;

import com.prosicraft.MobArena.Arena;
import com.prosicraft.MobArena.MobArena;
import com.garbagemule.MobArena.spout.ClassButton;

public class MASpoutScreenListener extends ScreenListener
{
    private MobArena plugin;
    
    public MASpoutScreenListener(MobArena plugin)
    {
        this.plugin = plugin;
    }
    
    public void onButtonClick(ButtonClickEvent event)
    {
        Button b = event.getButton();
        if (!(b instanceof ClassButton)) return;
        
        Player p = event.getPlayer();
        Arena arena = plugin.getAM().getArenaWithPlayer(p);
        if (arena == null) return;
        
        arena.assignClass(p, b.getText());
        event.getPlayer().getMainScreen().closePopup();
    }
}
