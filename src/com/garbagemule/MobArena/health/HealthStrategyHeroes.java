package com.garbagemule.MobArena.health;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;

public class HealthStrategyHeroes implements HealthStrategy
{
    @Override
    public void setHealth(Player p, int health) {
        int current = p.getHealth();
        int regain  = health == 20 ? 20 : health - current;
        
        try {
            EntityRegainHealthEvent event = new EntityRegainHealthEvent(p, regain, RegainReason.CUSTOM);
            Bukkit.getPluginManager().callEvent(event);
        }
        catch (Exception e) {} // Because Bukkit is retarded.
    }
}
