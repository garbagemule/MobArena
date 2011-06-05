package com.garbagemule.MobArena;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * This listener prevents players from warping out of the arena, if
 * they are in the arena session.
 */
// TO-DO: Fix the bug that causes the message when people get stuck in walls.
public class MATeleportListener extends PlayerListener
{
    private MobArena plugin;
    
    public MATeleportListener(MobArena instance)
    {
        plugin = instance;
    }
    
    public void onPlayerTeleport(PlayerTeleportEvent event)
    {
        Player p = event.getPlayer();
        if (!ArenaManager.playerSet.contains(p))
            return;
        
        Location to = event.getTo();
        
        if (ArenaManager.arenaLoc.equals(to) ||
            ArenaManager.lobbyLoc.equals(to) ||
            ArenaManager.spectatorLoc.equals(to))
        {
            return;
        }
        
        ArenaManager.tellPlayer(p, "Can't warp in arena! To leave, type /ma leave");
        event.setCancelled(true);
    }
}