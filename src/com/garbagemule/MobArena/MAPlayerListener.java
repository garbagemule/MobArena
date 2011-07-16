package com.garbagemule.MobArena;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class MAPlayerListener extends PlayerListener
{
    private MobArena plugin;
    private ArenaMaster am;
    
    public MAPlayerListener(MobArena plugin, ArenaMaster am)
    {
        this.plugin = plugin;
        this.am = am;
    }
    
    public void onPlayerInteract(PlayerInteractEvent event)
    {        
        if (!am.enabled) return;
        for (Arena arena : am.arenas)
            arena.onPlayerInteract(event);
    }
    
    public void onPlayerDropItem(PlayerDropItemEvent event)
    {
        if (!am.enabled) return;
        for (Arena arena : am.arenas)
            arena.onPlayerDropItem(event);
    }
    
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event)
    {
        if (!am.enabled) return;
        for (Arena arena : am.arenas)
            arena.onPlayerBucketEmpty(event);
    }
    
    public void onPlayerTeleport(PlayerTeleportEvent event)
    {
        if (!am.enabled) return;
        for (Arena arena : am.arenas)
            arena.onPlayerTeleport(event);
    }
    
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
    {
        if (!am.enabled) return;
        for (Arena arena : am.arenas)
            arena.onPlayerCommandPreprocess(event);
    }
    
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        for (Arena arena : am.arenas)
            arena.onPlayerQuit(event);
    }
    
    public void onPlayerKick(PlayerKickEvent event)
    {
        for (Arena arena : am.arenas)
            arena.onPlayerKick(event);
    }
    
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        if (!am.updateNotify || !event.getPlayer().isOp()) return;

        final Player p = event.getPlayer();
        Bukkit.getServer().getScheduler().scheduleAsyncDelayedTask(plugin,
            new Runnable()
            {
                public void run()
                {
                    MAUtils.checkForUpdates(plugin, p, false);
                }
            }, 100);
    }
}
