package com.garbagemule.MobArena.leaderboards;

import java.util.List;

import org.bukkit.block.Sign;

import com.garbagemule.MobArena.ArenaPlayerStatistics;

public interface LeaderboardColumn
{
    /**
     * Update all the signs in this column to the current values
     * of the player stat associated with this column.
     */
    public void update(List<ArenaPlayerStatistics> stats);
    
    /**
     * Get the String representation of the stat in question.
     * The line is calculated by simply calling the appropriate
     * getter on the ArenaPlayerStatistics object.
     * @param stats an ArenaPlayerStatistics object
     * @return the String representation of the stat in question
     */
    public String getLine(ArenaPlayerStatistics stats);
    
    /**
     * Clear the text on all the signs in the column.
     */
    public void clear();
    
    /**
     * Get the top sign of the column.
     * The top sign displays the stat name.
     * @return the top sign of the column
     */
    public Sign getHeader();
    
    /**
     * Get all signs in the column (minus the header).
     * @return all signs in the column (minus the header)
     */
    public List<Sign> getSigns();
}
