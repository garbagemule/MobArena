package com.garbagemule.MobArena.framework;

import com.garbagemule.MobArena.ArenaClass;
import com.garbagemule.MobArena.ArenaListener;
import com.garbagemule.MobArena.ArenaPlayer;
import com.garbagemule.MobArena.ClassLimitManager;
import com.garbagemule.MobArena.MASpawnThread;
import com.garbagemule.MobArena.Messenger;
import com.garbagemule.MobArena.MobArena;
import com.garbagemule.MobArena.MonsterManager;
import com.garbagemule.MobArena.Msg;
import com.garbagemule.MobArena.RewardManager;
import com.garbagemule.MobArena.ScoreboardManager;
import com.garbagemule.MobArena.leaderboards.Leaderboard;
import com.garbagemule.MobArena.region.ArenaRegion;
import com.garbagemule.MobArena.repairable.Repairable;
import com.garbagemule.MobArena.things.Thing;
import com.garbagemule.MobArena.things.ThingPicker;
import com.garbagemule.MobArena.util.inventory.InventoryManager;
import com.garbagemule.MobArena.util.timer.AutoStartTimer;
import com.garbagemule.MobArena.waves.WaveManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Arena
{
    /*/////////////////////////////////////////////////////////////////////////
    //
    //      NEW METHODS IN REFACTORING
    //
    /////////////////////////////////////////////////////////////////////////*/

    ConfigurationSection getSettings();

    World getWorld();

    void setWorld(World world);

    boolean isEnabled();

    void setEnabled(boolean value);

    boolean isProtected();

    void setProtected(boolean value);

    boolean isRunning();

    boolean inEditMode();

    void setEditMode(boolean value);

    int getMinPlayers();

    int getMaxPlayers();

    List<Thing> getEntryFee();

    Set<Map.Entry<Integer, ThingPicker>> getEveryWaveEntrySet();

    ThingPicker getAfterWaveReward(int wave);

    Set<Player> getPlayersInArena();

    Set<Player> getPlayersInLobby();

    Set<Player> getReadyPlayersInLobby();

    Set<Player> getSpectators();

    MASpawnThread getSpawnThread();

    WaveManager getWaveManager();

    ArenaListener getEventListener();

    void setLeaderboard(Leaderboard leaderboard);

    ArenaPlayer getArenaPlayer(Player p);

    Set<Block> getBlocks();

    void addBlock(Block b);

    boolean removeBlock(Block b);

    boolean hasPet(Entity e);

    void addRepairable(Repairable r);

    ArenaRegion getRegion();

    InventoryManager getInventoryManager();

    RewardManager getRewardManager();

    MonsterManager getMonsterManager();

    ClassLimitManager getClassLimitManager();

    void revivePlayer(Player p);

    ScoreboardManager getScoreboard();


    Messenger getMessenger();

    Messenger getGlobalMessenger();

    void announce(String msg);

    void announce(Msg msg, String s);

    void announce(Msg msg);




    void scheduleTask(Runnable r, int delay);












    boolean startArena();

    boolean endArena();

    void forceStart();

    void forceEnd();

    boolean hasPermission(Player p);

    boolean playerJoin(Player p, Location loc);

    void playerReady(Player p);

    boolean playerLeave(Player p);

    boolean isMoving(Player p);

    boolean isLeaving(Player p);

    void playerDeath(Player p);

    void playerRespawn(Player p);

    Location getRespawnLocation(Player p);

    void playerSpec(Player p, Location loc);

    void storeContainerContents();

    void restoreContainerContents();

    void discardPlayer(Player p);

    void repairBlocks();

    void queueRepairable(Repairable r);



    /*////////////////////////////////////////////////////////////////////
    //
    //      Items & Cleanup
    //
    ////////////////////////////////////////////////////////////////////*/

    void assignClass(Player p, String className);

    void assignClassGiveInv(Player p, String className, ItemStack[] contents);

    void addRandomPlayer(Player p);

    void assignRandomClass(Player p);



    /*////////////////////////////////////////////////////////////////////
    //
    //      Initialization & Checks
    //
    ////////////////////////////////////////////////////////////////////*/

    void restoreRegion();



    /*////////////////////////////////////////////////////////////////////
    //
    //      Getters & Misc
    //
    ////////////////////////////////////////////////////////////////////*/

    boolean inArena(Player p);

    boolean inLobby(Player p);

    boolean inSpec(Player p);

    boolean isDead(Player p);

    String configName();

    /**
     * @deprecated use {@link #configName()} instead
     */
    @Deprecated
    String arenaName();

    String getSlug();

    MobArena getPlugin();

    Map<String,ArenaClass> getClasses();

    int getPlayerCount();

    List<Player> getAllPlayers();

    Collection<ArenaPlayer> getArenaPlayerSet();

    List<Player> getNonreadyPlayers();

    boolean canAfford(Player p);

    boolean takeFee(Player p);

    boolean refund(Player p);

    boolean canJoin(Player p);

    boolean canSpec(Player p);

    boolean hasIsolatedChat();

    Player getLastPlayerStanding();

    AutoStartTimer getAutoStartTimer();
}
