package com.garbagemule.MobArena;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.ItemStack;

import com.garbagemule.MobArena.waves.MABoss;

public class MonsterManager
{
    private Set<LivingEntity> monsters, sheep, golems;
    private Set<Wolf> pets;
    private Map<LivingEntity,MABoss> bosses;
    private Map<LivingEntity,List<ItemStack>> suppliers;
    
    public MonsterManager() {
        this.monsters  = new HashSet<LivingEntity>();
        this.sheep     = new HashSet<LivingEntity>();
        this.golems    = new HashSet<LivingEntity>();
        this.pets      = new HashSet<Wolf>();
        this.bosses    = new HashMap<LivingEntity,MABoss>();
        this.suppliers = new HashMap<LivingEntity,List<ItemStack>>();
    }
    
    public void reset() {
        monsters.clear();
        sheep.clear();
        golems.clear();
        pets.clear();
        bosses.clear();
        suppliers.clear();
    }
    
    public void clear() {
        removeAll(monsters);
        removeAll(sheep);
        removeAll(golems);
        removeAll(pets);
        removeAll(bosses.keySet());
        removeAll(suppliers.keySet());
        
        reset();
    }
    
    private void removeAll(Collection<? extends LivingEntity> collection) {
        for (LivingEntity e : collection) {
            if (e != null) {
                e.remove();
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
    
    public Set<Wolf> getPets() {
        return pets;
    }
    
    public void addPet(Wolf w) {
        pets.add(w);
    }
    
    public boolean hasPet(Entity e) {
        return pets.contains(e);
    }
    
    public void removePets(Player p) {
        for (Wolf w : pets) {
            if (w == null || !(w.getOwner() instanceof Player) || !((Player) w.getOwner()).getName().equals(p.getName()))
                continue;
            
            w.setOwner(null);
            w.remove();
        }
    }
    
    public void addSupplier(LivingEntity e, List<ItemStack> drops) {
        suppliers.put(e, drops);
    }
    
    public List<ItemStack> getLoot(Entity e) {
        return suppliers.get(e);
    }
    
    public MABoss addBoss(LivingEntity e, int maxHealth) {
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
