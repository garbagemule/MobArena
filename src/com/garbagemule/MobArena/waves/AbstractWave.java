package com.garbagemule.MobArena.waves;

import java.util.List;
import java.util.Map;

import org.bukkit.Location;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.enums.*;

public abstract class AbstractWave implements Wave
{
    private String name;
    
    private WaveBranch branch; // recurrent, single
    private WaveType   type;   // default, special, swarm, boss
    
    private double healthMultiplier, amountMultiplier;
    
    private int firstWave, frequency, priority;
    
    private List<Location> spawnpoints;
    
    @Override
    public abstract Map<MACreature, Integer> getMonstersToSpawn(int wave, int playerCount, Arena arena);

    @Override
    public List<Location> getSpawnpoints(Arena arena) {
        return WaveUtils.getValidSpawnpoints(arena, spawnpoints, arena.getPlayersInArena());
    }
    
    @Override
    public void setSpawnpoints(List<Location> spawnpoints) {
        this.spawnpoints = spawnpoints;
    }
    
    @Override
    public void announce(Arena arena, int wave) {
        type.announce(arena, wave);
    }

    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public WaveBranch getBranch() {
        return branch;
    }

    @Override
    public void setBranch(WaveBranch branch) {
        this.branch = branch;
    }

    @Override
    public WaveType getType() {
        return type;
    }
    
    @Override
    public void setType(WaveType type) {
        this.type = type;
    }

    @Override
    public int getFirstWave() {
        return firstWave;
    }

    @Override
    public void setFirstWave(int firstWave) {
        this.firstWave = firstWave;
    }

    @Override
    public int getFrequency() {
        return frequency;
    }

    @Override
    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public double getHealthMultiplier() {
        return healthMultiplier;
    }
    
    @Override
    public void setHealthMultiplier(double healthMultiplier) {
        this.healthMultiplier = healthMultiplier;
    }

    @Override
    public double getAmountMultiplier() {
        return amountMultiplier;
    }
    
    @Override
    public void setAmountMultiplier(double amountMultiplier) {
        this.amountMultiplier = amountMultiplier;
    }

    @Override
    public boolean matches(int wave) {
        return branch.matches(wave, this);
    }
}
