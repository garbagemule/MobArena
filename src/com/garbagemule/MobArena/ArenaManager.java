package com.garbagemule.MobArena;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.util.config.Configuration;

public class ArenaManager
{
    // Convenience variables.
    protected static MobArena plugin       = null;
    protected static Server   server       = null;
    protected static World    world        = null;
    protected static Location arenaLoc     = null;
    protected static Location lobbyLoc     = null;
    protected static Location spectatorLoc = null;
    protected static boolean isRunning     = false;
    protected static boolean isSetup       = false;
    protected static boolean isEnabled     = true;
    protected static boolean isProtected   = true;
    protected static int spawnTaskId       = -1;
    protected static int waveDelay, waveInterval, specialModulo, repairDelay;
    protected static boolean checkUpdates, lightning, spawnMonsters;
    
    // Location variables for the arena region.
    protected static Location p1 = null;
    protected static Location p2 = null;
    
    // Configuration
    protected static Configuration config = null;
    
    // Spawn locations list and monster distribution fields.
    protected static List<Location> spawnpoints = new ArrayList<Location>();
    protected static int dZombies, dSkeletons, dSpiders, dCreepers, dWolves;
    protected static int dPoweredCreepers, dPigZombies, dSlimes, dMonsters,
                         dAngryWolves, dGiants, dGhasts;
    
    // Sets and Maps for storing players, their locations, and their rewards.
    protected static Set<Player> playerSet            = new HashSet<Player>();
    protected static Set<Player> readySet             = new HashSet<Player>();
    protected static Map<Player,String> rewardMap     = new HashMap<Player,String>();
    protected static Map<Player,Location> locationMap = new HashMap<Player,Location>();
    
    // Maps for storing player classes, class items and armor.
    protected static List<String> classes              = new ArrayList<String>();
    protected static Map<Player,String> classMap       = new HashMap<Player,String>();
    protected static Map<String,String> classItemMap   = new HashMap<String,String>();
    protected static Map<String,String> classArmorMap  = new HashMap<String,String>();
    
    // Maps for rewards.
    protected static Map<Integer,String> everyWaveMap = new HashMap<Integer,String>();
    protected static Map<Integer,String> afterWaveMap = new HashMap<Integer,String>();
    
    // Entities, blocks and items on MobArena floor.
    protected static Set<LivingEntity> monsterSet = new HashSet<LivingEntity>();
    protected static Set<Block> blockSet          = new HashSet<Block>();
    
    
    
    /* ///////////////////////////////////////////////////////////////////// //
    
            INITIALIZATION AND UPDATE METHODS
    
    // ///////////////////////////////////////////////////////////////////// */
    
    /**
     * Initializes the ArenaManager.
     */
    public static void init(MobArena instance)
    {
        // If instance == null, simply update location variables.
        if (instance != null)
        {
            // General variables.
            config        = MAUtils.getConfig();
            plugin        = instance;
            server        = plugin.getServer();
            world         = MAUtils.getWorld();
            lightning     = MAUtils.getBoolean("settings.lightning", true);
            repairDelay   = MAUtils.getInt("settings.repairdelay", 5);
            spawnMonsters = MAUtils.spawnBypass(false);
        
            // Class list and maps.
            classes       = MAUtils.getClasses();
            classItemMap  = MAUtils.getClassItems("items");
            classArmorMap = MAUtils.getClassItems("armor");
            
            // Waves and rewards.
            waveDelay     = MAUtils.getInt("settings.firstwavedelay", 5) * 20;
            waveInterval  = MAUtils.getInt("settings.waveinterval",  20) * 20;
            specialModulo = MAUtils.getInt("settings.specialmodulo",  4);
            everyWaveMap  = MAUtils.getWaveMap("every");
            afterWaveMap  = MAUtils.getWaveMap("after");
            
            // Monster distribution coefficients.
            dZombies      = MAUtils.getDistribution("zombies");
            dSkeletons    = MAUtils.getDistribution("skeletons");
            dSpiders      = MAUtils.getDistribution("spiders");
            dCreepers     = MAUtils.getDistribution("creepers");
            dWolves       = MAUtils.getDistribution("wolves");
            
            dPoweredCreepers = MAUtils.getDistribution("poweredcreepers", "special");
            dPigZombies      = MAUtils.getDistribution("zombiepigmen",    "special");
            dSlimes          = MAUtils.getDistribution("slimes",          "special");
            dMonsters        = MAUtils.getDistribution("humans",          "special");
            dAngryWolves     = MAUtils.getDistribution("angrywolves",     "special");
            dGiants          = MAUtils.getDistribution("giants",          "special");
            dGhasts          = MAUtils.getDistribution("ghasts",          "special");
        }
        
        // Convenience variables.
        arenaLoc      = MAUtils.getCoords("arena");
        lobbyLoc      = MAUtils.getCoords("lobby");
        spectatorLoc  = MAUtils.getCoords("spectator");
        p1            = MAUtils.getCoords("p1");
        p2            = MAUtils.getCoords("p2");
        spawnpoints   = MAUtils.getSpawnPoints();
        checkUpdates  = MAUtils.getBoolean("settings.updatenotification", true);
        
        // Set the boolean if all variables are valid.
        ArenaManager.isSetup = MAUtils.verifyData();
    }
    
    /**
     * Updates all relevant variables.
     */
    public static void updateVariables()
    {
        init(null);
    }
    
    
    
    /* ///////////////////////////////////////////////////////////////////// //
    
            ARENA METHODS
    
    // ///////////////////////////////////////////////////////////////////// */
    
    /**
     * Starts the current MobArena session.
     */
    public static void startArena()
    {
        isRunning = true;
        readySet.clear();
        MAUtils.spawnBypass(true);
        
        // Clear the floor for good measure.
        clearEntities();
        
        for (Player p : playerSet)
        {
            p.teleport(arenaLoc);
            rewardMap.put(p,"");
        }
        
        MASpawnThread thread = new MASpawnThread();
        spawnTaskId = server.getScheduler().scheduleSyncRepeatingTask(plugin,thread,(long)waveDelay,(long)waveInterval);
        
        tellAll("Let the slaughter begin!");
    }
    
    /**
     * Ends the current MobArena session.
     * Clears the arena floor, gives all the players their rewards,
     * and stops the spawning of monsters.
     */
    public static void endArena()
    {
        isRunning = false;
        server.getScheduler().cancelTask(spawnTaskId);
        MAUtils.spawnBypass(true);
        
        killMonsters();
        clearBlocks();
        clearEntities();
        giveRewards();
        
        tellAll("Arena finished.");
    }
    
    /**
     * Attempts to let a player join the arena session.
     * Players must have an empty inventory to join the arena. Their
     * location will be stored for when they leave.
     */
    public static void playerJoin(Player p)
    {
        if (!isEnabled)
        {
            tellPlayer(p, "MobArena is not enabled.");
            return;
        }
        if (!isSetup)
        {
            tellPlayer(p, "MobArena has not been set up yet!");
            return;
        }
        if (playerSet.contains(p))
        {
            tellPlayer(p, "You are already playing!");
            return;
        }
        if (isRunning)
        {
            tellPlayer(p, "Arena in progress. Type /ma spec to watch.");
            return;
        }
        if (!MAUtils.hasEmptyInventory(p))
        {
            tellPlayer(p, "You must empty your inventory to join the arena.");
            return;
        }
        
        playerSet.add(p);
        
        if (!locationMap.containsKey(p))
        {
            locationMap.put(p, p.getLocation());
        }
        
        p.teleport(lobbyLoc);
        
        tellPlayer(p, "You joined the arena. Have fun!");
    }
    
    /**
     * Attempts to remove a player from the arena session.
     * The player is teleported back to his previous location, and
     * is removed from all the sets and maps.
     */
    public static void playerLeave(Player p)
    {
        if (!locationMap.containsKey(p))
        {
            tellPlayer(p, "You are not in the arena.");
            return;
        }
        
        // This must occur before playerSet.remove(p) to avoid teleport block.
        p.teleport(locationMap.get(p));
        locationMap.remove(p);
        readySet.remove(p);
        classMap.remove(p);
        
        if (playerSet.remove(p))
            MAUtils.clearInventory(p);
        
        if (isRunning && playerSet.isEmpty())
            endArena();

        if (!readySet.isEmpty() && readySet.equals(playerSet))
            startArena();
        
        tellPlayer(p, "You left the arena. Thanks for playing!");
    }

    /**
     * Adds a joined arena player to the set of ready players.
     */
    public static void playerReady(Player p)
    {
        readySet.add(p);

        if (readySet.equals(playerSet))
            startArena();
    }
    
    /**
     * Removes a dead player from the arena session.
     * The player is teleported safely back to the spectator area,
     * and their health is restored. All sets and maps are updated.
     * If this was the last player alive, the arena session ends.
     */
    public static void playerDeath(Player p)
    {
        p.teleport(spectatorLoc);
        MAUtils.clearInventory(p);
        p.setFireTicks(0);
        p.setHealth(20);
        tellAll(p.getName() + " died!");
        
        playerSet.remove(p);
        classMap.remove(p);
            
        if (isRunning && playerSet.isEmpty())
            endArena();
    }
    
    /**
     * Lets a player spectate the current arena session.
     */
    public static void playerSpectate(Player p)
    {
        if (!playerSet.contains(p))
        {
            p.teleport(spectatorLoc);
            tellPlayer(p, "Enjoy the show!");
        }
        else
        {
            tellPlayer(p, "Can't spectate when in the arena!");
        }
    }
    
    /**
     * Prints the list of players currently in the arena session.
     */
    public static void playerList(Player p)
    {
        if (playerSet.isEmpty())
        {
            tellPlayer(p, "There is no one in the arena right now.");
            return;
        }

        StringBuffer list = new StringBuffer();
        final String SEPARATOR = ", ";
        for (Player player : playerSet)
        {
            list.append(player.getName());
            list.append(SEPARATOR);
        }
        
        tellPlayer(p, "Survivors: " + list.substring(0, list.length() - 2));
    }
    
    /**
     * Prints the list of players who aren't ready.
     */
    public static void notReadyList(Player p)
    {
        if (!playerSet.contains(p) || isRunning)
        {
            tellPlayer(p, "You aren't in the lobby!");
            return;
        }
        
        Set<Player> notReadySet = new HashSet<Player>(playerSet);
        notReadySet.removeAll(readySet);
        
        StringBuffer list = new StringBuffer();
        final String SEPARATOR = ", ";
        for (Player player : notReadySet)
        {
            list.append(player.getName());
            list.append(SEPARATOR);
        }
        
        tellPlayer(p, "Not ready: " + list.substring(0, list.length() - 2));
    }
    
    /**
     * Forcefully starts the arena, causing all players in the
     * playerSet who aren't ready to leave, and starting the
     * arena for everyone else.
     */
    public static void forceStart(Player p)
    {
        if (ArenaManager.isRunning)
        {
            ArenaManager.tellPlayer(p, "Arena has already started.");
            return;
        }
        if (ArenaManager.readySet.isEmpty())
        {
            ArenaManager.tellPlayer(p, "Can't force start, no players are ready.");
            return;
        }
        
        Set<Player> set = new HashSet<Player>(playerSet);
        for (Player player : set)
        {
            if (!ArenaManager.readySet.contains(player))
                ArenaManager.playerLeave(player);
        }
        
        ArenaManager.tellPlayer(p, "Forced arena start.");
    }
    
    /**
     * Forcefully ends the arena, causing all players to leave and
     * all relevant sets and maps to be cleared.
     */
    public static void forceEnd(Player p)
    {
        if (ArenaManager.playerSet.isEmpty())
        {
            ArenaManager.tellPlayer(p, "No one is in the arena.");
            return;
        }
        
        Set<Player> set = new HashSet<Player>(playerSet);
        for (Player player : set)
            ArenaManager.playerLeave(player);
        
        // Just for good measure.
        endArena();
        
        ArenaManager.tellPlayer(p, "Forced arena end.");
    }
    
    
    
    /* ///////////////////////////////////////////////////////////////////// //
    
            CLEANUP METHODS
    
    // ///////////////////////////////////////////////////////////////////// */
    
    /**
     * Kills all monsters currently on the arena floor.
     */
    public static void killMonsters()
    {        
        // Remove all monsters, then clear the Set.
        for (LivingEntity e : monsterSet)
        {
            if (!e.isDead())
                e.remove();
        }
        monsterSet.clear();
    }
    
    /**
     * Removes all the blocks on the arena floor.
     */
    public static void clearBlocks()
    {
        // Remove all blocks, then clear the Set.
        for (Block b : blockSet)
            b.setType(Material.AIR);
        
        blockSet.clear();
    }
    
    /**
     * Removes all items and slimes in the arena region.
     */
    public static void clearEntities()
    {
        Chunk c1 = world.getChunkAt(p1);
        Chunk c2 = world.getChunkAt(p2);
        
        /* Yes, ugly nesting, but it's necessary. This bit
         * removes all the entities in the arena region without
         * bloatfully iterating through all entities in the
         * world. Much faster on large servers especially. */ 
        for (int i = c1.getX(); i <= c2.getX(); i++)
            for (int j = c1.getZ(); j <= c2.getZ(); j++)
                for (Entity e : world.getChunkAt(i,j).getEntities())
                    if ((e instanceof Item) || (e instanceof Slime))
                        e.remove();
    }
    
    
    
    /* ///////////////////////////////////////////////////////////////////// //
    
            CLASS AND REWARD METHODS
    
    // ///////////////////////////////////////////////////////////////////// */
    
    /**
     * Assigns a class to the player.
     */
    public static void assignClass(Player p, String className)
    {
        classMap.put(p, className);
        giveClassItems(p);
    }
    
    /**
     * Grant a player their class-specific items.
     */
    public static void giveClassItems(Player p)
    {        
        String className  = classMap.get(p);
        String classItems = classItemMap.get(className);
        String classArmor = classArmorMap.get(className);
        
        MAUtils.giveItems(p, classItems, classArmor);
    }
    
    /**
     * Gives all the players the rewards they earned.
     */
    public static void giveRewards()
    {
        /* This has to be delayed for players to actually receive
         * their rewards after they die. Not sure why. */
        server.getScheduler().scheduleSyncDelayedTask(plugin,
            new Runnable()
            {
                public void run()
                {
                    for (Player p : rewardMap.keySet())
                    {
                        String r = rewardMap.get(p);
                        if (r.isEmpty()) continue;
                        
                        tellPlayer(p, "Here are all of your rewards!");
                        MAUtils.giveItems(true, p, r);
                    }
                    
                    rewardMap.clear();
                }}, 20);
    }
    
    
    
    /* ///////////////////////////////////////////////////////////////////// //
    
            MISC METHODS
    
    // ///////////////////////////////////////////////////////////////////// */
    
    /**
     * Sends a message to a player.
     */
    public static void tellPlayer(Player p, String msg)
    {
        if (p == null)
            return;
        
        p.sendMessage(ChatColor.GREEN + "[MobArena] " + ChatColor.WHITE + msg);
	}
    
    /**
     * Sends a message to all players in the arena.
     */
    public static void tellAll(String msg)
    {
        Chunk c1 = world.getChunkAt(p1);
        Chunk c2 = world.getChunkAt(p2);

        for (int i = c1.getX(); i <= c2.getX(); i++)
            for (int j = c1.getZ(); j <= c2.getZ(); j++)
                for (Entity p : world.getChunkAt(i,j).getEntities())
                    if (p instanceof Player)
                        tellPlayer((Player)p, msg);
            
	}
}