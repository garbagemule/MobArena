package com.garbagemule.MobArena;

import org.bukkit.block.Sign;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * This listener handles the class assignments. When a player in
 * the arena session hits a class sign, they will be given their
 * class-specific items.
 */
// TO-DO: Merge with MAReadyListener and MADropListener into MALobbyListener
public class MASignListener extends PlayerListener
{
    private MobArena plugin;

    public MASignListener(MobArena instance)
    {
        plugin = instance;
    }
    
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        // Only do these checks if the arena isn't running.
        if (!ArenaManager.isRunning)
        {
            Action a = event.getAction();
            Player p = event.getPlayer();

            // Check if player is trying to use an item.
            // TO-DO: Find a way to allow right-click. Perhaps just remove the return;
            if ((a == Action.RIGHT_CLICK_AIR) || (a == Action.RIGHT_CLICK_BLOCK))
            {
                if (ArenaManager.playerSet.contains(p))
                    event.setUseItemInHand(Result.DENY);
                return;
            }

            // Check if the clicked block is one of the class signs.
            if ((event.hasBlock()) && (event.getClickedBlock().getState() instanceof Sign))
            {
                // Cast the block to a sign to get the text on it.
                Sign sign = (Sign) event.getClickedBlock().getState();

                if (ArenaManager.playerSet.contains(p))
                {
                    // Check if the first line of the sign is a class name.
                    String className = sign.getLine(0);
                    if (ArenaManager.classes.contains(className))
                    {
                        // Set the player's class.
                        ArenaManager.assignClass(p, className);
                        ArenaManager.tellPlayer(p, "You have chosen " + className + " as your class!");
                    }
                }
            }
        }
    }
}