package com.garbagemule.MobArena;

import org.bukkit.entity.Player;

public abstract class AbstractArena
{
    /**
     * Start the arena session.
     * This method should warp all players to their respective warp points, start all
     * needed timers, clear/populate all sets and lists, and flag all booleans.
     */
    public abstract void startArena();
    
    /**
     * Stop the arena session.
     * Distribute rewards, clean up arena floor and reset everything to how it was before
     * the arena session was started.
     */
    public abstract void endArena();
    
    /**
     * Player joins the arena/lobby.
     */
    public abstract void playerJoin(Player p);
    
    /**
     * Player leaves the arena/lobby.
     */
    public abstract void playerLeave(Player p);
    
    /**
     * Player dies in the arena.
     */
    public abstract void playerDeath(Player p);
    
    /**
     * Player signals that they are ready.
     */
    public abstract void playerReady(Player p);
}
