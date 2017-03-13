package com.garbagemule.MobArena.leaderboards;

import com.garbagemule.MobArena.ArenaPlayerStatistics;
import org.bukkit.block.Sign;

import java.util.List;

public class PlayerLeaderboardColumn extends AbstractLeaderboardColumn
{
    public PlayerLeaderboardColumn(String statname, Sign header, List<Sign> signs) {
        super(statname, header, signs);
    }

    @Override
    public String getLine(ArenaPlayerStatistics stats) {
        return stats.getPlayerName();
    }
}
