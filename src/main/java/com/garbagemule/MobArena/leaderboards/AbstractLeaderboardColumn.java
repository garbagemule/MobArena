package com.garbagemule.MobArena.leaderboards;

import com.garbagemule.MobArena.ArenaPlayerStatistics;
import org.bukkit.block.Sign;

import java.util.List;

public abstract class AbstractLeaderboardColumn implements LeaderboardColumn
{
    protected String statName;
    private final Sign header;
    private final List<Sign> signs;

    public AbstractLeaderboardColumn(String statName, Sign header, List<Sign> signs) {
        this.statName     = statName;
        this.header       = header;
        this.signs        = signs;
    }

    public void update(List<ArenaPlayerStatistics> stats) {
        // Make sure the stats will fit on the signs.
        int range = Math.min(stats.size(), signs.size()*4);

        for (int i = 0; i < range; i++) {
            // Grab the right sign.
            Sign s = signs.get(i/4);

            // Call the template method.
            String value = getLine(stats.get(i));

            // And set the line
            s.setLine(i % 4, value);
            s.update();
        }
    }

    public abstract String getLine(ArenaPlayerStatistics stats);

    public void clear() {
        for (Sign s : signs) {
            s.setLine(0, "");
            s.setLine(1, "");
            s.setLine(2, "");
            s.setLine(3, "");
            s.update();
        }
    }

    public Sign getHeader() {
        return header;
    }

    public List<Sign> getSigns() {
        return signs;
    }
}
