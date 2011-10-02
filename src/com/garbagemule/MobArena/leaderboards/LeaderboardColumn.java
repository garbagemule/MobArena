package com.garbagemule.MobArena.leaderboards;

import java.lang.reflect.Field;
import java.util.List;

import org.bukkit.block.Sign;

import com.garbagemule.MobArena.ArenaPlayerStatistics;

public class LeaderboardColumn
{
    private Field field;
    private Sign header;
    private List<Sign> signs;
    
    private LeaderboardColumn(String statname, Sign header, List<Sign> signs) throws Exception
    {
        this.field  = ArenaPlayerStatistics.class.getDeclaredField(statname);
        this.header = header;
        this.signs  = signs;
    }
    
    /**
     * Safely create a new LeaderboardColumn.
     * Avoid the try-catch blocks by creating columns with this method.
     * @param statname The name of the stat to track
     * @param header The header sign
     * @param signs A list of signs
     * @return A new LeaderboardColumn, or null
     */
    public static LeaderboardColumn create(String statname, Sign header, List<Sign> signs)
    {
        try
        {
            return new LeaderboardColumn(statname, header, signs); 
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
    
    public void update(List<ArenaPlayerStatistics> stats)
    {
        // Make sure the stats will fit on the signs.
        int range = Math.min(stats.size(), signs.size()*4);

        try
        {
            for (int i = 0; i < range; i++)
            {
                // Grab the right sign.
                Sign s = signs.get(i/4);
                
                // Get the stat value.
                field.setAccessible(true);
                Object o = field.get(stats.get(i));
                field.setAccessible(false);
                
                // Set the value on the right line.
                s.setLine(i % 4, o.toString());
                s.update();
            }
        }
        catch (Exception e) { e.printStackTrace(); }
    }
    
    public Sign getHeader()
    {
        return header;
    }
    
    public List<Sign> getSigns()
    {
        return signs;
    }
}
