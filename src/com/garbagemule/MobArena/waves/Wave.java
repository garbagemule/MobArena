package com.garbagemule.MobArena.waves;

import java.util.List;
import java.util.Map;

import org.bukkit.Location;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.enums.*;

public interface Wave
{
    /**
     * Populate a map of which monsters and how many of each this
     * wave should spawn, given the wave number, player count and
     * an arena object.
     * @param wave the current wave number
     * @param playerCount the amount of players
     * @param arena an Arena
     * @return a collection of MACreatures and how many of each to spawn
     */
    public Map<MACreature,Integer> getMonstersToSpawn(int wave, int playerCount, Arena arena);
    
    /**
     * Get a list of spawnpoints upon which the monsters of this
     * wave may be spawned. Note that it is expected that the
     * getMonstersToSpawn() method is called first, and that each
     * monster is then spawned on a location given in the list
     * from this method.
     * @param arena an Arena
     * @return a list of valid spawnpoints
     */
    public List<Location> getSpawnpoints(Arena arena);
    
    /**
     * Set the list of spawnpoints on which the monsters of this
     * wave may be spawned. If the value is null, all spawnpoints
     * of the arena region will be considered.
     * @param spawnpoints a list of spawnpoints
     */
    public void setSpawnpoints(List<Location> spawnpoints);
    
    /**
     * Announce to all players that this wave is spawning.
     * @param arena an arena
     * @param wave a wave number
     */
    public void announce(Arena arena, int wave);
    
    /**
     * Get the wave's name.
     * @return The name
     */
    public String getName();

    /**
     * Set the name of this wave.
     * @param name a name
     */
    public void setName(String name);

    /**
     * Get the branch type.
     * @return The WaveBranch of this Wave.
     */
    public WaveBranch getBranch();

    /**
     * Set the type of branch
     * @param branch recurrent or single
     */
    public void setBranch(WaveBranch branch);
    
    /**
     * Get the type of wave.
     * @return a WaveType
     */
    public WaveType getType();

    /**
     * Set the type of wave.
     * @param type a WaveType
     */
    public void setType(WaveType type);
    
    /**
     * Get the first wave this Wave instance may spawn on.
     * @return a wave number
     */
    public int getFirstWave();

    /**
     * Set the first wave this Wave instance may spawn on.
     * @param firstWave a wave number
     */
    public void setFirstWave(int firstWave);
    
    /**
     * Get the wave's frequency, i.e. wave number "modulo"
     * @return a frequency
     */
    public int getFrequency();
    
    /**
     * Set the wave's frequency
     * @param frequency a frequency
     */
    public void setFrequency(int frequency);
    
    /**
     * Get the wave's priority value.
     * @return a priority
     */
    public int getPriority();
    
    /**
     * Set the wave's priority.
     * @param priority a priority
     */
    public void setPriority(int priority);
    
    /**
     * Get the wave's health multiplier.
     * @return The health multiplier
     */
    public double getHealthMultiplier();
    
    /**
     * Get the wave's health multiplier.
     * @param healthMultiplier a double in the range ]0;1]
     */
    public void setHealthMultiplier(double healthMultiplier);
    
    /**
     * Get the wave's amount multiplier.
     * @return The amount multiplier
     */
    public double getAmountMultiplier();
    
    /**
     * Set the wave's amount multiplier.
     * @param amountMultiplier a positive double
     */
    public void setAmountMultiplier(double amountMultiplier);
    
    /**
     * Check if this wave matches the wave number.
     * The SingleWave class does a simple check if its wave == the parameter.
     * The RecurrentWave class is more complex in that it needs to do some
     * calculations based on the initial wave and the frequency.
     * @param wave The current wave number
     * @return true, if the wave should spawn, false otherwise
     */
    public boolean matches(int wave);
}