package com.garbagemule.MobArena.leaderboards;

import java.lang.reflect.Field;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;

import com.garbagemule.MobArena.ArenaPlayerStatistics;
import com.garbagemule.MobArena.util.TextUtils;

public class LeaderboardColumn
{
    private Field field;
    private boolean isPlayerName;
    private Sign header;
    private List<Sign> signs;
    
    private LeaderboardColumn(String statname, Sign header, List<Sign> signs) throws Exception
    {
        this.field        = ArenaPlayerStatistics.class.getDeclaredField(statname);
        this.isPlayerName = statname.equals("playerName");
        this.header       = header;
        this.signs        = signs;
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
            if (isPlayerName)
            {
                for (int i = 0; i < range; i++)
                {
                    // Grab the right sign.
                    Sign s = signs.get(i/4);
                    
                    // Get the stat value.
                    field.setAccessible(true);
                    ArenaPlayerStatistics aps = stats.get(i);
                    Object o = field.get(aps);
                    field.setAccessible(false);
                    String name = aps.getArenaPlayer().isDead() ? o.toString() : ChatColor.GREEN + o.toString();
                    
                    // Set the value on the right line.
                    s.setLine(i % 4, TextUtils.truncate(name));
                    s.update();
                }
            }
            else
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
        }
        catch (Exception e) { e.printStackTrace(); }
    }
    
    public void clear()
    {
        for (Sign s : signs)
        {
            s.setLine(0, "");
            s.setLine(1, "");
            s.setLine(2, "");
            s.setLine(3, "");
            s.update();
        }
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
