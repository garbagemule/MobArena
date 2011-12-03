package com.garbagemule.MobArena;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ArenaPlayer
{
    public MobArena plugin;
    public Player player;
    public String className;
    public Arena arena;
    public List<ItemStack> rewards;
    public List<Block> blocks;
    
    private ArenaPlayerStatistics stats;
    
    protected boolean inArena, inLobby, inSpec, isReady;
    
    public ArenaPlayer(Player player, Arena arena, MobArena plugin)
    {
        this.player = player;
        this.arena  = arena;
        this.plugin = plugin;
        
        className   = arena.classMap.get(player);
        rewards     = new LinkedList<ItemStack>();
        blocks      = new LinkedList<Block>();
        
        stats = new ArenaPlayerStatistics(this);
    }
    
    public Player getPlayer()    { return player; }
    public Arena getArena()      { return arena;  }
    public String getClassName() { return className; }
    public ArenaPlayerStatistics getStats() { return stats; }
}
