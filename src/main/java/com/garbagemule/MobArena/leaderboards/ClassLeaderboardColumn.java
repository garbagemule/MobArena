package com.garbagemule.MobArena.leaderboards;

import com.garbagemule.MobArena.ArenaPlayerStatistics;
import org.bukkit.block.Sign;

import java.util.List;

public class ClassLeaderboardColumn extends AbstractLeaderboardColumn
{
    public ClassLeaderboardColumn(String statName, Sign header, List<Sign> signs) {
        super(statName, header, signs);
    }

    @Override
    public String getLine(ArenaPlayerStatistics stats) {
        return stats.getClassName();
    }
}
