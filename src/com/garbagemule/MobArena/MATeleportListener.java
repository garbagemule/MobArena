package com.garbagemule.MobArena;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * This listener prevents players from warping out of the arena, if
 * they are in the arena session.
 * Also prevents players from warping into the arena during a session.
 */
// TO-DO: Fix the bug that causes the message when people get stuck in walls.
public class MATeleportListener extends PlayerListener
{
    public MATeleportListener(MobArena instance)
    {
    }
    
    public void onPlayerTeleport(PlayerTeleportEvent event)
    {
        Player p = event.getPlayer();
        Location to = event.getTo();
        
        // If the player teleports from outside of the arena..
        if (!ArenaManager.playerSet.contains(p))
        {
            if (!MAUtils.inRegion(to))
                return;
            
            if (!ArenaManager.isRunning)
                return;
            
            if (ArenaManager.spectatorLoc.equals(to))
                return;
            
            // ..into the region during a battle; cancel
            ArenaManager.tellPlayer(p, "Can't warp to the arena during battle!");
            ArenaManager.tellPlayer(p, "Type /ma spec to watch.");
            event.setCancelled(true);
            return;
        }
        
        // Otherwise, only warp if to is in the locationMap, or a valid warp
        if (ArenaManager.locationMap.get(p).equals(to) ||
            ArenaManager.arenaLoc.equals(to) ||
            ArenaManager.lobbyLoc.equals(to) ||
            ArenaManager.spectatorLoc.equals(to))
            return;
        
        // If warp isn't valid, notify the player
        ArenaManager.tellPlayer(p, "Warping not allowed in the arena!");
        ArenaManager.tellPlayer(p, "Type /ma leave to leave");
        event.setCancelled(true);
    }
}