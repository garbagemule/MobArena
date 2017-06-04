package com.garbagemule.MobArena.leaderboards;

import com.garbagemule.MobArena.ArenaPlayerStatistics;
import org.bukkit.block.Sign;

import java.util.List;

public interface LeaderboardColumn
{
    /**
     * Update all the signs in this column to the current values
     * of the player stat associated with this column.
     */
    void update(List<ArenaPlayerStatistics> stats);
    
    /**
     * Get the String representation of the stat in question.
     * The line is calculated by simply calling the appropriate
     * getter on the ArenaPlayerStatistics object.
     * @param stats an ArenaPlayerStatistics object
     * @return the String representation of the stat in question
     */
    String getLine(ArenaPlayerStatistics stats);
    
    /**
     * Clear the text on all the signs in the column.
     */
    void clear();
    
    /**
     * Get the top sign of the column.
     * The top sign displays the stat name.
     * @return the top sign of the column
     */
    Sign getHeader();
    
    /**
     * Get all signs in the column (minus the header).
     * @return all signs in the column (minus the header)
     */
    List<Sign> getSigns();
}
