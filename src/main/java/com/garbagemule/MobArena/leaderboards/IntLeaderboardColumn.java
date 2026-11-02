package com.garbagemule.MobArena.leaderboards;

import com.garbagemule.MobArena.ArenaPlayerStatistics;
import org.bukkit.Location;

import java.util.List;

public class IntLeaderboardColumn extends AbstractLeaderboardColumn
{
    public IntLeaderboardColumn(String statname, Location header, List<Location> signs) {
        super(statname, header, signs);
    }

    @Override
    public String getLine(ArenaPlayerStatistics stats) {
        return "" + stats.getInt(statname);
    }
}
