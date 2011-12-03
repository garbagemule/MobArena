package com.garbagemule.MobArena.spout;

import org.getspout.spoutapi.gui.GenericButton;

public class ClassButton extends GenericButton
{
    public ClassButton(String className)
    {
        this.setText(className);
        this.setWidth(100).setHeight(20);
    }
}
