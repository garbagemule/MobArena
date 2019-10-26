package com.garbagemule.MobArena;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectileManager {

    private Map<Player, List<Projectile>> projectiles;

    public ProjectileManager() {
        projectiles = new HashMap<>();
    }

    public void addProjectile(Projectile projectile) {
        if (projectile.getShooter() instanceof Player) {
            Player player = (Player) projectile.getShooter();
            if (projectiles.containsKey(player)) {
                projectiles.get(player).add(projectile);
            } else {
                projectiles.put(player, new ArrayList<>());
                projectiles.get(player).add(projectile);
            }
        }
    }

    public void removeProjectiles(Player player) {
        if (projectiles.containsKey(player)) {
            for (Projectile projectile : projectiles.get(player)) {
                projectile.remove();
            }
        }
        projectiles.remove(player);
    }

    public void removeProjectile(Projectile projectile) {
        if (projectile.getShooter() instanceof Player) {
            Player player = (Player) projectile.getShooter();
            if (projectiles.containsKey(player)) {
                projectiles.get(player).remove(projectile);
            }
        }
    }
}