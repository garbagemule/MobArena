package com.garbagemule.MobArena.time;

import org.bukkit.entity.Player;

public class TimeStrategyNull implements TimeStrategy
{
    @Override
    public void setTime(Time time) {}
    
    @Override
    public void setPlayerTime(Player p) {}

    @Override
    public void resetPlayerTime(Player p) {}
}
