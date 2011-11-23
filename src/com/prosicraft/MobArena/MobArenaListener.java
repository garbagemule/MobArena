package com.prosicraft.MobArena;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.prosicraft.MobArena.waves.Wave.WaveBranch;
import com.prosicraft.MobArena.waves.Wave.WaveType;

public abstract class MobArenaListener
{
    protected MobArena plugin;
    
    public MobArenaListener()
    {
        plugin = (MobArena) Bukkit.getServer().getPluginManager().getPlugin("MobArena");
        plugin.getAM().listeners.add(this);
    }
    
    public void onArenaStart(Arena arena) {}
    public void onArenaEnd(Arena arena) {}
    public void onWave(Arena arena, int waveNumber, String waveName, WaveBranch waveBranch, WaveType waveType) {}
    public void onPlayerJoin(Arena arena, Player p) {}
    public void onPlayerLeave(Arena arena, Player p) {}
    public void onPlayerDeath(Arena arena, Player p) {}
}
