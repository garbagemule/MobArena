package com.garbagemule.MobArena;

import com.garbagemule.MobArena.healthbar.HealthBar;
import com.garbagemule.MobArena.waves.MABoss;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class MonsterManager
{
    private Set<LivingEntity> monsters, sheep, golems;
    private Map<LivingEntity,MABoss> bosses;
    private Map<LivingEntity,List<ItemStack>> suppliers;
    private Set<LivingEntity> mounts;
    private Map<Entity, Player> petToPlayer;
    private Map<Player, Set<Entity>> playerToPets;

    public MonsterManager() {
        this.monsters   = new HashSet<>();
        this.sheep      = new HashSet<>();
        this.golems     = new HashSet<>();
        this.bosses     = new HashMap<>();
        this.suppliers  = new HashMap<>();
        this.mounts     = new HashSet<>();
        this.petToPlayer = new HashMap<>();
        this.playerToPets = new HashMap<>();
    }

    public void reset() {
        monsters.clear();
        sheep.clear();
        golems.clear();
        bosses.clear();
        suppliers.clear();
        mounts.clear();
        petToPlayer.clear();
        playerToPets.clear();
    }

    public void clear() {
        bosses.values().stream()
            .map(MABoss::getHealthBar)
            .filter(Objects::nonNull)
            .forEach(HealthBar::removeAll);

        removeAll(monsters);
        removeAll(sheep);
        removeAll(golems);
        removeAll(bosses.keySet());
        removeAll(suppliers.keySet());
        removeAll(mounts);
        removeAll(petToPlayer.keySet());

        reset();
    }

    private void removeAll(Collection<? extends Entity> collection) {
        for (Entity e : collection) {
            if (e != null) {
                e.remove();
            }
        }
    }

    public void remove(Entity e) {
        if (monsters.remove(e)) {
            sheep.remove(e);
            golems.remove(e);
            suppliers.remove(e);
            MABoss boss = bosses.remove(e);
            if (boss != null) {
                boss.setDead(true);
            }
        }
    }

    public Set<LivingEntity> getMonsters() {
        return monsters;
    }

    public void addMonster(LivingEntity e) {
        monsters.add(e);
    }

    public boolean removeMonster(Entity e) {
        return monsters.remove(e);
    }

    public Set<LivingEntity> getExplodingSheep() {
        return sheep;
    }

    public void addExplodingSheep(LivingEntity e) {
        sheep.add(e);
    }

    public boolean removeExplodingSheep(LivingEntity e) {
        return sheep.remove(e);
    }

    public Set<LivingEntity> getGolems() {
        return golems;
    }

    public void addGolem(LivingEntity e) {
        golems.add(e);
    }

    public boolean removeGolem(LivingEntity e) {
        return golems.remove(e);
    }

    public void addPet(Player player, Entity pet) {
        petToPlayer.put(pet, player);
        playerToPets
            .computeIfAbsent(player, (key) -> new HashSet<>())
            .add(pet);
    }

    public boolean hasPet(Entity e) {
        return petToPlayer.containsKey(e);
    }

    public void removePet(Entity pet) {
        pet.remove();

        Player owner = petToPlayer.remove(pet);
        if (owner != null) {
            Set<Entity> pets = playerToPets.get(owner);
            if (pets != null) {
                pets.remove(pet);
            }
        }
    }

    public Player getOwner(Entity pet) {
        return petToPlayer.get(pet);
    }

    public Collection<Entity> getPets(Player owner) {
        Set<Entity> pets = playerToPets.get(owner);
        if (pets != null) {
            return pets;
        }
        return Collections.emptySet();
    }

    public void removePets(Player p) {
        Set<Entity> pets = playerToPets.remove(p);
        if (pets != null) {
            pets.forEach(Entity::remove);
            pets.clear();
        }
    }

    public void addMount(LivingEntity e) {
        mounts.add(e);
    }

    public boolean hasMount(Entity e) {
        return mounts.contains(e);
    }

    public boolean removeMount(Entity e) {
        return mounts.remove(e);
    }

    public void removeMounts() {
        for (LivingEntity e : mounts) {
            e.remove();
        }
    }

    public void addSupplier(LivingEntity e, List<ItemStack> drops) {
        suppliers.put(e, drops);
    }

    public List<ItemStack> getLoot(Entity e) {
        return suppliers.get(e);
    }

    public MABoss addBoss(LivingEntity e, double maxHealth) {
        MABoss b = new MABoss(e, maxHealth);
        bosses.put(e, b);
        return b;
    }

    public MABoss removeBoss(LivingEntity e) {
        return bosses.remove(e);
    }

    public MABoss getBoss(LivingEntity e) {
        return bosses.get(e);
    }

    public Set<LivingEntity> getBossMonsters() {
        return bosses.keySet();
    }
}
