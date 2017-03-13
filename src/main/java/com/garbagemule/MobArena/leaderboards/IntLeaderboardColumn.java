package com.garbagemule.MobArena.leaderboards;

import com.garbagemule.MobArena.ArenaPlayerStatistics;
import org.bukkit.block.Sign;

import java.util.List;

public class IntLeaderboardColumn extends AbstractLeaderboardColumn
{
    public IntLeaderboardColumn(String statname, Sign header, List<Sign> signs) {
        super(statname, header, signs);
    }

    @Override
    public String getLine(ArenaPlayerStatistics stats) {
        return "" + stats.getInt(statname);
    }
}
