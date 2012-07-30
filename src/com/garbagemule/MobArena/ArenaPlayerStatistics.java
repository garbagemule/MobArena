package com.garbagemule.MobArena;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import com.garbagemule.MobArena.ArenaPlayer;
import com.garbagemule.MobArena.ArenaPlayerStatistics;
import com.garbagemule.MobArena.MobArena;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.util.MutableInt;

public class ArenaPlayerStatistics
{
    private ArenaPlayer player;
    private String playerName, className;
    private Map<String, MutableInt> ints;

    public ArenaPlayerStatistics(ArenaPlayer player) {
        this.player = player;
        this.playerName = player.getPlayer().getName();
        this.className = player.getArenaClass().getLowercaseName();
        reset();
    }

    public void reset() {
        if (ints == null) {
            ints = new HashMap<String, MutableInt>();
        }

        ints.clear();

        ints.put("kills", new MutableInt());
        ints.put("dmgDone", new MutableInt());
        ints.put("dmgTaken", new MutableInt());
        ints.put("swings", new MutableInt());
        ints.put("hits", new MutableInt());
        ints.put("lastWave", new MutableInt());
    }

    public ArenaPlayerStatistics(Player p, Arena arena, MobArena plugin) {
        this(new ArenaPlayer(p, arena, plugin));
    }

    public ArenaPlayer getArenaPlayer() {
        return player;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getClassName() {
        return className;
    }

    public int getInt(String s) {
        return ints.get(s).value();
    }

    public void inc(String s) {
        ints.get(s).inc();
    }

    public void add(String s, int amount) {
        ints.get(s).add(amount);
    }

    public static Comparator<ArenaPlayerStatistics> killComparator() {
        return new Comparator<ArenaPlayerStatistics>() {
            public int compare(ArenaPlayerStatistics s1, ArenaPlayerStatistics s2) {
                int s1kills = s1.getInt("kills");
                int s2kills = s2.getInt("kills");

                if (s1kills == s2kills)
                    return 0;

                return (s1kills > s2kills ? -1 : 1);
            }
        };
    }

    public static Comparator<ArenaPlayerStatistics> waveComparator() {
        return new Comparator<ArenaPlayerStatistics>() {
            public int compare(ArenaPlayerStatistics s1, ArenaPlayerStatistics s2) {
                int result = compareWaves(s1, s2);
                if (result != 0)
                    return result;

                return compareKills(s1, s2);
            }
        };
    }

    public static Comparator<ArenaPlayerStatistics> dmgDoneComparator() {
        return new Comparator<ArenaPlayerStatistics>() {
            public int compare(ArenaPlayerStatistics s1, ArenaPlayerStatistics s2) {
                int s1dmgDone = s1.getInt("dmgDone");
                int s2dmgDone = s2.getInt("dmgDone");

                if (s1dmgDone == s2dmgDone)
                    return 0;

                return (s1dmgDone > s2dmgDone ? -1 : 1);
            }
        };
    }

    private static int compareKills(ArenaPlayerStatistics s1, ArenaPlayerStatistics s2) {
        int s1kills = s1.getInt("kills");
        int s2kills = s2.getInt("kills");

        if (s1kills == s2kills)
            return 0;

        return (s1kills > s2kills ? -1 : 1);
    }

    private static int compareWaves(ArenaPlayerStatistics s1, ArenaPlayerStatistics s2) {
        int s1wave = s1.getInt("lastWave");
        int s2wave = s2.getInt("lastWave");

        if (s1wave == s2wave)
            return 0;

        return (s1wave > s2wave ? -1 : 1);
    }
}
