package com.garbagemule.MobArena;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.config.Configuration;

import com.garbagemule.MobArena.MAMessages.Msg;

public class Arena
{
    private MobArena plugin;
    
    // Setup fields
    protected String name;
    protected World world;
    protected boolean enabled, protect, logging, running, setup, lobbySetup, autoEquip, forceRestore, softRestore, softRestoreDrops, emptyInvJoin, emptyInvSpec, pvp, monsterInfight, allowWarp;
    protected boolean edit, waveClear, detCreepers, detDamage, lightning, hellhounds, specOnDeath, shareInArena;
    protected Location p1, p2, l1, l2, arenaLoc, lobbyLoc, spectatorLoc;
    protected Map<String,Location> spawnpoints;

    // Wave/reward/entryfee fields
    protected int spawnTaskId, waveDelay, waveInterval, specialModulo, spawnMonstersInt, maxIdleTime;
    protected MASpawnThread spawnThread;
    protected Map<Integer,List<ItemStack>> everyWaveMap, afterWaveMap;
    protected Map<String,Integer> distDefault, distSpecial;
    protected Map<Player,String> classMap;
    protected Map<String,List<ItemStack>>  classItems, classArmor;
    protected Map<Integer,Map<Player,List<ItemStack>>> classBonuses;
    protected Map<Player,List<ItemStack>> rewardMap;
    protected List<ItemStack> entryFee;
    
    // Arena sets/maps
    protected Set<Player>         arenaPlayers, lobbyPlayers, readyPlayers, specPlayers, hasPaid, rewardedPlayers, notifyPlayers, randoms;
    protected Set<LivingEntity>   monsters;
    protected Set<Block>          blocks;
    protected Set<Wolf>           pets;
    protected Map<Player,Integer> petMap;
    protected List<int[]>         repairList;
    
    // Spawn overriding
    protected int spawnMonsters;
    protected boolean allowMonsters, allowAnimals;
    
    // Other settings
    protected int repairDelay, playerLimit, joinDistance;
    protected List<String> classes = new LinkedList<String>();
    protected Map<Player,Location> locations = new HashMap<Player,Location>();
    
    // Logging
    protected ArenaLog log;
    protected List<String> classDistribution = new LinkedList<String>();
    protected Map<Player,Integer> waveMap = new HashMap<Player,Integer>();
    protected Map<Player,Integer> killMap = new HashMap<Player,Integer>();
    protected Timestamp startTime;
    protected Timestamp endTime;
    
    protected MAListener eventListener;
    
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
        log = new ArenaLog(plugin, this);
        
        arenaPlayers    = new HashSet<Player>();
        lobbyPlayers    = new HashSet<Player>();
        notifyPlayers   = new HashSet<Player>();
        readyPlayers    = new HashSet<Player>();
        specPlayers     = new HashSet<Player>();
        rewardedPlayers = new HashSet<Player>();
        hasPaid         = new HashSet<Player>();
        monsters        = new HashSet<LivingEntity>();
        blocks          = new HashSet<Block>();
        pets            = new HashSet<Wolf>();
        petMap          = new HashMap<Player,Integer>();
        classMap        = new HashMap<Player,String>();
        randoms         = new HashSet<Player>();
        rewardMap       = new HashMap<Player,List<ItemStack>>();
        repairList      = new LinkedList<int[]>();
        
        running         = false;
        edit            = false;
        
        allowMonsters   = world.getAllowMonsters();
        allowAnimals    = world.getAllowAnimals();
        spawnMonsters   = ((net.minecraft.server.World) ((CraftWorld) world).getHandle()).spawnMonsters;
        
        eventListener   = new MAListener(this, plugin);
    }
    
    public boolean startArena()
    {
        // Sanity-checks
        if (running || lobbyPlayers.isEmpty() || !readyPlayers.containsAll(lobbyPlayers))
            return false;
        if (!softRestore && forceRestore && !serializeRegion())
            return false;
        
        // Populate arenaPlayers and clear the lobby.
        arenaPlayers.addAll(lobbyPlayers);
        lobbyPlayers.clear();
        readyPlayers.clear();
        
        // Assign random classes.
        for (Player p : randoms)
            assignRandomClass(p);
        if (arenaPlayers.isEmpty())
            return false;
        
        // Teleport players, give full health, initialize maps
        for (Player p : arenaPlayers)
        {
            p.teleport(arenaLoc);
            p.setHealth(20);
            rewardMap.put(p, new LinkedList<ItemStack>());
            waveMap.put(p, 0);
            killMap.put(p, 0);
        }
        
        // Spawn pets.
        spawnPets();
        
        // Start spawning monsters.
        startSpawning();
        
        // Start logging
        if (logging)
            log.start();
        
        // Set the boolean.
        running = true;
        
        // Announce and notify.
        MAUtils.tellAll(this, MAMessages.get(Msg.ARENA_START));
        for (MobArenaListener listener : plugin.getAM().listeners)
            listener.onArenaStart();
        
        return true;
    }
    
    public boolean endArena()
    {
        // Sanity-checks.
        if (!running || !arenaPlayers.isEmpty())
            return false;
        
        // Set the boolean.
        running = false;
        
        // Stop logging.
        if (logging)
        {
            log.end();
            log.serialize();
            log.clear();
        }
        
        // Stop spawning.
        stopSpawning();

        // Clean arena floor.
        cleanup();
        
        // Restore region.
        if (softRestore)
            for (int[] buffer : repairList)
                world.getBlockAt(buffer[0], buffer[1], buffer[2]).setTypeIdAndData(buffer[3], (byte) buffer[4], false);
        else if (forceRestore)
            deserializeRegion();

        // Announce and clear sets.
        MAUtils.tellAll(this, MAMessages.get(Msg.ARENA_END), true);
        arenaPlayers.clear();
        notifyPlayers.clear();
        rewardedPlayers.clear();
        classMap.clear();
        rewardMap.clear();
        waveMap.clear();
        killMap.clear();
        spawnThread = null;
        
        // Notify listeners.
        for (MobArenaListener listener : plugin.getAM().listeners)
            listener.onArenaEnd();
        
        return true;
    }
    
    public void forceStart()
    {
        // Set operations.
        Set<Player> tmp = new HashSet<Player>();
        //tmp.addAll(livePlayers);
        tmp.removeAll(readyPlayers);
        
        // Force leave.
        for (Player p : tmp)
        {
            plugin.getAM().arenaMap.remove(p);
            playerLeave(p);
        }
    }
    
    public void forceEnd()
    {
        for (Player p : getAllPlayers())
        {
            plugin.getAM().arenaMap.remove(p);
            playerLeave(p);
        }
        
        for (Entity e : monsters)
            e.remove();
        
        arenaPlayers.clear();
        lobbyPlayers.clear();
        readyPlayers.clear();
        
        rewardMap.clear();
        waveMap.clear();
        killMap.clear();
        monsters.clear();
        
        spawnTaskId = -1;
    }
    
    public void playerJoin(Player p, Location loc)
    {
        if (!locations.containsKey(p))
            locations.put(p,loc);
        
        // Update chunk.
        updateChunk(lobbyLoc);
        
        MAUtils.sitPets(p);
        lobbyPlayers.add(p);
        p.teleport(lobbyLoc);

        // Notify listeners.
        for (MobArenaListener listener : plugin.getAM().listeners)
            listener.onPlayerJoin(p);
    }
    
    public void playerReady(Player p)
    {
        readyPlayers.add(p);
        startArena();
    }
    
    public void playerLeave(Player p)
    {
        // Clear class inventory, restore old inventory and fork over rewards.
        restoreInvAndGiveRewards(p, (arenaPlayers.contains(p) || lobbyPlayers.contains(p)));
        
        // Grab the player's entry location, and warp them there.
        Location entry = locations.get(p);
        if (entry != null)
        {
            updateChunk(entry);
            p.teleport(entry);
        }
        locations.remove(p);
        
        // Remove from the arenaMap and all the sets.
        plugin.getAM().arenaMap.remove(p);
        removePlayer(p);
        
        // End the arena if conditions are met.
        endArena();
    }
    
    public void playerDeath(Player p)
    {
        // If spectate-on-death: false, pass on to playerLeave.
        if (!specOnDeath)
        {
            p.teleport(arenaLoc);
            playerLeave(p);
            return;
        }

        // Clear class inventory, restore old inventory and fork over rewards.
        restoreInvAndGiveRewards(p, true);

        // Remove player from sets, warp to spectator area, then add to specPlayers.
        removePlayer(p);   
        p.teleport(arenaLoc); // This will sometimes force players to drop any items held (not confirmed)  
        p.teleport(spectatorLoc);
        specPlayers.add(p);

        // Update the monster targets.
        if (running && spawnThread != null)
            spawnThread.updateTargets();
        
        // Announce and notify
        MAUtils.tellAll(this, MAMessages.get(Msg.PLAYER_DIED, p.getName()));
        for (MobArenaListener listener : plugin.getAM().listeners)
            listener.onPlayerDeath(p);

        // End the arena if conditions are met.
        endArena();
    }
    
    public void playerSpec(Player p, Location loc)
    {
        if (!locations.containsKey(p))
            locations.put(p,loc);
        
        MAUtils.sitPets(p);
        specPlayers.add(p);
        p.teleport(spectatorLoc);
    }
    
    public void removePlayer(Player p)
    {
        // Heal and put out fire.
        p.setFireTicks(0);
        p.setHealth(20);
        
        // Remove pets.
        removePets(p);
        
        // readyPlayers before lobbyPlayers because of startArena sanity-checks
        readyPlayers.remove(p);
        specPlayers.remove(p);
        arenaPlayers.remove(p);
        lobbyPlayers.remove(p);
    }

    private void spawnPets()
    {
        for (Map.Entry<Player,Integer> entry : petMap.entrySet())
        {
            // Remove the bones from the inventory.
            Player p = entry.getKey();
            p.getInventory().removeItem(new ItemStack(Material.BONE, entry.getValue()));
            
            // Spawn wolves, set owner and health, and add to pet set.
            for (int i = 0; i < entry.getValue(); i++)
            {
                Wolf wolf = (Wolf) world.spawnCreature(p.getLocation(), CreatureType.WOLF);
                wolf.setTamed(true);
                wolf.setOwner(p);
                wolf.setHealth(20);
                if (hellhounds)
                    wolf.setFireTicks(32768);
                pets.add(wolf);
            }
        }
    }
    
    private void startSpawning()
    {
        // Set the spawn flags to enable monster spawning.
        MAUtils.setSpawnFlags(plugin, world, 1, allowMonsters, allowAnimals);
        
        // Start the spawnThread.
        spawnThread  = new MASpawnThread(plugin, this);
        spawnTaskId  = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, spawnThread, waveDelay, (!waveClear) ? waveInterval : 60);
    }
    
    private void stopSpawning()
    {
        // Stop the spawn thread.
        if (spawnThread != null)
        {
            Bukkit.getServer().getScheduler().cancelTask(spawnThread.taskId);
            Bukkit.getServer().getScheduler().cancelTask(spawnTaskId);
            spawnTaskId = -1;
            spawnThread = null;
        }
        
        // Restore spawn flags.
        MAUtils.setSpawnFlags(plugin, world, spawnMonsters, allowMonsters, allowAnimals);
    }
    
    private void updateChunk(Location loc)
    {
        if (!arenaPlayers.isEmpty() || !world.getName().equals(loc.getWorld().getName()))
            return;
        
        Chunk chunk = world.getChunkAt(loc);
        if (!world.isChunkLoaded(chunk))
            world.loadChunk(chunk);
        else
            world.refreshChunk(chunk.getX(), chunk.getZ());
    }
    
    public void playerKill(Player p)
    {
    	if (p == null || killMap.get(p) == null)
    		return;
    	
        killMap.put(p, killMap.get(p) + 1);
    }
    
    public void restoreInvAndGiveRewards(final Player p, final boolean clear)
    {
        final List<ItemStack> rewards = rewardMap.get(p);
        final boolean hadRewards = rewardedPlayers.contains(p);
        
        if (clear) MAUtils.clearInventory(p);
        
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin,
            new Runnable()
            {
                public void run()
                { 
                    //if (clear)
                    //    MAUtils.clearInventory(p);
                    
                    if (!emptyInvJoin)
                        MAUtils.restoreInventory(p);
                    
                    //if (rewardedPlayers.contains(p))
                    if (hadRewards)
                        return;
                    
                    MAUtils.giveRewards(p, rewards, plugin);
                    if (running)
                        rewardedPlayers.add(p);
                }
            });
    }
    
    
    
    /*////////////////////////////////////////////////////////////////////
    //
    //      Items & Cleanup
    //
    ////////////////////////////////////////////////////////////////////*/
    
    public void assignClass(Player p, String className)
    {
        petMap.remove(p);
        randoms.remove(p);
        classMap.put(p, className);
        
        MAUtils.clearInventory(p);
        
        // If random, don't give any items yet.
        if (className.equalsIgnoreCase("random"))
        {
            randoms.add(p);
            return;
        }
        
        MAUtils.giveItems(p, classItems.get(className), autoEquip, plugin);
        MAUtils.giveItems(p, classArmor.get(className), autoEquip, plugin);
        
        int pets = MAUtils.getPetAmount(p);
        if (pets > 0) petMap.put(p, pets);
    }
    
    public void assignRandomClass(Player p)
    {
        Random r = new Random();
        List<String> classes = new LinkedList<String>(plugin.getAM().classes);

        String className = classes.remove(r.nextInt(classes.size()));
        while (!plugin.has(p, "mobarena.classes." + className))
        {
            if (classes.isEmpty())
            {
                System.out.println("[MobArena] ERROR! Player '" + p.getName() + "' has no class permissions!");
                playerLeave(p);
                return;
            }
            className = classes.remove(r.nextInt(classes.size()));
        }
        
        assignClass(p, className);
        MAUtils.tellPlayer(p, MAMessages.get(Msg.LOBBY_CLASS_PICKED, className));
    }
    
    private void cleanup()
    {
        removeMonsters();
        removeBlocks();
        removePets();
        removeEntities();
        monsters.clear();
        blocks.clear();
        pets.clear();
    }
    
    private void removeMonsters()
    {
        for (LivingEntity e : monsters)
            e.remove();
    }
    
    private void removeBlocks()
    {
        for (Block b : blocks)
            b.setTypeId(0);
    }
    
    private void removePets()
    {
        for (Wolf w : pets)
        {
            w.setOwner(null);
            w.remove();
        }
    }
    
    private void removePets(Player p)
    {
        for (Wolf w : pets)
        {
            if (!((Player) w.getOwner()).getName().equals(p.getName()))
                continue;
            
            w.setOwner(null);
            w.remove();
        }
        petMap.remove(p);
    }
    
    private void removeEntities()
    {
        Chunk c1 = world.getChunkAt(p1);
        Chunk c2 = world.getChunkAt(p2);
        
        for (int i = c1.getX(); i <= c2.getX(); i++)
            for (int j = c1.getZ(); j <= c2.getZ(); j++)
                for (Entity e : world.getChunkAt(i,j).getEntities())
                    if ((e instanceof Item || e instanceof Slime) && inRegion(e.getLocation()))
                        e.remove();
    }
    
    
    
    /*////////////////////////////////////////////////////////////////////
    //
    //      Initialization & Checks
    //
    ////////////////////////////////////////////////////////////////////*/
    
    public void load(Configuration config)
    {
        config.load();
        
        String arenaPath = "arenas." + MAUtils.nameArenaToConfig(name) + ".settings.";
        String configName = MAUtils.nameArenaToConfig(name);
        
        enabled          = config.getBoolean(arenaPath + "enabled", true);
        protect          = config.getBoolean(arenaPath + "protect", true);
        logging          = config.getBoolean(arenaPath + "logging", false);
        autoEquip        = config.getBoolean(arenaPath + "auto-equip-armor", true);
        waveClear        = config.getBoolean(arenaPath + "clear-wave-before-next", false);
        detCreepers      = config.getBoolean(arenaPath + "detonate-creepers", false);
        detDamage        = config.getBoolean(arenaPath + "detonate-damage", false);
        lightning        = config.getBoolean(arenaPath + "lightning", true);
        forceRestore     = config.getBoolean(arenaPath + "force-restore", false);
        softRestore      = config.getBoolean(arenaPath + "soft-restore", false);
        softRestoreDrops = config.getBoolean(arenaPath + "soft-restore-drops", false);
        emptyInvJoin     = config.getBoolean(arenaPath + "require-empty-inv-join", true);
        emptyInvSpec     = config.getBoolean(arenaPath + "require-empty-inv-spec", true);
        hellhounds       = config.getBoolean(arenaPath + "hellhounds", false);
        pvp              = config.getBoolean(arenaPath + "pvp-enabled", false);
        monsterInfight   = config.getBoolean(arenaPath + "monster-infight", false);
        allowWarp        = config.getBoolean(arenaPath + "allow-teleporting", false);
        specOnDeath      = config.getBoolean(arenaPath + "spectate-on-death", true);
        shareInArena     = config.getBoolean(arenaPath + "share-items-in-arena", true);
        joinDistance     = config.getInt(arenaPath + "max-join-distance", 0);
        playerLimit      = config.getInt(arenaPath + "player-limit", 0);
        repairDelay      = config.getInt(arenaPath + "repair-delay", 5);
        waveDelay        = config.getInt(arenaPath + "first-wave-delay", 5) * 20;
        waveInterval     = config.getInt(arenaPath + "wave-interval", 20) * 20;
        specialModulo    = config.getInt(arenaPath + "special-modulo", 4);
        maxIdleTime      = config.getInt(arenaPath + "max-idle-time", 0) * 20;

        distDefault      = MAUtils.getArenaDistributions(config, configName, "default");
        distSpecial      = MAUtils.getArenaDistributions(config, configName, "special");
        everyWaveMap     = MAUtils.getArenaRewardMap(config, configName, "every");
        afterWaveMap     = MAUtils.getArenaRewardMap(config, configName, "after");
        entryFee         = MAUtils.getEntryFee(config, configName);
        
        p1               = MAUtils.getArenaCoord(config, world, configName, "p1");
        p2               = MAUtils.getArenaCoord(config, world, configName, "p2");
        l1               = MAUtils.getArenaCoord(config, world, configName, "l1");
        l2               = MAUtils.getArenaCoord(config, world, configName, "l2");
        arenaLoc         = MAUtils.getArenaCoord(config, world, configName, "arena");
        lobbyLoc         = MAUtils.getArenaCoord(config, world, configName, "lobby");
        spectatorLoc     = MAUtils.getArenaCoord(config, world, configName, "spectator");
        spawnpoints      = MAUtils.getArenaSpawnpoints(config, world, configName);
        
        classes          = plugin.getAM().classes;
        classItems       = plugin.getAM().classItems;
        classArmor       = plugin.getAM().classArmor;
        
        // Determine if the arena is properly set up. Then add the to arena list.
        setup            = MAUtils.verifyData(this);
        lobbySetup       = MAUtils.verifyLobby(this);
    }
    
    public void serializeConfig()
    {
        String coords = "arenas." + configName() + ".coords.";
        Configuration config = plugin.getConfig();
        
        config.setProperty("arenas." + configName() + ".settings.enabled", enabled);
        config.setProperty("arenas." + configName() + ".settings.protect", protect);
        if (p1 != null)           config.setProperty(coords + "p1",        MAUtils.makeCoord(p1));
        if (p2 != null)           config.setProperty(coords + "p2",        MAUtils.makeCoord(p2));
        if (l1 != null)           config.setProperty(coords + "l1",        MAUtils.makeCoord(l1));
        if (l2 != null)           config.setProperty(coords + "l2",        MAUtils.makeCoord(l2));
        if (arenaLoc != null)     config.setProperty(coords + "arena",     MAUtils.makeCoord(arenaLoc));
        if (lobbyLoc != null)     config.setProperty(coords + "lobby",     MAUtils.makeCoord(lobbyLoc));
        if (spectatorLoc != null) config.setProperty(coords + "spectator", MAUtils.makeCoord(spectatorLoc));
        for (Map.Entry<String,Location> entry : spawnpoints.entrySet())
            config.setProperty(coords + "spawnpoints." + entry.getKey(), MAUtils.makeCoord(entry.getValue()));
        
        config.save();
    }
    
    public void deserializeConfig()
    {
        Configuration config = plugin.getConfig();
        config.load();
        load(config);
    }
    
    public boolean serializeRegion()
    {
        int x1 = (int) p1.getX();
        int y1 = (int) p1.getY();
        int z1 = (int) p1.getZ();
        int x2 = (int) p2.getX();
        int y2 = (int) p2.getY();
        int z2 = (int) p2.getZ();

        HashSet<int[]> set = new HashSet<int[]>();
        int[] buffer;
        for (int i = x1; i <= x2; i++)
        {
            for (int j = y1; j <= y2; j++)
            {
                for (int k = z1; k <= z2; k++)
                {
                    buffer = new int[4];
                    buffer[0] = i;
                    buffer[1] = j;
                    buffer[2] = k;
                    buffer[3] = world.getBlockAt(i,j,k).getTypeId();
                    set.add(buffer);
                }
            }
        }
        
        try
        {
            new File(plugin.getDataFolder() + File.separator + "regions").mkdir();
            File regionFile = new File(plugin.getDataFolder() + File.separator + "regions" + File.separator + configName() + ".tmp");
            if (regionFile.exists())
                regionFile.createNewFile();
            
            FileOutputStream fos = new FileOutputStream(regionFile);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(set);
            oos.close();
        }
        catch (Exception e)
        {
            System.out.println("[MobArena] ERROR! Could not create region file. The arena will not be started!");
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    @SuppressWarnings("unchecked")
    public boolean deserializeRegion()
    {
        HashSet<int[]> set = new HashSet<int[]>();
        try
        {
            File regionFile = new File(plugin.getDataFolder() + File.separator + "regions" + File.separator + configName() + ".tmp");
            if (!regionFile.exists())
                return false;
            
            FileInputStream fis = new FileInputStream(regionFile);
            ObjectInputStream ois = new ObjectInputStream(fis);
            set = (HashSet<int[]>) ois.readObject();
            ois.close();
        }
        catch (Exception e)
        {
            System.out.println("[MobArena] ERROR! Could not find region file. The arena cannot be restored!");
            e.printStackTrace();
            return false;
        }
        
        for (int[] buffer : set)
            world.getBlockAt(buffer[0], buffer[1], buffer[2]).setTypeId(buffer[3]);
        
        return true;
    }
    
    /**
     * Check if a location is inside of the cuboid region
     * that p1 and p2 span.
     */
    public boolean inRegion(Location loc)
    {
        if (!loc.getWorld().getName().equals(world.getName()) || !setup)
            return false;
        
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        
        // Check the lobby first.
        if (lobbySetup)
        {
            if ((x >= l1.getBlockX() && x <= l2.getBlockX()) &&            
                (z >= l1.getBlockZ() && z <= l2.getBlockZ()) &&           
                (y >= l1.getBlockY() && y <= l2.getBlockY()))
                return true;
        }
        
        // Returns false if the location is outside of the region.
        return ((x >= p1.getBlockX() && x <= p2.getBlockX()) &&            
                (z >= p1.getBlockZ() && z <= p2.getBlockZ()) &&           
                (y >= p1.getBlockY() && y <= p2.getBlockY()));
    }
    
    /**
     * Check if a location is inside of the arena region, expanded
     * by 'radius' blocks. Used with explosions.
     */
    public boolean inRegionRadius(Location loc, int radius)
    {
        if (!loc.getWorld().getName().equals(world.getName()) || !setup)
            return false;
        
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        
        // Check the lobby first.
        if (lobbySetup)
        {
            if ((x + radius >= l1.getBlockX() && x - radius <= l2.getBlockX()) &&            
                (z + radius >= l1.getBlockZ() && z - radius <= l2.getBlockZ()) &&           
                (y + radius >= l1.getBlockY() && y - radius <= l2.getBlockY()))
                return true;
        }
        
        return ((x + radius >= p1.getBlockX() && x - radius <= p2.getBlockX()) &&
                (z + radius >= p1.getBlockZ() && z - radius <= p2.getBlockZ()) &&
                (y + radius >= p1.getBlockY() && y - radius <= p2.getBlockY()));
    }
    
    
    
    /*////////////////////////////////////////////////////////////////////
    //
    //      Getters & Misc
    //
    ////////////////////////////////////////////////////////////////////*/
    
    public String configName()
    {
        return MAUtils.nameArenaToConfig(name);
    }
    
    public String arenaName()
    {
        return name;
    }
    
    public List<Player> getAllPlayers()
    {
        List<Player> result = new LinkedList<Player>();
        result.addAll(arenaPlayers);
        result.addAll(lobbyPlayers);
        result.addAll(specPlayers);
        return result;
    }
    
    public List<Player> getLivingPlayers()
    {
        List<Player> result = new LinkedList<Player>();
        result.addAll(arenaPlayers);
        return result;
    }
    
    public List<Player> getNonreadyPlayers()
    {
        List<Player> result = new LinkedList<Player>();
        result.addAll(lobbyPlayers);
        result.removeAll(readyPlayers);
        return result;
    }
    
    public void resetIdleTimer()
    {
        if (maxIdleTime <= 0 || !running)
            return;
        
        // Reset the previousSize, cancel the previous timer, and start the new timer.
        spawnThread.previousSize = monsters.size();
        Bukkit.getServer().getScheduler().cancelTask(spawnThread.taskId);
        spawnThread.taskId = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin,
            new Runnable()
            {
                public void run()
                {
                    // Make sure to remove any dead/removed entities first.
                    List<Entity> tmp = new LinkedList<Entity>(monsters);
                    for (Entity e : tmp)
                        if (e.isDead())
                            monsters.remove(e);
                    
                    // Compare the current size with the previous size.
                    if (monsters.size() < spawnThread.previousSize || spawnThread.previousSize == 0)
                    {
                        resetIdleTimer();
                        return;
                    }
                    
                    // Clear all player inventories, and "kill" all players.
                    for (Player p : arenaPlayers)
                    {
                        MAUtils.clearInventory(p);
                        MAUtils.tellPlayer(p, MAMessages.get(Msg.FORCE_END_IDLE));
                        playerDeath(p);
                    }
                }
            }, maxIdleTime);
    }
    
    public void addTrunkAndLeaves(Block b)
    {
        final int x = b.getX();
        final int y = b.getY();
        final int z = b.getZ();
        
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin,
            new Runnable()
            {
                public void run()
                {
                    List<Block> result = new LinkedList<Block>();
                    for (int i = x-2; i <= x+2; i++)
                        for (int j = y; j <= y+10; j++)
                            for (int k = z-2; k <= z+2; k++)
                                if (world.getBlockAt(i,j,k).getType() == Material.LOG || world.getBlockAt(i,j,k).getType() == Material.LEAVES)
                                    result.add(world.getBlockAt(i,j,k));
                    
                    if (running) blocks.addAll(result);
                }
            }, 10);
    }
    
    public boolean canAfford(Player p)
    {
        if (entryFee.isEmpty())
            return true;
        
        PlayerInventory inv = p.getInventory();
        for (ItemStack stack : entryFee)
        {
            // Economy money
            if (stack.getTypeId() == MobArena.ECONOMY_MONEY_ID)
            {
                if (plugin.Methods.hasMethod() && !plugin.Method.getAccount(p.getName()).hasEnough(stack.getAmount()))                
                    return false;
            }
            // Normal stack
            else
            {
                if (!inv.contains(stack.getTypeId(), stack.getAmount()))
                    return false;
            }
        }
        
        return true;
    }
    
    public boolean takeFee(Player p)
    {
        if (entryFee.isEmpty())
            return true;
        
        PlayerInventory inv = p.getInventory();
        for (ItemStack stack : entryFee)
        {
            // Economy money
            if (stack.getTypeId() == MobArena.ECONOMY_MONEY_ID)
            {
                if (plugin.Methods.hasMethod() && !plugin.Method.getAccount(p.getName()).subtract(stack.getAmount()))                
                    return false;
            }

            // Normal stack
            else
            {
                int id = stack.getTypeId();
                int amount = stack.getAmount();
                
                while (amount > 0)
                {
                    int pos = inv.first(id);
                    if (pos == -1) return false;
                    
                    ItemStack is = inv.getItem(pos);
                    if (is.getAmount() > amount)
                    {
                        is.setAmount(is.getAmount() - amount);
                        amount = 0;
                    }
                    else
                    {
                        amount -= is.getAmount();
                        inv.setItem(pos, null);
                    }
                }
            }
        }
        
        hasPaid.add(p);
        return true;
    }
    
    public void refund(Player p)
    {
        if (!hasPaid.contains(p))
            return;
        
        MAUtils.giveItems(p, entryFee, false, plugin);
    }
    
    /**
     * The "perfect equals method" cf. "Object-Oriented Design and Patterns"
     * by Cay S. Horstmann.
     */
    public boolean equals(Object other)
    {
        if (this == other) return true;
        if (other == null) return false;
        if (getClass() != other.getClass()) return false;
        
        // Arenas must have different names.
        if (other instanceof Arena && ((Arena)other).name.equals(name))
            return true;
        
        return false;
    }
    
    public String toString()
    {
        return ((enabled && setup) ? ChatColor.GREEN : ChatColor.GRAY) + configName();
    }
}
