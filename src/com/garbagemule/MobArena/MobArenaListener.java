package com.garbagemule.MobArena;

import org.bukkit.entity.Player;

public class MobArenaListener
{
    protected MobArena plugin;
    
    public MobArenaListener()
    {
        plugin = ArenaManager.plugin;
        ArenaManager.listeners.add(this);
    }
    
    public void onArenaStart() {}
    public void onArenaEnd() {}
    public void onDefaultWave(int waveNumber) {}
    public void onSpecialWave(int waveNumber, int specialwaveNumber) {}
    public void onPlayerJoin(Player p) {}
    public void onPlayerLeave(Player p) {}
    public void onPlayerDeath(Player p) {}
}
