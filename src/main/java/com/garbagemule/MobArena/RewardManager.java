package com.garbagemule.MobArena;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.things.Thing;
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
        
        for (Thing reward : rewards) {
            if (reward == null) {
                continue;
            }
            reward.giveTo(p);
        }
        rewarded.add(p);
    }
}