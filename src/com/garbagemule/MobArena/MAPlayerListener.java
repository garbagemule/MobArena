package com.garbagemule.MobArena;

import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.garbagemule.MobArena.MAMessages.Msg;


public class MAPlayerListener extends PlayerListener
{
    private MobArena plugin;
    private ArenaMaster am;
    
    public MAPlayerListener(MobArena plugin, ArenaMaster am)
    {
        this.plugin = plugin;
        this.am = am;
    }
    
    public void onPlayerAnimation(PlayerAnimationEvent event)
    {
        if (!am.enabled) return;
        for (Arena arena : am.arenas)
            arena.eventListener.onPlayerAnimation(event);
    }
    
    public void onPlayerInteract(PlayerInteractEvent event)
    {
    	if (!am.enabled) return;
        if(event.hasBlock() && event.getClickedBlock().getState() instanceof Sign) {
            Player p = event.getPlayer();
            if (p.hasPermission("mobarena.use.signs")) {
                Sign sign = (Sign) event.getClickedBlock().getState();
                if (sign.getLine(0).contains("[MA]")) {
                    if (sign.getLine(1).contains("[join]")) {
                        String arenaName = sign.getLine(2);
                        am.joinArena(p, arenaName);
                        return;
                    }
                    
                    if (sign.getLine(1).contains("[leave]")) {
                        am.leaveArena(p);
                        return;
                    }
                    
                    if (sign.getLine(1).contains("[spectate]")) {
                        String arenaName = sign.getLine(2);
                        am.spectateArena(p, arenaName);
                        return;
                    }
                }
            }
        }
    	for (Arena arena : am.arenas)
    		arena.eventListener.onPlayerInteract(event);
    }
    
    public void onPlayerDropItem(PlayerDropItemEvent event)
    {
        if (!am.enabled) return;
        for (Arena arena : am.arenas)
            arena.eventListener.onPlayerDropItem(event);
    }
    
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event)
    {
        if (!am.enabled) return;
        for (Arena arena : am.arenas)
            arena.eventListener.onPlayerBucketEmpty(event);
    }
    
    public void onPlayerTeleport(PlayerTeleportEvent event)
    {
        if (!am.enabled) return;
        for (Arena arena : am.arenas)
            arena.eventListener.onPlayerTeleport(event);
    }
    
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
    {
        if (!am.enabled) return;
        for (Arena arena : am.arenas)
            arena.eventListener.onPlayerCommandPreprocess(event);
    }
    
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        for (Arena arena : am.arenas)
            arena.eventListener.onPlayerQuit(event);
    }
    
    public void onPlayerKick(PlayerKickEvent event)
    {
        for (Arena arena : am.arenas)
            arena.eventListener.onPlayerKick(event);
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
            }, 60);
    }
}
