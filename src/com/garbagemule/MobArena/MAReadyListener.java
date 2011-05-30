package com.garbagemule.MobArena;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * This listener flags players as ready, if they are in the arena
 * session, and hit an iron block (id: 42).
 */
// TO-DO: Merge with MASignListener and MADropListener into MALobbyListener
// TO-DO: Let server host decide which type of block is the "readyblock".
public class MAReadyListener extends PlayerListener
{
    private MobArena plugin;
    
    public MAReadyListener(MobArena instance)
    {
        plugin = instance;
    }

    public void onPlayerInteract(PlayerInteractEvent event)
    {
        Player p = event.getPlayer();
        if (ArenaManager.playerSet.contains(p))
        {
            if ((event.hasBlock()) && (event.getClickedBlock().getTypeId() == 42))
            {
                if (ArenaManager.classMap.containsKey(p))
                {
                    ArenaManager.tellPlayer(p, "You have been flagged as ready!");
                    ArenaManager.playerReady(p);
                }
                else
                {
                    ArenaManager.tellPlayer(p, "You must first pick a class!");
                }
            }
        }
    }
}