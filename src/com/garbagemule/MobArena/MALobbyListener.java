package com.garbagemule.MobArena;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

/**
 * This listener prevents players from sharing class-specific
 * items (read: cheating) before the arena session starts.
 */
// TO-DO: Merge with MASignListener and MAReadyListener into MALobbyListener
public class MALobbyListener extends PlayerListener
{    
    public MALobbyListener(MobArena instance)
    {
    }

    /**
     * Players can only drop items when the arena session has started.
     */
    public void onPlayerDropItem(PlayerDropItemEvent event)
    {
        Player p = event.getPlayer();
        
        if (!ArenaManager.playerSet.contains(p))
            return;
            
        if (ArenaManager.isRunning)
            return;
        
        ArenaManager.tellPlayer(p, "No sharing before the arena starts!");
        event.setCancelled(true);
    }
    
    /**
     * Adds liquid blocks to the blockset when players empty their buckets.
     */
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event)
    {        
        if (!ArenaManager.playerSet.contains(event.getPlayer()))
            return;
        
        if (!ArenaManager.isRunning)
        {
            event.getBlockClicked().getFace(event.getBlockFace()).setTypeId(0);
            event.setCancelled(true);
            return;
        }

        Block liquid = event.getBlockClicked().getFace(event.getBlockFace());
        ArenaManager.blockSet.add(liquid);
    }
    
    /**
     * Checks if the player hits an iron block or a sign, or if the player
     * is trying to use an item.
     */
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        // Only do these checks if the arena isn't running.
        if (ArenaManager.isRunning)
            return;
        
        Player p = event.getPlayer();
        
        if (!ArenaManager.playerSet.contains(p))
            return;

        Action a = event.getAction();
        
        // Check if player is trying to use an item.
        if ((a == Action.RIGHT_CLICK_AIR) || (a == Action.RIGHT_CLICK_BLOCK))
        {
            event.setUseItemInHand(Result.DENY);
            event.setCancelled(true);
        }
        
        // Iron block
        if (event.hasBlock() && event.getClickedBlock().getTypeId() == 42)
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
            return;
        }
        
        // Sign
        if (event.hasBlock() && event.getClickedBlock().getState() instanceof Sign)
        {
            if (a == Action.RIGHT_CLICK_BLOCK)
            {
                ArenaManager.tellPlayer(p, "Punch the sign. Don't right-click.");
                return;
            }
            
            // Cast the block to a sign to get the text on it.
            Sign sign = (Sign) event.getClickedBlock().getState();
            
            // Check if the first line of the sign is a class name.
            String className = sign.getLine(0);
            if (!ArenaManager.classes.contains(className))
                return;
                
            // Set the player's class.
            ArenaManager.assignClass(p, className);
            ArenaManager.tellPlayer(p, "You have chosen " + className + " as your class!");
            return;
        }
    }
}