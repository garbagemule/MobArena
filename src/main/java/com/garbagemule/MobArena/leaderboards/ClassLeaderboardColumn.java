package com.garbagemule.MobArena.leaderboards;

import com.garbagemule.MobArena.ArenaPlayerStatistics;
import org.bukkit.Location;

import java.util.List;

public class ClassLeaderboardColumn extends AbstractLeaderboardColumn
{
    public ClassLeaderboardColumn(String statname, Location header, List<Location> signs) {
        super(statname, header, signs);
    }

    @Override
    public String getLine(ArenaPlayerStatistics stats) {
        return stats.getClassName();
    }
}
