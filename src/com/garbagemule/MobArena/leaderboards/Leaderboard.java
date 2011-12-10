package com.garbagemule.MobArena.leaderboards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
//import org.bukkit.util.config.Configuration;

import com.garbagemule.MobArena.Arena;
import com.garbagemule.MobArena.ArenaPlayer;
import com.garbagemule.MobArena.ArenaPlayerStatistics;
import com.garbagemule.MobArena.MobArena;
import com.garbagemule.MobArena.util.Config;

public class Leaderboard
{
    private MobArena plugin;
    private Arena    arena;
    
    private Location  topLeft;
    private Sign      topLeftSign;
    private BlockFace direction;
    private int rows, cols, trackingId;
    
    private List<LeaderboardColumn> boards;
    private List<ArenaPlayerStatistics> stats;
    
    private boolean isValid;
    
    /**
     * Default constructor.
     * Creates a new leaderboard with no signs or locations or anything.
     * @param plugin MobArena instance.
     * @param arena The arena to which this leaderboard belongs.
     */
    public Leaderboard(MobArena plugin, Arena arena)
    {
        this.plugin = plugin;
        this.arena  = arena;
        this.boards = new ArrayList<LeaderboardColumn>();
        this.stats  = new ArrayList<ArenaPlayerStatistics>();
    }
    
    /**
     * Config constructor.
     * Used to create a leaderboard from the location persisted in the config-file.
     * @param plugin MobArena instance.
     * @param arena The arena to which this leaderboard belongs.
     * @param config The config-file in which the location is specified.
     */
    public Leaderboard(MobArena plugin, Arena arena, Config config)
    {
        this(plugin, arena);
        
        // Grab the coords from the config-file.
        String coords = config.getString("arenas." + arena.configName() + ".coords.leaderboard", null);
        
        if (coords != null)
        {
            // Grab the top left sign.
            topLeft = Config.parseLocation(arena.getWorld(), coords);

            // If it is a sign, validate.
            if (topLeft.getBlock().getState() instanceof Sign)
                isValid = isGridWellFormed();
        }
    }
    
    /**
     * Location constructor.
     * Used to create a leaderboard on-the-fly from the location from the SignChangeEvent.
     * @param plugin MobArena instance.
     * @param arena The arena to which this leaderboard belongs.
     * @param topLeft The location at which the main leaderboard sign exists.
     */
    public Leaderboard(MobArena plugin, Arena arena, Location topLeft)
    {
        this(plugin, arena);
        
        if (!(topLeft.getBlock().getState() instanceof Sign))
            throw new IllegalArgumentException("Block must be a sign!");
        
        this.topLeft = topLeft;
    }
    
    /**
     * Grab all adjacent signs and register the individual columns.
     */
    public void initialize()
    {
        if (!isGridWellFormed())
            return;
        
        initializeBoards();
        initializeStats();
        clear();
    }
    
    public void clear()
    {
        for (LeaderboardColumn column : boards)
            column.clear();
    }
    
    public void update()
    {
        Collections.sort(stats, ArenaPlayerStatistics.waveComparator());
        
        for (LeaderboardColumn column : boards)
            column.update(stats);
    }
    
    public void startTracking()
    {
        trackingId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin,
            new Runnable()
            {
                public void run()
                {
                    update();
                }
            }, 100, 100);
    }
    
    public void stopTracking()
    {
        Bukkit.getServer().getScheduler().cancelTask(trackingId);
    }
    
    /**
     * Check if the leaderboards grid is well-formed.
     * @return true, if the grid is well-formed, false otherwise.
     */
    private boolean isGridWellFormed()
    {
        if (topLeft == null)
            return false;
        
        BlockState state = topLeft.getBlock().getState();
        
        if (!(state instanceof Sign))
        {
            MobArena.error("Leaderboards for '" + arena.configName() + "' could not be established!");
            return false;
        }
        
        // Grab the top left sign and set up a copy for parsing.
        this.topLeftSign = (Sign) state;
        Sign current = this.topLeftSign;
        
        // Calculate matrix dimensions.
        this.direction = getRightDirection(current);
        this.rows = getSignCount(current, BlockFace.DOWN);
        this.cols = getSignCount(current, direction);
        
        // Require at least 2x2 to be valid
        if (rows <= 1 || cols <= 1)
            return false;
        
        // Get the left-most sign in the current row.
        Sign first = getAdjacentSign(current, BlockFace.DOWN);
        
        for (int i = 1; i < rows; i++)
        {
            // Back to the first sign of the row.
            current = first;
            for (int j = 1; j < cols; j++)
            {
                // Grab the sign to the right, if not a sign, grid is ill-formed.
                current = getAdjacentSign(current, direction);
                if (current == null) return false;
            }
            
            // Hop down to the next row.
            first = getAdjacentSign(first, BlockFace.DOWN);
        }
        return true;
    }
    
    /**
     * Build the leaderboards.
     * Requires: The grid MUST be valid!
     */
    private void initializeBoards()
    {
        boards.clear();
        Sign header = this.topLeftSign;
        Sign current;
        
        do
        {
            // Strip the sign of any colors.
            String name = ChatColor.stripColor(header.getLine(2));
            
            // Grab the stat to track.
            Stats stat = Stats.getByFullName(name);
            if (stat == null) continue;
            
            // Create the list of signs
            List<Sign> signs = new ArrayList<Sign>();
            current = header;
            for (int i = 1; i < rows; i++)
            {
                current = getAdjacentSign(current, BlockFace.DOWN);
                signs.add(current);
            }
            
            // Create the column.
            LeaderboardColumn column = LeaderboardColumn.create(stat.getShortName(), header, signs);
            if (column == null) continue;
            this.boards.add(column);
        }
        while ((header = getAdjacentSign(header, direction)) != null);
    }
    
    private void initializeStats()
    {
        stats.clear();
        for (ArenaPlayer ap : arena.getArenaPlayerSet())
            stats.add(ap.getStats());
    }
    
    private int getSignCount(Sign s, BlockFace direction)
    {
        int i = 1;
        
        BlockState state = s.getBlock().getState();
        while ((state = state.getBlock().getRelative(direction).getState()) instanceof Sign)
            i++;
        
        return i;
    }
    
    private Sign getAdjacentSign(Sign s, BlockFace direction)
    {
        BlockState state = s.getBlock().getRelative(direction).getState();
        if (state instanceof Sign)
            return (Sign) state;
        return null;
    }
    
    private BlockFace getRightDirection(Sign s)
    {
        byte data = s.getRawData();

        if (data == 2) return BlockFace.NORTH;
        if (data == 3) return BlockFace.SOUTH;
        if (data == 4) return BlockFace.WEST;
        if (data == 5) return BlockFace.EAST;
        
        return null;
    }
    
    public boolean isValid()
    {
        return isValid;
    }
}
