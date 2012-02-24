package com.garbagemule.MobArena.spout;

import java.util.List;

import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.gui.WidgetAnchor;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.garbagemule.MobArena.MobArena;

public class ClassPopup extends GenericPopup
{
    private GenericLabel arenaTitle = new GenericLabel();
    
    public ClassPopup(MobArena plugin, SpoutPlayer sp, List<ClassButton> buttons)
    {
        int screenWidth   = sp.getMainScreen().getWidth();
        int screenHeight  = sp.getMainScreen().getHeight();
        
        int buttonAmount  = buttons.size();
        int buttonsHeight = buttonAmount * 20;
        int buttonsX      = (screenWidth - 100) / 2;
        int buttonsY      = (screenHeight - buttonsHeight) / 2 + 20;
        
        // Apparently Spout complains if these aren't set
        arenaTitle.setWidth(30);
        arenaTitle.setHeight(10);
        
        arenaTitle.setText("Choose your class!");
        arenaTitle.setAlign(WidgetAnchor.CENTER_CENTER);
        arenaTitle.setX((screenWidth - arenaTitle.getWidth()) / 2).setY(buttonsY - 20);
        this.attachWidget(plugin, arenaTitle);

        for (int i = 0; i < buttons.size(); i++)
        {
            ClassButton b = buttons.get(i);
            b.setX(buttonsX).setY(buttonsY + i*20);
            this.attachWidget(plugin, b);
        }
    }
}
