package com.garbagemule.MobArena.leaderboards;

import java.util.List;

import org.bukkit.block.Sign;

import com.garbagemule.MobArena.ArenaPlayerStatistics;

public class ClassLeaderboardColumn extends AbstractLeaderboardColumn
{
    public ClassLeaderboardColumn(String statname, Sign header, List<Sign> signs) {
        super(statname, header, signs);
    }

    @Override
    public String getLine(ArenaPlayerStatistics stats) {
        return stats.getClassName();
    }
}
