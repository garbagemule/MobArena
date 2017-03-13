package com.garbagemule.MobArena.time;

import org.bukkit.entity.Player;

public interface TimeStrategy
{
    /**
     * Set the time enum used by setPlayerTime()
     * @param time a Time enum
     */
    void setTime(Time time);
    
    /**
     * Set the local client time for the player.
     * @param p a player
     */
    void setPlayerTime(Player p);
    
    /**
     * Reset the local client time for the player to the server time
     * @param p a player
     */
    void resetPlayerTime(Player p);
}
