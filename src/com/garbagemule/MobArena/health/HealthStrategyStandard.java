package com.garbagemule.MobArena.health;

import org.bukkit.entity.Player;

public class HealthStrategyStandard implements HealthStrategy
{
    @Override
    public void setHealth(Player p, int health) {
        p.setHealth(health);
    }
}
