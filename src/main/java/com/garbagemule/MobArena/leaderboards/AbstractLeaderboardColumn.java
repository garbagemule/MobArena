package com.garbagemule.MobArena.leaderboards;

import com.garbagemule.MobArena.ArenaPlayerStatistics;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;

import java.util.List;

public abstract class AbstractLeaderboardColumn implements LeaderboardColumn
{
    protected String statname;
    private Location header;
    private List<Location> signs;

    public AbstractLeaderboardColumn(String statname, Location header, List<Location> signs) {
        this.statname     = statname;
        this.header       = header;
        this.signs        = signs;
    }

    public void update(List<ArenaPlayerStatistics> stats) {
        // Make sure the stats will fit on the signs.
        int range = Math.min(stats.size(), signs.size()*4);

        for (int i = 0; i < range; i++) {
            // Grab the right sign.
            Location l = signs.get(i / 4);
            BlockState state = l.getBlock().getState();
            if (!(state instanceof Sign)) {
                continue;
            }
            Sign s = (Sign) state;

            // Call the template method.
            String value = getLine(stats.get(i));

            // And set the line
            s.setLine(i % 4, value);
            s.update();
        }
    }

    public abstract String getLine(ArenaPlayerStatistics stats);

    public void clear() {
        for (Location l : signs) {
            BlockState state = l.getBlock().getState();
            if (!(state instanceof Sign)) {
                continue;
            }
            Sign s = (Sign) state;
            s.setLine(0, "");
            s.setLine(1, "");
            s.setLine(2, "");
            s.setLine(3, "");
            s.update();
        }
    }

    public Location getHeader() {
        return header;
    }

    public List<Location> getSigns() {
        return signs;
    }
}
