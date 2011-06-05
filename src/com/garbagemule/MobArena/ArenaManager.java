package com.garbagemule.MobArena;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import org.bukkit.World;
import org.bukkit.Server;
import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.config.Configuration;
import org.bukkit.event.Event;
import org.bukkit.plugin.EventExecutor;

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
    
    // Set and Maps for storing players, their locations, items, armor, etc.
    protected static Set<Player> playerSet            = new HashSet<Player>();
    protected static Set<Player> readySet             = new HashSet<Player>();
    protected static Map<Player,String> rewardMap     = new HashMap<Player,String>();
    protected static Map<Player,Location> locationMap = new HashMap<Player,Location>();
    
    // Maps for storing class items and armor.
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
    protected static Set<Item> dropSet            = new HashSet<Item>();
    
    
    
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
            config = MAUtils.getConfig();
            plugin = instance;
            server = plugin.getServer();
            world  = MAUtils.getWorld();
        
            // Class list and maps.
            classes       = MAUtils.getClasses();
            classItemMap  = MAUtils.getClassItems("items");
            classArmorMap = MAUtils.getClassItems("armor");
            
            // Waves and rewards.
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
        
        for (Player p : playerSet)
        {
            p.teleport(arenaLoc);
            rewardMap.put(p,"");
        }
        
        MASpawnThread thread = new MASpawnThread();
        server.getScheduler().scheduleSyncRepeatingTask(plugin,thread,100,400);
        
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
        server.getScheduler().cancelTasks(plugin);
        killMonsters();
        clearBlocks();
        clearDrops();
        giveRewards();
        
        // TO-DO: Fix this, maybe add a Set<Player> dead
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
            tellPlayer(p, "Arena in progress. Type /marena spectate to watch.");
            return;
        }
        if (!MAUtils.hasEmptyInventory(p))
        {
            tellPlayer(p, "You must empty your inventory to join the arena.");
            return;
        }
        
        playerSet.add(p);
        
        if (!locationMap.keySet().contains(p))
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
        if (!locationMap.keySet().contains(p))
        {
            tellPlayer(p, "You are not in the arena.");
            return;
        }
        
        if (playerSet.contains(p))
        {
            playerSet.remove(p);
            MAUtils.clearInventory(p);
        }
        
        //if (readySet.contains(p))
            readySet.remove(p);
            
        //if (classMap.keySet().contains(p))
            classMap.remove(p);
        
        // This must occur after playerSet.remove(p) to avoid teleport block.
        p.teleport(locationMap.remove(p));
        
        if (isRunning && playerSet.isEmpty())
            endArena();
        
        tellPlayer(p, "You left the arena. Thanks for playing!");
    }

    /**
     * Adds a joined arena player to the set of ready players.
     */
    public static void playerReady(Player p)
    {
        readySet.add(p);

        if (readySet.equals(playerSet))
        {
            readySet.clear();
            startArena();
        }
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
        p.setHealth(20);
        tellAll(p.getName() + " died!");
        
        //if (playerSet.contains(p))
            playerSet.remove(p);
        
        //if (classMap.keySet().contains(p))
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

        String list = "";
        for (Player player : playerSet)
            list += player.getName() + ", ";
        list = list.substring(0,list.length()-2);
        
        tellPlayer(p, "Survivors: " + list);
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
        
        String list = "";
        for (Player player : notReadySet)
            list += player.getName() + ", ";
        list = list.substring(0,list.length()-2);
        
        tellPlayer(p, "Not ready: " + list);
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
        {
            b.setType(Material.AIR);
        }
        blockSet.clear();
    }
    
    /**
     * Removes all items on the arena floor.
     */
    public static void clearDrops()
    {
        // Remove all blocks, then clear the Set.
        for (Item i : dropSet)
        {
            i.remove();
        }
        
        dropSet.clear();
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
        for (Player p : rewardMap.keySet())
        {
            String r = rewardMap.get(p);
            if (r.equals("")) continue;
            
            tellPlayer(p, "Here are all of your rewards!");
            MAUtils.giveItems(true, p, r);
        }
        
        rewardMap.clear();
    }
    
    
    
    /* ///////////////////////////////////////////////////////////////////// //
    
            MISC METHODS
    
    // ///////////////////////////////////////////////////////////////////// */
    
    /**
     * Sends a message to a player.
     */
    public static void tellPlayer(Player p, String msg)
    {
        p.sendMessage(ChatColor.GREEN + "[MobArena] " + ChatColor.WHITE + msg);
	}
    
    /**
     * Sends a message to all players in the arena.
     */
    public static void tellAll(String msg)
    {
        for (Player p : playerSet)
        {
            tellPlayer(p, msg);
        }
	}
}