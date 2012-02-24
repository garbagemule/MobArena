package com.garbagemule.MobArena.waves.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.AbstractWave;
import com.garbagemule.MobArena.waves.BossAbility;
import com.garbagemule.MobArena.waves.BossAbilityThread;
import com.garbagemule.MobArena.waves.MABoss;
import com.garbagemule.MobArena.waves.MACreature;
import com.garbagemule.MobArena.waves.enums.*;
import com.garbagemule.MobArena.MobArena;

public class BossWave extends AbstractWave
{
    private MACreature monster;
    private Set<MABoss> bosses;
    private BossHealth health;
    
    private List<BossAbility> abilities;
    private boolean activated;
    
    private int abilityInterval;
    
    public BossWave(MACreature monster) {
        this.monster   = monster;
        this.bosses    = new HashSet<MABoss>();
        this.abilities = new ArrayList<BossAbility>();
        this.activated = false;
        this.setType(WaveType.BOSS);
    }
    
    @Override
    public Map<MACreature,Integer> getMonstersToSpawn(int wave, int playerCount, Arena arena) {
        Map<MACreature,Integer> result = new HashMap<MACreature,Integer>();
        result.put(monster, 1);
        return result;
    }
    
    public int getMaxHealth(int playerCount) {
        return health.getMax(playerCount);
    }
    
    public void setHealth(BossHealth health) {
        this.health = health;
    }
    
    public void addMABoss(MABoss boss) {
        bosses.add(boss);
    }
    
    public Set<MABoss> getMABosses() {
        Set<MABoss> result = new HashSet<MABoss>();
        for (MABoss b : bosses) {
            if (!b.isDead()) {
                result.add(b);
            }
        }
        return result;
    }
    
    public void addBossAbility(BossAbility ability) {
        abilities.add(ability);
    }
    
    public int getAbilityInterval() {
        return abilityInterval;
    }
    
    public void setAbilityInterval(int abilityInterval) {
        this.abilityInterval = abilityInterval;
    }
    
    public void activateAbilities(Arena arena) {
        if (activated) {
            return;
        }
        
        BossAbilityThread bat = new BossAbilityThread(this, abilities, arena);
        scheduleTask(arena, bat, 100);
        activated = true;
    }
    
    public void scheduleTask(Arena arena, Runnable r, int delay) {
        MobArena plugin = arena.getPlugin();
        
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(
                plugin,
                r,
                delay);
    }
}
