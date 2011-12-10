package com.garbagemule.MobArena;

import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachment;
//import org.bukkit.util.config.Configuration;

import com.garbagemule.MobArena.leaderboards.Leaderboard;
import com.garbagemule.MobArena.repairable.Repairable;
import com.garbagemule.MobArena.repairable.RepairableComparator;
import com.garbagemule.MobArena.util.Config;
import com.garbagemule.MobArena.waves.BossWave;
import com.garbagemule.MobArena.waves.Wave;

public abstract class Arena
{
    private MobArena plugin;
    
    // Setup fields
    protected String name;
    protected World world;
    protected boolean enabled, protect, running, setup, lobbySetup, autoEquip, forceRestore, softRestore, softRestoreDrops, emptyInvJoin, emptyInvSpec, pvp, monsterInfight, allowWarp;

    protected boolean edit, waveClear, detCreepers, detDamage, lightning, hellhounds, specOnDeath, shareInArena, spoutSelect;
    protected Location p1, p2, l1, l2, arenaLoc, lobbyLoc, spectatorLoc;
    protected Map<String,Location> spawnpoints, spawnpointsBoss, containers;
    protected String logging;

    // Wave/reward/entryfee fields
    protected int spawnTaskId, sheepTaskId, waveDelay, waveInterval, specialModulo, spawnMonstersInt, maxIdleTime, finalWave;
    protected MASpawnThread spawnThread;
    protected Map<Integer,List<ItemStack>> everyWaveMap, afterWaveMap;
    protected Map<Player,String> classMap;
    protected Map<String,List<ItemStack>>  classItems, classArmor;
    protected Map<String,Map<String,Boolean>> classPerms;
    protected Map<Player,PermissionAttachment> attachments;
    protected List<ItemStack> entryFee;

    // Player sets
    protected Set<Player> arenaPlayers, lobbyPlayers, readyPlayers, specPlayers;
    
    // Wave stuff
    protected TreeSet<Wave> singleWaves, singleWavesInstance;
    protected TreeSet<Wave> recurrentWaves;
    protected BossWave bossWave;
    protected Wave currentWave;
    
    // Arena sets/maps
    protected Set<Player>            hasPaid, rewardedPlayers, notifyPlayers, randoms;
    protected Set<LivingEntity>      monsters, explodingSheep, plaguedPigs, madCows;
    protected Set<Block>             blocks;
    protected Set<Wolf>              pets;
    protected Map<Player,Integer>    petMap;
    protected LinkedList<Repairable> repairables, containables;
    
    // Spawn overriding
    protected int spawnMonsters;
    protected boolean allowMonsters, allowAnimals;
    
    // Other settings
    protected int minPlayers, maxPlayers, joinDistance;
    protected Set<String> classes = new HashSet<String>();
    protected Map<Player,Location> locations = new HashMap<Player,Location>();
    protected Map<Player,Integer> healthMap = new HashMap<Player,Integer>();
    protected Map<Player,Integer> hungerMap = new HashMap<Player,Integer>();
    protected Map<Player,GameMode> modeMap = new HashMap<Player,GameMode>();
    
    // Logging
    protected ArenaLog log;
    protected Map<Player,ArenaPlayer> arenaPlayerMap;
    protected Leaderboard leaderboard;
    
    protected MAListener eventListener;
    
    protected PriorityBlockingQueue<Repairable> repairQueue;
    
    public Arena() {
        
    }
    
    /**
     * Primary constructor. Requires a name and a world.
     */
    public Arena(String name, World world)
    {
        if (world == null)
            throw new NullPointerException("[MobArena] ERROR! World for arena '" + name + "' does not exist!");
        
        this.name = name;
        this.world = world;
        plugin = (MobArena) Bukkit.getServer().getPluginManager().getPlugin("MobArena");
        
        arenaPlayers    = new HashSet<Player>();
        lobbyPlayers    = new HashSet<Player>();
        notifyPlayers   = new HashSet<Player>();
        readyPlayers    = new HashSet<Player>();
        specPlayers     = new HashSet<Player>();
        rewardedPlayers = new HashSet<Player>();
        hasPaid         = new HashSet<Player>();
        monsters        = new HashSet<LivingEntity>();
        explodingSheep  = new HashSet<LivingEntity>();
        plaguedPigs     = new HashSet<LivingEntity>();
        madCows         = new HashSet<LivingEntity>();
        blocks          = new HashSet<Block>();
        pets            = new HashSet<Wolf>();
        petMap          = new HashMap<Player,Integer>();
        classMap        = new HashMap<Player,String>();
        randoms         = new HashSet<Player>();
        repairables     = new LinkedList<Repairable>();
        containables    = new LinkedList<Repairable>();
        attachments     = new HashMap<Player,PermissionAttachment>();
        arenaPlayerMap  = new HashMap<Player,ArenaPlayer>();
        
        running         = false;
        edit            = false;
        
        allowMonsters   = world.getAllowMonsters();
        allowAnimals    = world.getAllowAnimals();
        spawnMonsters   = MAUtils.getSpawnMonsters(world);
        
        eventListener   = new MAListener(this, plugin);
        repairQueue     = new PriorityBlockingQueue<Repairable>(100, new RepairableComparator());
    }
    
    public abstract boolean startArena();
    
    public abstract boolean endArena();
    
    public abstract void forceStart();
    
    public abstract void forceEnd();
    
    public abstract void playerJoin(Player p, Location loc);
    
    public abstract void playerReady(Player p);
    
    public abstract void playerLeave(Player p);
    
    public abstract void playerDeath(Player p);
    
    public abstract void playerSpec(Player p, Location loc);
    
    public abstract void playerKill(Player p);
    
    public abstract void restoreInvAndGiveRewardsDelayed(final Player p);
    
    public abstract void restoreInvAndGiveRewards(final Player p);
    
    public abstract void storePlayerData(Player p, Location loc);
    
    public abstract void storeContainerContents();
    
    public abstract void restoreContainerContents();
    
    public abstract void movePlayerToLobby(Player p);
    
    public abstract void movePlayerToSpec(Player p);
    
    public abstract void movePlayerToEntry(Player p);
    
    public abstract void repairBlocks();
    
    public abstract void queueRepairable(Repairable r);
    
    
    
    /*////////////////////////////////////////////////////////////////////
    //
    //      Items & Cleanup
    //
    ////////////////////////////////////////////////////////////////////*/
    
    public abstract void assignClass(Player p, String className);
    
    public abstract void assignRandomClass(Player p);
    
    public abstract void assignClassPermissions(Player p);
    
    public abstract void removeClassPermissions(Player p);
    
    
    
    /*////////////////////////////////////////////////////////////////////
    //
    //      Initialization & Checks
    //
    ////////////////////////////////////////////////////////////////////*/
    
    public abstract void load(Config config);
    
    public abstract void restoreRegion();
    
    public abstract void serializeConfig();
    
    public abstract void deserializeConfig();
    
    public abstract boolean serializeRegion();
    
    public abstract boolean deserializeRegion();
    
    /**
     * Check if a location is inside of the cuboid region
     * that p1 and p2 span.
     */
    public abstract boolean inRegion(Location loc);
    
    /**
     * Check if a location is inside of the arena region, expanded
     * by 'radius' blocks. Used with explosions.
     */
    public abstract boolean inRegionRadius(Location loc, int radius);
    
    
    
    /*////////////////////////////////////////////////////////////////////
    //
    //      Getters & Misc
    //
    ////////////////////////////////////////////////////////////////////*/
    
    public abstract boolean inArena(Player p);
    
    public abstract boolean inLobby(Player p);
    
    public abstract boolean isRunning();
    
    public abstract boolean isPvpEnabled();
    
    public abstract boolean isLightningEnabled();
    
    public abstract boolean isBossWave();
    
    public abstract String configName();
    
    public abstract String arenaName();
    
    public abstract MobArena getPlugin();
    
    public abstract World getWorld();
    
    public abstract Wave getWave();
    
    public abstract void setWave(Wave wave);

    public abstract void setBossWave(BossWave bossWave);
    
    public abstract Set<String> getClasses();
    
    public abstract List<Location> getAllSpawnpoints();
    
    public abstract List<Location> getSpawnpoints();
    
    public abstract Location getBossSpawnpoint();
    
    public abstract int getPlayerCount();
    
    public abstract void addBlock(Block b);
    
    public abstract void addMonster(LivingEntity e);
    
    public abstract void addExplodingSheep(LivingEntity e);
    
    public abstract List<Player> getAllPlayers();
    
    public abstract List<Player> getLivingPlayers();
    
    public abstract Set<Player> getArenaPlayers();
    
    public abstract Collection<ArenaPlayer> getArenaPlayerSet();
    
    public abstract List<ArenaPlayerStatistics> getArenaPlayerStatistics(Comparator<ArenaPlayerStatistics> comparator);
    
    public abstract List<Player> getNonreadyPlayers();
    
    public abstract Set<LivingEntity> getMonsters();
    
    public abstract Set<Wolf> getPets();
    
    public abstract void resetIdleTimer();
    
    public abstract void addTrunkAndLeaves(Block b);
    
    public abstract boolean canAfford(Player p);
    
    public abstract boolean takeFee(Player p);
    
    public abstract boolean canJoin(Player p);
    
    public abstract boolean canSpec(Player p);
}
