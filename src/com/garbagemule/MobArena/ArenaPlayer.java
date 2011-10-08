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
    
    protected boolean isDead, inArena, inLobby, inSpec, isReady;
    
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
    
    /**
     * Check if the player is "dead", i.e. died or not.
     * @return true, if the player is either a spectator or played and died, false otherwise
     */
    public boolean isDead()
    {
        return isDead;
    }
    
    /**
     * Set the player's death status.
     * @param value true, if the player is dead, false otherwise
     */
    public void setDead(boolean value)
    {
        isDead = value;
    }
    
    public ArenaPlayerStatistics getStats() { return stats; }
}
