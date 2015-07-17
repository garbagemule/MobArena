package com.garbagemule.MobArena.time;

import org.bukkit.entity.Player;

public class TimeStrategyLocked implements TimeStrategy
{
    private Time time;
    
    public TimeStrategyLocked(Time time) {
        setTime(time);
    }

    @Override
    public void setTime(Time time) {
        this.time = time;
    }
    
    @Override
    public void setPlayerTime(Player p) {
        p.setPlayerTime(time.getTime(), false);
    }

    @Override
    public void resetPlayerTime(Player p) {
        p.resetPlayerTime();
    }
}
