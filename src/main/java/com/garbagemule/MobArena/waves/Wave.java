package com.garbagemule.MobArena.waves;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.enums.WaveBranch;
import com.garbagemule.MobArena.waves.enums.WaveType;
import org.bukkit.Location;
import org.bukkit.potion.PotionEffect;

import java.util.List;
import java.util.Map;

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
    Map<MACreature,Integer> getMonstersToSpawn(int wave, int playerCount, Arena arena);

    /**
     * Get a list of spawnpoints upon which the monsters of this
     * wave may be spawned. Note that it is expected that the
     * getMonstersToSpawn() method is called first, and that each
     * monster is then spawned on a location given in the list
     * from this method.
     * @param arena an Arena
     * @return a list of valid spawnpoints
     */
    List<Location> getSpawnpoints(Arena arena);

    /**
     * Set the list of spawnpoints on which the monsters of this
     * wave may be spawned. If the value is null, all spawnpoints
     * of the arena region will be considered.
     * @param spawnpoints a list of spawnpoints
     */
    void setSpawnpoints(List<Location> spawnpoints);

    /**
     * Get a list of potion effects that the monsters of this wave
     * will be given when they spawn.
     *
     * @return a list of potion effects, may be empty
     */
    List<PotionEffect> getEffects();

    /**
     * Set the list of potion effects that the monsters of this wave
     * will be given when they spawn.
     *
     * @param effects a list of potion effects, must be non-null
     */
    void setEffects(List<PotionEffect> effects);

    /**
     * Announce to all players that this wave is spawning.
     * @param arena an arena
     * @param wave a wave number
     */
    void announce(Arena arena, int wave);

    /**
     * Get the wave's name.
     * @return The name
     */
    String getName();

    /**
     * Set the name of this wave.
     * @param name a name
     */
    void setName(String name);

    /**
     * Get the branch type.
     * @return The WaveBranch of this Wave.
     */
    WaveBranch getBranch();

    /**
     * Set the type of branch
     * @param branch recurrent or single
     */
    void setBranch(WaveBranch branch);

    /**
     * Get the type of wave.
     * @return a WaveType
     */
    WaveType getType();

    /**
     * Set the type of wave.
     * @param type a WaveType
     */
    void setType(WaveType type);

    /**
     * Get the first wave this Wave instance may spawn on.
     * @return a wave number
     */
    int getFirstWave();

    /**
     * Set the first wave this Wave instance may spawn on.
     * @param firstWave a wave number
     */
    void setFirstWave(int firstWave);

    /**
     * Get the wave's frequency, i.e. wave number "modulo"
     * @return a frequency
     */
    int getFrequency();

    /**
     * Set the wave's frequency
     * @param frequency a frequency
     */
    void setFrequency(int frequency);

    /**
     * Get the wave's priority value.
     * @return a priority
     */
    int getPriority();

    /**
     * Set the wave's priority.
     * @param priority a priority
     */
    void setPriority(int priority);

    /**
     * Get the wave's health multiplier.
     * @return The health multiplier
     */
    double getHealthMultiplier();

    /**
     * Get the wave's health multiplier.
     * @param healthMultiplier a double in the range ]0;1]
     */
    void setHealthMultiplier(double healthMultiplier);

    /**
     * Get the wave's amount multiplier.
     * @return The amount multiplier
     */
    double getAmountMultiplier();

    /**
     * Set the wave's amount multiplier.
     * @param amountMultiplier a positive double
     */
    void setAmountMultiplier(double amountMultiplier);

    /**
     * Get the wave's shadow blacklist.
     * @return a list of shadow wave names
     */
    String getShadowBlacklist();

    /**
     * Set the wave's shadow blacklist.
     * @param blacklist a list of shadow wave names
     */
    void setShadowBlacklist(String blacklist);

    /**
     * Check if this wave matches the wave number.
     * The SingleWave class does a simple check if its wave == the parameter.
     * The RecurrentWave class is more complex in that it needs to do some
     * calculations based on the initial wave and the frequency.
     * @param wave The current wave number
     * @return true, if the wave should spawn, false otherwise
     */
    boolean matches(int wave);

    /**
     * Make a copy of the wave.
     * <p>
     * This method is used by the WaveManager in the {@code next()} method to
     * ensure that boss waves in particular do not share state.
     *
     * @return a copy of the wave
     */
    Wave copy();
}
