package com.garbagemule.MobArena;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * This listener acts when a player is kicked or disconnected
 * from the server. If 15 seconds pass, and the player hasn't
 * reconnected, the player is forced to leave the arena.
 */
public class MADisconnectListener extends PlayerListener
{
    public MADisconnectListener(MobArena instance)
    {
    }
    
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        Player p = event.getPlayer();
        
        if (ArenaManager.playerSet.contains(p))
        {
            MAUtils.clearInventory(p);
            ArenaManager.playerLeave(p);
        }
    }
    
    public void onPlayerKick(PlayerKickEvent event)
    {
        Player p = event.getPlayer();
        
        if (ArenaManager.playerSet.contains(p))
        {
            MAUtils.clearInventory(p);
            ArenaManager.playerLeave(p);
        }
    }
    
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        if (!ArenaManager.checkUpdates)
            return;
        
        final Player p = event.getPlayer();
        
        if (!event.getPlayer().isOp())
            return;
            
        ArenaManager.server.getScheduler().scheduleSyncDelayedTask(ArenaManager.plugin,
            new Runnable()
            {
                public void run()
                {
                    MAUtils.checkForUpdates(p, true);
                }
            }, 100);
    }
}