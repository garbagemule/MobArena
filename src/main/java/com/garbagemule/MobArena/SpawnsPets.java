package com.garbagemule.MobArena;

import com.garbagemule.MobArena.framework.Arena;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.PlayerInventory;

public class SpawnsPets {

    private final Material wolfMaterial;

    SpawnsPets(Material wolfMaterial) {
        this.wolfMaterial = wolfMaterial;
    }

    void spawn(Arena arena) {
        arena.getPlayersInArena()
            .forEach(player -> spawnPets(player, arena));
    }

    private void spawnPets(Player player, Arena arena) {
        if (player == null || !player.isOnline()) {
            return;
        }
        ArenaClass ac = arena.getArenaPlayer(player).getArenaClass();
        if (ac == null || ac.getConfigName().equals("My Items")) {
            return;
        }
        spawnWolfPets(player, arena);
    }

    private void spawnWolfPets(Player player, Arena arena) {
        if (wolfMaterial == null) {
            return;
        }
        PlayerInventory inv = player.getInventory();
        int index = inv.first(wolfMaterial);
        if (index == -1) {
            return;
        }

        int amount = inv.getItem(index).getAmount();
        for (int i = 0; i < amount; i++) {
            Wolf wolf = (Wolf) arena.getWorld().spawnEntity(player.getLocation(), EntityType.WOLF);
            wolf.setTamed(true);
            wolf.setOwner(player);
            arena.getMonsterManager().addPet(wolf);
        }

        inv.setItem(index, null);
    }

}
