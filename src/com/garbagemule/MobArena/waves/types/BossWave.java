package com.garbagemule.MobArena.waves.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.garbagemule.MobArena.Messenger;
import com.garbagemule.MobArena.Msg;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.AbstractWave;
import com.garbagemule.MobArena.waves.BossAbilityThread;
import com.garbagemule.MobArena.waves.MABoss;
import com.garbagemule.MobArena.waves.MACreature;
import com.garbagemule.MobArena.waves.ability.Ability;
import com.garbagemule.MobArena.waves.ability.AbilityInfo;
import com.garbagemule.MobArena.waves.enums.*;

public class BossWave extends AbstractWave
{
    private MACreature monster;
    private Set<MABoss> bosses;
    private BossHealth health;
    
    private List<Ability> abilities;
    private boolean activated, abilityAnnounce;
    
    private int abilityInterval;
    
    public BossWave(MACreature monster) {
        this.monster   = monster;
        this.bosses    = new HashSet<MABoss>();
        this.abilities = new ArrayList<Ability>();
        this.activated = false;
        this.abilityAnnounce = false;
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
    
    public void addBossAbility(Ability ability) {
        abilities.add(ability);
    }
    
    public int getAbilityInterval() {
        return abilityInterval;
    }
    
    public void setAbilityInterval(int abilityInterval) {
        this.abilityInterval = abilityInterval;
    }
    
    public boolean getAbilityAnnounce() {
        return abilityAnnounce;
    }
    
    public void setAbilityAnnounce(boolean abilityAnnounce) {
        this.abilityAnnounce = abilityAnnounce;
    }
    
    public void activateAbilities(Arena arena) {
        if (activated) {
            return;
        }
        
        BossAbilityThread bat = new BossAbilityThread(this, abilities, arena);
        arena.scheduleTask(bat, 100);
        activated = true;
    }
    
    public void announceAbility(Ability ability, MABoss boss, Arena arena) {
        if(getAbilityAnnounce()) {
            AbilityInfo info = ability.getClass().getAnnotation(AbilityInfo.class);
            Messenger.tellAll(arena, Msg.WAVE_BOSS_ABILITY, info.name());
        }
    }
}
