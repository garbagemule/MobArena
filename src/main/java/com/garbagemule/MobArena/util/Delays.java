package com.garbagemule.MobArena.util;

import com.garbagemule.MobArena.MobArena;
import com.garbagemule.MobArena.framework.Arena;
import org.bukkit.entity.Player;

public class Delays
{
    public static void douse(MobArena plugin, final Player p, long delay) {
        if (!plugin.isEnabled()) return;
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            public void run() {
                if (p.isOnline()) {
                    p.setFireTicks(0);
                }
            }
        }, delay);
    }
    
    public static void revivePlayer(MobArena plugin, final Arena arena, final Player p) {
        if (!plugin.isEnabled()) return;
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            public void run() {
                if (p.isOnline()) {
                    arena.revivePlayer(p);
                }
            }
        });
    }
}
