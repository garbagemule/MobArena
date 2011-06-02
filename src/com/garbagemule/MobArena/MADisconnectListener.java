package com.garbagemule.MobArena;

import java.util.List;
import java.util.Arrays;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerKickEvent;

/**
 * This listener acts when a player is kicked or disconnected
 * from the server. If 15 seconds pass, and the player hasn't
 * reconnected, the player is forced to leave the arena.
 */
public class MADisconnectListener extends PlayerListener
{
    private MobArena plugin;
    
    public MADisconnectListener(MobArena instance)
    {
        plugin = instance;
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
}