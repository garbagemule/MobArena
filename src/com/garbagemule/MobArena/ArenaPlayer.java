package com.garbagemule.MobArena;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ArenaPlayer
{
    public MobArena plugin;
    public Player player;
    public String className;
    public Arena arena;
    public List<ItemStack> rewards;
    
    protected boolean inArena, inLobby, inSpec, isReady;
    
    // Session fields.
    public int kills, dmgDone, dmgTaken, swings, hits, deaths, lastWave;
    public int flagCaps, flagAttempts, flagReturns; // BG: Capture the Pumpkin
    public int baseCaps; // BG: Domination
    
    // All-time fields.
    protected int totalKills, totalDmgDone, totalDmgTaken, totalSwings, totalHits, totalDeaths;
    protected int totalFlagCaps, totalFlagAttempts, totalFlagReturns; // BG: Capture the Pumpkin
    protected int totalBaseCaps; // BG: Domination
    
    public ArenaPlayer(Player player, Arena arena, MobArena plugin)
    {
        this.player = player;
        this.arena  = arena;
        this.plugin = plugin;
        
        className   = arena.classMap.get(player);
        rewards     = new LinkedList<ItemStack>();
    }
    
    public Player getPlayer()    { return player; }
    public Arena getArena()      { return arena;  }
    public String getClassName() { return className; }
}
