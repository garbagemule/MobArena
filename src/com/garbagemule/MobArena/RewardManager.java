package com.garbagemule.MobArena;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.garbagemule.MobArena.framework.Arena;

public class RewardManager
{
    @SuppressWarnings("unused")
    private MobArena plugin;
    @SuppressWarnings("unused")
    private Arena arena;
    private Map<Player,List<ItemStack>> players;
    private Set<Player> rewarded;
    
    public RewardManager(Arena arena) {
        this.plugin   = arena.getPlugin();
        this.arena    = arena;
        this.players  = new HashMap<Player,List<ItemStack>>();
        this.rewarded = new HashSet<Player>();
    }
    
    public void reset() {
        players.clear();
        rewarded.clear();
    }
    
    public void addReward(Player p, ItemStack stack) {
        if (!players.containsKey(p)) {
            players.put(p, new ArrayList<ItemStack>());
        }
        players.get(p).add(stack);
    }
    
    public List<ItemStack> getRewards(Player p) {
        List<ItemStack> rewards = players.get(p);
        return (rewards == null ? new ArrayList<ItemStack>(1) : Collections.unmodifiableList(rewards));
    }
    
    public void grantRewards(Player p) {
        if (rewarded.contains(p)) return;
        
        List<ItemStack> rewards = players.get(p);
        if (rewards == null) return;
        
        for (ItemStack stack : rewards) {
            if (stack == null) {
                continue;
            }
            
            if (stack.getTypeId() == MobArena.ECONOMY_MONEY_ID) {
                // plugin.giveMoney(p, stack.getAmount()); - removed to fix double money rewards
                continue;
            }
            
            p.getInventory().addItem(stack);
        }
        rewarded.add(p);
    }
}