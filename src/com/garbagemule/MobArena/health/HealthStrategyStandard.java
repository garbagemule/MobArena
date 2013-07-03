package com.garbagemule.MobArena.health;

import org.bukkit.entity.Player;

public class HealthStrategyStandard implements HealthStrategy
{
    @Override
    public void setHealth(Player p, double health) {
        // TODO: Remove cast for 1.6
        p.setHealth((int) health);
    }
}
