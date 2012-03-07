package com.garbagemule.MobArena.log;

import java.util.List;
import java.util.Map;

import org.bukkit.inventory.ItemStack;

import com.garbagemule.MobArena.util.MutableInt;

public interface LogSessionBuilder
{
    /**
     * Builds the start time of this session.
     * The method should be called as soon as the session starts, or as soon
     * as possible right after for the most precise results.
     */
    public void buildStartTime();
    
    /**
     * Builds the start time of this session.
     * The method should be called as soon as the session ends, or as soon as
     * possible right after for the most precise results.
     * @param end the start time
     */
    public void buildEndTime();
    
    /**
     * Builds the duration of this session by creating a duration string on
     * the form HH:MM:SS using the provided start and end times.
     * PRECONDITION: buildStartTime() and buildEndTime() must be called before
     * calling this method.
     */
    public void buildDuration();
    
    /**
     * Builds the last reached wave of this session.
     * @param lastWave the wave number of the last reached wave
     */
    public void buildLastWave(int lastWave);
    
    /**
     * Builds the number of players that participated in this session.
     * @param amount the amount of players
     */
    public void buildNumberOfPlayers(int amount);
    
    /**
     * Builds the distribution of players over all classes.
     * @param classDistribution class names mapped to number of players per class
     */
    public void buildClassDistribution(Map<String,MutableInt> classDistribution);
    
    /**
     * Builds a player entry when a player has left/died.
     * @param entry the log entry to build
     * @param rewards a list of rewards given to the player in question
     */
    public void buildPlayerEntry(ArenaLogPlayerEntry entry, List<ItemStack> rewards);
    
    /**
     * Finalizes the session log, possibly writing it to disk or updating a database.
     * The method should only be called when no more changes are expected.
     * Behavior is undefined for the other methods after calling this method.
     */
    public void finalize();
}
