package com.garbagemule.MobArena.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.garbagemule.MobArena.MobArena;

public class Delays
{
    public static void douse(MobArena plugin, final Player p, long delay)
    {
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
        {
            public void run()
            {
                p.setFireTicks(0);
            }
        }, delay);
    }
}
