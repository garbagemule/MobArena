package com.garbagemule.MobArena.log;

import java.util.Map;

import com.garbagemule.MobArena.util.MutableInt;

public interface LogTotalsBuilder
{
    /**
     * Store the start time of the current session.
     * Used to add to the total time played per player.
     */
    public void recordStartTime();
    
    /**
     * Update the total time played in this arena.
     * PRECONDITION: recordStartTime() must have been called first.
     */
    public void updateTimePlayed();
    
    /**
     * Increment the total number of sessions played.
     * The method should be called once per session and no more.
     */
    public void updateSessionsPlayed();
    
    /**
     * Update the last wave recorded to be the maximum of the current last wave
     * and the last wave of the last session.
     * @param wave last wave of the last session
     */
    public void updateLastWave(int lastWave);
    
    /**
     * Update the total distribution of players over all classes.
     * @param classDistribution class names mapped to number of players per class
     */
    public void updateClassDistribution(Map<String,MutableInt> classDistribution);
    
    /**
     * Update the totals for a specific player given a log entry.
     * @param entry a log entry
     */
    public void updatePlayerEntry(ArenaLogPlayerEntry entry);
    
    /**
     * Finish updating the totals. Calling this method makes all changes permanent
     * and resets the builder, making it ready for updates for the next session.
     */
    public void finish();
}
