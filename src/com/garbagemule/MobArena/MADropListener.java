package com.garbagemule.MobArena;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;

/**
 * This listener prevents players from sharing class-specific
 * items (read: cheating) before the arena session starts.
 */
// TO-DO: Merge with MASignListener and MAReadyListener into MALobbyListener
public class MADropListener extends PlayerListener
{
    private MobArena plugin;
    
    public MADropListener(MobArena instance)
    {
        plugin = instance;
    }

    public void onPlayerDropItem(PlayerDropItemEvent event)
    {
        Player p = event.getPlayer();
        if (ArenaManager.playerSet.contains(p))
        {
            if (ArenaManager.isRunning)
                return;
            
            ArenaManager.tellPlayer(p, "No sharing before the arena starts!");
            event.setCancelled(true);
        }
    }
}