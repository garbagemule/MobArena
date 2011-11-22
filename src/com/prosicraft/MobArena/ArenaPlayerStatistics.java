package com.prosicraft.MobArena;

import java.util.Comparator;

import org.bukkit.entity.Player;

public class ArenaPlayerStatistics
{
    private String playerName, className;
    private ArenaPlayer player;
    public int kills, dmgDone, dmgTaken, swings, hits, lastWave;
    
    public ArenaPlayerStatistics(ArenaPlayer player)
    {
        this.player     = player;
        this.playerName = player.getPlayer().getName();
        this.className  = player.getClassName();
    }
    
    public ArenaPlayerStatistics(Player p, Arena arena, MobArena plugin)
    {
        this(new ArenaPlayer(p, arena, plugin));
    }
    
    public ArenaPlayer getArenaPlayer()
    {
        return player;
    }
    
    public static Comparator<ArenaPlayerStatistics> killComparator()
    {
        return new Comparator<ArenaPlayerStatistics>()
            {
                public int compare(ArenaPlayerStatistics s1, ArenaPlayerStatistics s2)
                {
                    if (s1.kills > s2.kills)
                        return -1;
                    else if (s1.kills < s2.kills)
                        return 1;
                    return 0;
                }
            };
    }
    
    private static int compareKills(ArenaPlayerStatistics s1, ArenaPlayerStatistics s2)
    {
        if (s1.kills > s2.kills)
            return -1;
        else if (s1.kills < s2.kills)
            return 1;
        return 0;
    }
    
    public static Comparator<ArenaPlayerStatistics> waveComparator()
    {
        return new Comparator<ArenaPlayerStatistics>()
            {
                public int compare(ArenaPlayerStatistics s1, ArenaPlayerStatistics s2)
                {
                    int result = compareWaves(s1, s2);
                    if (result != 0) return result;
                    
                    return compareKills(s1, s2);
                }
            };
    }
    
    private static int compareWaves(ArenaPlayerStatistics s1, ArenaPlayerStatistics s2)
    {
        if (s1.lastWave > s2.lastWave)
            return -1;
        else if (s1.lastWave < s2.lastWave)
            return 1;
        return 0;
    }
    
    public static Comparator<ArenaPlayerStatistics> dmgDoneComparator()
    {
        return new Comparator<ArenaPlayerStatistics>()
            {
                public int compare(ArenaPlayerStatistics s1, ArenaPlayerStatistics s2)
                {
                    if (s1.dmgDone > s2.dmgDone)
                        return -1;
                    else if (s1.dmgDone < s2.dmgDone)
                        return 1;
                    return 0;
                }
            };
    }
}
