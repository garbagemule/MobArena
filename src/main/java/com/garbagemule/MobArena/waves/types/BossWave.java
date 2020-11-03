package com.garbagemule.MobArena.waves.types;

import com.garbagemule.MobArena.Msg;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.things.Thing;
import com.garbagemule.MobArena.things.ThingPicker;
import com.garbagemule.MobArena.waves.AbstractWave;
import com.garbagemule.MobArena.waves.BossAbilityThread;
import com.garbagemule.MobArena.waves.MABoss;
import com.garbagemule.MobArena.waves.MACreature;
import com.garbagemule.MobArena.waves.Wave;
import com.garbagemule.MobArena.waves.ability.Ability;
import com.garbagemule.MobArena.waves.ability.AbilityInfo;
import com.garbagemule.MobArena.waves.enums.BossHealth;
import com.garbagemule.MobArena.waves.enums.WaveType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BossWave extends AbstractWave
{
    private String bossName;

    private MACreature monster;
    private Set<MABoss> bosses;

    private boolean useHealthMultiplier;
    private int healthMultiplier;
    private int flatHealth;

    private List<Ability> abilities;
    private boolean activated, abilityAnnounce;

    private int abilityInterval;

    private ThingPicker reward;
    private List<ItemStack> drops;

    public BossWave(MACreature monster) {
        this.monster   = monster;
        this.bosses    = new HashSet<>();
        this.abilities = new ArrayList<>();
        this.activated = false;
        this.abilityAnnounce = false;
        this.setType(WaveType.BOSS);

        this.useHealthMultiplier = true;
        this.healthMultiplier = 0;
        this.flatHealth = 0;
    }

    @Override
    public Map<MACreature,Integer> getMonstersToSpawn(int wave, int playerCount, Arena arena) {
        Map<MACreature,Integer> result = new HashMap<>();
        result.put(monster, 1);
        return result;
    }

    public String getBossName() {
        return bossName;
    }

    public void setBossName(String bossName) {
        this.bossName = bossName;
    }

    public int getMaxHealth(int playerCount) {
        if (useHealthMultiplier) {
            return (playerCount + 1) * 20 * healthMultiplier;
        }
        return flatHealth;
    }

    public void setHealth(BossHealth health) {
        this.healthMultiplier = health.getMultiplier();
        this.useHealthMultiplier = true;
    }

    public void setFlatHealth(int flatHealth) {
        this.flatHealth = flatHealth;
        this.useHealthMultiplier = false;
    }

    public void addMABoss(MABoss boss) {
        bosses.add(boss);
    }

    public Set<MABoss> getMABosses() {
        Set<MABoss> result = new HashSet<>();
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

    public ThingPicker getReward() {
        return reward;
    }

    public void setReward(ThingPicker reward) {
        this.reward = reward;
    }

    public List<ItemStack> getDrops() {
        return drops;
    }

    public void setDrops(List<ItemStack> drops) {
        this.drops = drops;
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
            arena.announce(Msg.WAVE_BOSS_ABILITY, info.name());
        }
    }

    public Wave copy() {
        BossWave result = new BossWave(this.monster);
        for (Ability ability : this.abilities) {
            result.addBossAbility(ability);
        }
        result.abilityInterval = this.abilityInterval;
        result.abilityAnnounce = this.abilityAnnounce;
        result.useHealthMultiplier = this.useHealthMultiplier;
        result.healthMultiplier = this.healthMultiplier;
        result.flatHealth = this.flatHealth;
        result.reward = this.reward;
        result.drops = this.drops;
        result.bossName = this.bossName;

        // From AbstractWave
        result.setAmountMultiplier(getAmountMultiplier());
        result.setHealthMultiplier(getHealthMultiplier());
        result.setName(getName());
        result.setSpawnpoints(getSpawnpoints());
        result.setEffects(getEffects());
        return result;
    }
}
