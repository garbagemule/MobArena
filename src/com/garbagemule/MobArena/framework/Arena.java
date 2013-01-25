package com.garbagemule.MobArena.framework;

import java.util.*;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.garbagemule.MobArena.ArenaClass;
import com.garbagemule.MobArena.ArenaListener;
import com.garbagemule.MobArena.ArenaPlayer;
import com.garbagemule.MobArena.ClassLimitManager;
import com.garbagemule.MobArena.MASpawnThread;
import com.garbagemule.MobArena.MobArena;
import com.garbagemule.MobArena.MonsterManager;
import com.garbagemule.MobArena.RewardManager;
import com.garbagemule.MobArena.leaderboards.Leaderboard;
import com.garbagemule.MobArena.log.ArenaLog;
import com.garbagemule.MobArena.region.ArenaRegion;
import com.garbagemule.MobArena.repairable.Repairable;
import com.garbagemule.MobArena.util.config.ConfigSection;
import com.garbagemule.MobArena.util.inventory.InventoryManager;
import com.garbagemule.MobArena.waves.Wave;
import com.garbagemule.MobArena.waves.WaveManager;

public interface Arena
{
    /*/////////////////////////////////////////////////////////////////////////
    //
    //      NEW METHODS IN REFACTORING
    //
    /////////////////////////////////////////////////////////////////////////*/
    
    public ConfigSection getSettings();
    
    public World getWorld();
    
    public void setWorld(World world);
    
    public boolean isEnabled();
    
    public void setEnabled(boolean value);
    
    public boolean isProtected();
    
    public void setProtected(boolean value);
    
    public boolean isLogging();
    
    public void setLogging(boolean value);
    
    public boolean isRunning();
    
    public boolean inEditMode();
    
    public void setEditMode(boolean value);
    
    public Material getClassLogo(String classname);
    
    public List<ItemStack> getEntryFee();
    
    public Set<Map.Entry<Integer,List<ItemStack>>> getEveryWaveEntrySet();

    public List<ItemStack> getAfterWaveReward(int wave);
    
    public Set<Player> getPlayersInArena();
    
    public Set<Player> getPlayersInLobby();
    
    public Set<Player> getReadyPlayersInLobby();
    
    public Set<Player> getSpectators();

    public MASpawnThread getSpawnThread();
    
    public WaveManager getWaveManager();
    
    public Location getPlayerEntry(Player p);
    
    public ArenaListener getEventListener();
    
    public void setLeaderboard(Leaderboard leaderboard);
    
    public ArenaPlayer getArenaPlayer(Player p);
    
    public Set<Block> getBlocks();

    public void addBlock(Block b);
    
    public boolean removeBlock(Block b);
    
    public boolean hasPet(Entity e);
    
    public void addRepairable(Repairable r);
    
    public ArenaRegion getRegion();
    
    public InventoryManager getInventoryManager();
    
    public RewardManager getRewardManager();
    
    public MonsterManager getMonsterManager();

    public ClassLimitManager getClassLimitManager();

    public void revivePlayer(Player p);
    
    public ArenaLog getLog();
    
    
    
    

    
    public void scheduleTask(Runnable r, int delay);
    
    
    
    
    
    
    
    
    
    
    
    
    public boolean startArena();
    
    public boolean endArena();
    
    public void forceStart();
    
    public void forceEnd();
    
    public boolean playerJoin(Player p, Location loc);
    
    public void playerReady(Player p);
    
    public boolean playerLeave(Player p);
    
    public void playerDeath(Player p);

    public void playerRespawn(Player p);
    
    public Location getRespawnLocation(Player p);
    
    public void playerSpec(Player p, Location loc);
    
    public void storePlayerData(Player p, Location loc);
    
    public void storeContainerContents();
    
    public void restoreContainerContents();
    
    public void movePlayerToLobby(Player p);
    
    public void movePlayerToSpec(Player p);
    
    public void movePlayerToEntry(Player p);
    
    public void discardPlayer(Player p);
    
    public void repairBlocks();
    
    public void queueRepairable(Repairable r);
    
    
    
    /*////////////////////////////////////////////////////////////////////
    //
    //      Items & Cleanup
    //
    ////////////////////////////////////////////////////////////////////*/
    
    public void assignClass(Player p, String className);

    public void assignClassGiveInv(Player p, String className, ItemStack[] contents);

    public void addRandomPlayer(Player p);
    
    public void assignRandomClass(Player p);
    
    public void assignClassPermissions(Player p);
    
    public void removeClassPermissions(Player p);
    
    
    
    /*////////////////////////////////////////////////////////////////////
    //
    //      Initialization & Checks
    //
    ////////////////////////////////////////////////////////////////////*/
    
    public void restoreRegion();
    
    
    
    /*////////////////////////////////////////////////////////////////////
    //
    //      Getters & Misc
    //
    ////////////////////////////////////////////////////////////////////*/
    
    public boolean inArena(Player p);
    
    public boolean inLobby(Player p);
    
    public boolean inSpec(Player p);
    
    public boolean isDead(Player p);
    
    public String configName();
    
    public String arenaName();
    
    public MobArena getPlugin();
    
    public Wave getWave();
    
    public Map<String,ArenaClass> getClasses();
    
    public int getPlayerCount();
    
    public List<Player> getAllPlayers();
    
    public Collection<ArenaPlayer> getArenaPlayerSet();
    
    public List<Player> getNonreadyPlayers();
    
    public boolean canAfford(Player p);
    
    public boolean takeFee(Player p);

    public boolean refund(Player p);
    
    public boolean canJoin(Player p);
    
    public boolean canSpec(Player p);
}
