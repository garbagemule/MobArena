package com.garbagemule.MobArena;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.things.Thing;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RewardManager
{
    private Map<Player,List<Thing>> players;
    private Set<Player> rewarded;
    private static final String PREFIX_LONG = "money:";
    private static final String PREFIX_SHORT = "$";

    public RewardManager(Arena arena) {
        this.players  = new HashMap<>();
        this.rewarded = new HashSet<>();
    }

    public void reset() {
        players.clear();
        rewarded.clear();
    }

    public void addReward(Player p, Thing thing) {
        if (!players.containsKey(p)) {
            players.put(p, new ArrayList<>());
        }
        players.get(p).add(thing);
    }

    public void grantRewards(Player p) {
        if (rewarded.contains(p)) return;

        List<Thing> rewards = players.get(p);
        if (rewards == null) return;
        Double moneyTotal = 0.00;

        for (Thing reward : rewards) {
            if (reward == null) {
                continue;
            }
            reward.giveTo(p);
            String moneyHolder = trimPrefix(reward.toString());
            if (moneyHolder == null) continue;
            if (moneyHolder.contains(",")) {
                moneyHolder = moneyHolder.replaceAll(",","");
            }
            moneyTotal += Double.parseDouble(moneyHolder);
        }
        p.sendMessage(ChatColor.GREEN + "You were rewarded with: " + ChatColor.YELLOW + "$" + moneyTotal);
        rewarded.add(p);
    }

    private String trimPrefix(String s) {
        if (s.startsWith(PREFIX_SHORT)) {
            return s.substring(PREFIX_SHORT.length());
        }
        if (s.startsWith(PREFIX_LONG)) {
            return s.substring(PREFIX_LONG.length());
        }
        return null;
    }
}
