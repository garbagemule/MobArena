package com.garbagemule.MobArena.health;

import org.bukkit.entity.Player;

public interface HealthStrategy
{
    /**
     * Set the health of a player.
     * @param p a player
     * @param health amount of health
     */
    public void setHealth(Player p, int health);
}
