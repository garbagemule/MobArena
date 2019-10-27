package com.garbagemule.MobArena;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ProjectileManager {

    private Map<Player, Set<Projectile>> projectiles;

    public ProjectileManager() {
        projectiles = new HashMap<>();
    }

    public void addProjectile(Player player, Projectile projectile) {
        if (!projectiles.containsKey(player)) {
            projectiles.put(player, new HashSet<>());
        }
        projectiles.get(player).add(projectile);
    }

    public void clearProjectiles(Player player) {
        Set<Projectile> launched = projectiles.get(player);
        if(launched == null) return;

        for(Projectile projectile : launched) {
            projectile.remove();
        }

        projectiles.remove(player);
    }

    public void removeProjectile(Projectile projectile) {
        if (projectile.getShooter() instanceof Player) {
            Player player = (Player) projectile.getShooter();
            Set<Projectile> launched = projectiles.get(player);

            if(launched == null) return;
            launched.remove(projectile);
        }
    }

}
