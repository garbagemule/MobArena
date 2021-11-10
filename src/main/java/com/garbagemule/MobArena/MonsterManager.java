package com.garbagemule.MobArena;

import com.garbagemule.MobArena.healthbar.HealthBar;
import com.garbagemule.MobArena.waves.MABoss;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class MonsterManager
{
    private final Set<LivingEntity> monsters;
    private final Set<LivingEntity> sheep;
    private final Set<LivingEntity> golems;
    private final Map<LivingEntity,MABoss> bosses;
    private final Map<LivingEntity,List<ItemStack>> suppliers;
    private final Set<LivingEntity> mounts;
    private final Map<Entity, Player> petToPlayer;
    private final Map<Player, Set<Entity>> playerToPets;

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
        collection.stream().filter(Objects::nonNull).forEach(Entity::remove);
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

        Optional.ofNullable(petToPlayer.remove(pet))
                .flatMap(player -> Optional.ofNullable(playerToPets.get(player)))
                .ifPresent(entities -> playerToPets.remove(pet));
    }

    public Player getOwner(Entity pet) {
        return petToPlayer.get(pet);
    }

    public Collection<Entity> getPets(Player owner) {
        Optional<Set<Entity>> pets = Optional.ofNullable(playerToPets.get(owner));
        return (pets.isPresent() ? pets.get() : Collections.emptyList());
    }

    public void removePets(Player p) {
        Optional.ofNullable(playerToPets.remove(p)).ifPresent(pets -> {
            pets.forEach(Entity::remove);
            pets.clear();
        });
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
        mounts.forEach(Entity::remove);
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
