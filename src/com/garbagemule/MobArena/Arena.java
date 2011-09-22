package com.garbagemule.MobArena;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.PriorityBlockingQueue;

import net.minecraft.server.WorldServer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.ContainerBlock;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Creature;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.util.config.Configuration;

import com.garbagemule.MobArena.MAMessages.Msg;
import com.garbagemule.MobArena.repairable.Repairable;
import com.garbagemule.MobArena.repairable.RepairableComparator;
import com.garbagemule.MobArena.repairable.RepairableContainer;
import com.garbagemule.MobArena.spout.Spouty;
import com.garbagemule.MobArena.util.InventoryItem;
import com.garbagemule.MobArena.util.WaveUtils;
import com.garbagemule.MobArena.waves.BossWave;
import com.garbagemule.MobArena.waves.Wave;
import com.garbagemule.MobArena.waves.Wave.WaveBranch;

import com.herocraftonline.dev.heroes.persistence.Hero;

public class Arena
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
    protected int spawnTaskId, sheepTaskId, waveDelay, waveInterval, specialModulo, spawnMonstersInt, maxIdleTime;
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
    protected int repairDelay, minPlayers, maxPlayers, joinDistance;
    protected List<String> classes = new LinkedList<String>();
    protected Map<Player,Location> locations = new HashMap<Player,Location>();
    protected Map<Player,Integer> healthMap = new HashMap<Player,Integer>();
    protected Map<Player,Integer> hungerMap = new HashMap<Player,Integer>();
    
    // Logging
    protected ArenaLog log;
    
    protected MAListener eventListener;
    
    protected PriorityBlockingQueue<Repairable> repairQueue;
    
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
        
        running         = false;
        edit            = false;
        
        allowMonsters   = world.getAllowMonsters();
        allowAnimals    = world.getAllowAnimals();
        spawnMonsters   = ((net.minecraft.server.World) ((CraftWorld) world).getHandle()).spawnMonsters;
        
        eventListener   = new MAListener(this, plugin);
        repairQueue     = new PriorityBlockingQueue<Repairable>(100, new RepairableComparator());
    }
    
    public boolean startArena()
    {
        // Sanity-checks
        if (running || lobbyPlayers.isEmpty() || !readyPlayers.containsAll(lobbyPlayers))
            return false;
        if (!softRestore && forceRestore && !serializeRegion())
            return false;

        // Store all chest contents.
        storeContainerContents();
        
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
            if (plugin.getHeroManager() != null)
            {
                Hero hero = plugin.getHeroManager().getHero(p);
                hero.setHealth(hero.getMaxHealth());
            }
            p.setHealth(20);
            p.setFoodLevel(20);
            assignClassPermissions(p);
        }
        
        // Copy the singleWaves Set for polling.
        singleWavesInstance = new TreeSet<Wave>(singleWaves);
        
        // Start spawning monsters (must happen before 'running = true;')
        startSpawning();
        startBouncingSheep();
        
        // Set the boolean.
        running = true;
        
        // Spawn pets (must happen after 'running = true;')
        spawnPets();
        
        // Start logging
        log = new ArenaLog(plugin, this);
        log.start();
        
        // Announce and notify.
        MAUtils.tellAll(this, Msg.ARENA_START);
        
        for (MobArenaListener listener : plugin.getAM().listeners)
            listener.onArenaStart(this);
        
        return true;
    }
    
    public boolean endArena()
    {
        // Sanity-checks.
        if (!running || !arenaPlayers.isEmpty())
        {
            //System.out.println("Arena was not ended, playercount: " + arenaPlayers.size());
            return false;
        }
        //else System.out.println("Arena ending...");
        
        // Set the boolean.
        running = false;
        
        // Finish logging
        log.end();
        if (logging != null)
        {
            log.saveSessionData();
            log.updateArenaTotals();
        }
        log.clearSessionData();
        
        // Stop spawning.
        stopSpawning();

        // Clean arena floor.
        cleanup();
        
        // Restore region.
        if (softRestore)
            restoreRegion();
        else if (forceRestore)
            deserializeRegion();
        
        // Restore chests
        restoreContainerContents();

        // Announce and clear sets.
        MAUtils.tellAll(this, Msg.ARENA_END, true);
        arenaPlayers.clear();
        lobbyPlayers.clear();
        readyPlayers.clear();
        notifyPlayers.clear();
        rewardedPlayers.clear();
        classMap.clear();
        
        // Notify listeners.
        for (MobArenaListener listener : plugin.getAM().listeners)
            listener.onArenaEnd(this);
        
        return true;
    }
    
    public void forceStart()
    {
        if (running)
            return;
        
        // Set operations.
        Set<Player> tmp = new HashSet<Player>();
        tmp.addAll(lobbyPlayers);
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
        Bukkit.getServer().getScheduler().cancelTask(spawnTaskId);
        Bukkit.getServer().getScheduler().cancelTask(sheepTaskId);
        
        for (Player p : getAllPlayers())
            playerLeave(p);
        
        for (Entity e : monsters)
            e.remove();
        
        if (bossWave != null)
            bossWave.clear();
        
        arenaPlayers.clear();
        lobbyPlayers.clear();
        readyPlayers.clear();
        
        cleanup();

        spawnTaskId = -1;
        sheepTaskId = -1;
    }
    
    public void playerJoin(Player p, Location loc)
    {
        storePlayerData(p, loc);
        MAUtils.sitPets(p);
        if (plugin.getHeroManager() != null)
        {
            Hero hero = plugin.getHeroManager().getHero(p);
            hero.setHealth(hero.getMaxHealth());
        }
        p.setHealth(20);
        p.setFoodLevel(20);
        movePlayerToLobby(p);
        
        // Notify listeners.
        for (MobArenaListener listener : plugin.getAM().listeners)
            listener.onPlayerJoin(this, p);
        
        if (MobArena.hasSpout && spoutSelect)
            Spouty.classSelectionScreen(plugin, this, p);
    }
    
    public void playerReady(Player p)
    {
        readyPlayers.add(p);
        
        if (minPlayers > 0 && lobbyPlayers.size() < minPlayers)
        {
            MAUtils.tellPlayer(p, Msg.LOBBY_NOT_ENOUGH_PLAYERS, "" + minPlayers);
            return;
        }
        
        startArena();
    }
    
    public void playerLeave(Player p)
    {
        finishArenaPlayer(p, false);     
        movePlayerToEntry(p);
        discardPlayer(p);
        
        // Notify listeners.
        for (MobArenaListener listener : plugin.getAM().listeners)
            listener.onPlayerLeave(this, p);
        
        // End arena if possible.
        endArena();
    }
    
    public void playerDeath(Player p)
    {
        finishArenaPlayer(p, true);
        
        if (specOnDeath)
        {
            clearPlayer(p);
            movePlayerToSpec(p);
        }
        else
        {
            movePlayerToEntry(p);
            discardPlayer(p);
        }
        
        if (running && spawnThread != null)
            spawnThread.updateTargets();
        
        MAUtils.tellAll(this, Msg.PLAYER_DIED, p.getName());
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin,
            new Runnable()
            {
                public void run()
                {
                    endArena();
                }
            });
        
        // Notify listeners.
        for (MobArenaListener listener : plugin.getAM().listeners)
            listener.onPlayerDeath(this, p);
    }
    
    public void playerSpec(Player p, Location loc)
    {
        storePlayerData(p, loc);
        MAUtils.sitPets(p);
        movePlayerToSpec(p);
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
            Bukkit.getServer().getScheduler().cancelTask(spawnThread.getTaskId());
            Bukkit.getServer().getScheduler().cancelTask(spawnTaskId);
            Bukkit.getServer().getScheduler().cancelTask(sheepTaskId);
            spawnTaskId = -1;
            sheepTaskId = -1;
            spawnThread = null;
            currentWave = null;
        }
        else System.out.println("--------- THE SPAWNTHREAD IS NULL! ----------");
        
        // Restore spawn flags.
        MAUtils.setSpawnFlags(plugin, world, spawnMonsters, allowMonsters, allowAnimals);
    }
    
    private void startBouncingSheep()
    {
        sheepTaskId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin,
            new Runnable()
            {
                public void run()
                {
                    if (explodingSheep.isEmpty()) return;
                    
                    for (LivingEntity e : new LinkedList<LivingEntity>(explodingSheep))
                    {
                        Creature c = (Creature) e;
                        if (c.getTarget() != null && e.getLocation().distanceSquared(c.getTarget().getLocation()) < 8)
                        {
                            CraftEntity ce = (CraftEntity) e;
                            CraftWorld cw = (CraftWorld) e.getWorld();
                            WorldServer ws = cw.getHandle();
                            ws.createExplosion(ce.getHandle(), e.getLocation().getX(), e.getLocation().getY() + 1, e.getLocation().getZ(), 2f, false);
                            e.remove();
                        }
                        
                        if (e.isDead())
                        {
                            explodingSheep.remove(e);
                            continue;
                        }
                        
                        if (Math.abs(e.getVelocity().getY()) < 1)
                            e.setVelocity(e.getVelocity().setY(0.5));
                    }
                }
            }, waveDelay, 20);
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
    	if (p == null || log.players.get(p) == null)
    		return;
    	
        log.players.get(p).kills++;
    }
    
    public void restoreInvAndGiveRewardsDelayed(final Player p)
    {        
        final List<ItemStack> rewards = log != null && log.players.get(p) != null ? log.players.get(p).rewards : new LinkedList<ItemStack>();
        final boolean hadRewards = rewardedPlayers.contains(p);
        
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin,
            new Runnable()
            {
                public void run()
                {                    
                    if (!emptyInvJoin)
                        MAUtils.restoreInventory(p);
                    
                    if (hadRewards)
                        return;
                    
                    MAUtils.giveRewards(p, rewards, plugin);
                    if (running)
                        rewardedPlayers.add(p);
                }
            });
    }
    
    public void restoreInvAndGiveRewards(final Player p)
    {
        if (!emptyInvJoin)
            MAUtils.restoreInventory(p);
        
        if (rewardedPlayers.contains(p))
            return;
        
        final List<ItemStack> rewards = (log != null && log.players.get(p) != null) ?
                                         log.players.get(p).rewards :
                                         new LinkedList<ItemStack>();
        
        MAUtils.giveRewards(p, rewards, plugin);
        if (running)
            rewardedPlayers.add(p);
    }
    
    public void storePlayerData(Player p, Location loc)
    {
        plugin.getAM().arenaMap.put(p, this);
        
        if (!locations.containsKey(p))
            locations.put(p, loc);

        if (!healthMap.containsKey(p))
            healthMap.put(p, p.getHealth());
        
        if (!hungerMap.containsKey(p))
            hungerMap.put(p, p.getFoodLevel());
    }
    
    public void storeContainerContents()
    {
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin,
            new Runnable()
            {
                public void run()
                {
                    for (Location loc : containers.values())
                    {
                        BlockState state = world.getBlockAt(loc).getState();
                        if (state instanceof ContainerBlock)
                            containables.add(new RepairableContainer(state, false));
                    }
                }
            });
    }
    
    public void restoreContainerContents()
    {
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin,
            new Runnable()
            {
                public void run()
                {
                    for (Repairable r : containables)
                        r.repair();
                }
            });
    }
    
    public void movePlayerToLobby(Player p)
    {
        updateChunk(lobbyLoc);
        lobbyPlayers.add(p);
        p.teleport(lobbyLoc);
    }
    
    public void movePlayerToSpec(Player p)
    {
        updateChunk(spectatorLoc);
        specPlayers.add(p);
        p.teleport(spectatorLoc);
    }
    
    public void movePlayerToEntry(Player p)
    {
        Location entry = locations.get(p);
        if (entry == null || p.isDead()) return;
        
        updateChunk(entry);
        p.teleport(entry);
    }
    
    private void clearPlayer(Player p)
    {
        if (healthMap.containsKey(p))
        {
            int health = healthMap.remove(p);
            p.setHealth(health);
            if (plugin.getHeroManager() != null)
            {
                Hero hero = plugin.getHeroManager().getHero(p);
                hero.setHealth(health * hero.getMaxHealth() / 20);
            }
        }
        
        if (hungerMap.containsKey(p))
            p.setFoodLevel(hungerMap.remove(p));

        // Put out fire.
        p.setFireTicks(0);
        
        // Remove pets.
        removePets(p);
        
        // readyPlayers before lobbyPlayers because of startArena sanity-checks
        readyPlayers.remove(p);
        specPlayers.remove(p);
        arenaPlayers.remove(p);
        lobbyPlayers.remove(p);
        classMap.remove(p);
    }
    
    /**
     * Completely remove a player from the arena collections.
     * @param p A player
     */
    private void discardPlayer(Player p)
    {
        locations.remove(p);
        plugin.getAM().arenaMap.remove(p);
        clearPlayer(p);
    }
    
    /**
     * Give the player back his inventory and record his last wave.
     * Called when a player dies or leaves prematurely. 
     * @param p A player
     * @param dead If the player died or not
     */    
    private void finishArenaPlayer(Player p, boolean dead)
    {
        if (!arenaPlayers.contains(p) && !lobbyPlayers.contains(p))
            return;
        
        removeClassPermissions(p);
        MAUtils.clearInventory(p);
        
        if (dead) restoreInvAndGiveRewardsDelayed(p);
        else      restoreInvAndGiveRewards(p);
        
        if (log != null && spawnThread != null)
            log.players.get(p).lastWave = spawnThread.getWave() - 1;
    }
    
    public void repairBlocks()
    {
        while (!repairQueue.isEmpty())
            repairQueue.poll().repair();
    }
    
    public void queueRepairable(Repairable r)
    {
        repairQueue.add(r);
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
                MobArena.info("Player '" + p.getName() + "' has no class permissions!");
                playerLeave(p);
                return;
            }
            className = classes.remove(r.nextInt(classes.size()));
        }
        
        assignClass(p, className);
        MAUtils.tellPlayer(p, Msg.LOBBY_CLASS_PICKED, className);
    }
    
    public void assignClassPermissions(Player p)
    {        
        Map<String,Boolean> perms = classPerms.get(classMap.get(p));
        if (perms == null || perms.isEmpty()) return;

        PermissionAttachment pa = p.addAttachment(plugin);
        attachments.put(p,pa);
        for (Map.Entry<String,Boolean> entry : perms.entrySet())
            pa.setPermission(entry.getKey(), entry.getValue());
    }
    
    public void removeClassPermissions(Player p)
    {
        if (attachments.get(p) == null) return;
        
        for (PermissionAttachment pa : attachments.values())
            if (pa != null) pa.remove();
    }
    
    private void cleanup()
    {
        removeMonsters();
        removeBlocks();
        removePets();
        removeEntities();
        monsters.clear();
        explodingSheep.clear();
        plaguedPigs.clear();
        madCows.clear();
        blocks.clear();
        pets.clear();
    }
    
    private void removeMonsters()
    {
        if (bossWave != null)
            bossWave.clear();
        for (LivingEntity e : monsters)
            e.remove();
        for (LivingEntity e : explodingSheep)
            e.remove();
        for (LivingEntity e : plaguedPigs)
            e.remove();
        for (LivingEntity e : madCows)
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
        if (p1 == null || p2 == null) return;
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
        logging          = config.getString(arenaPath + "logging", null);
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
        spoutSelect      = config.getBoolean(arenaPath + "spout-class-select", true);
        joinDistance     = config.getInt(arenaPath + "max-join-distance", 0);
        minPlayers       = config.getInt(arenaPath + "min-players", 0);
        maxPlayers       = config.getInt(arenaPath + "max-players", 0);
        repairDelay      = config.getInt(arenaPath + "repair-delay", 5);
        waveDelay        = config.getInt(arenaPath + "first-wave-delay", 5) * 20;
        waveInterval     = config.getInt(arenaPath + "wave-interval", 20) * 20;
        specialModulo    = config.getInt(arenaPath + "special-modulo", 4);
        maxIdleTime      = config.getInt(arenaPath + "max-idle-time", 0) * 20;
        
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
        spawnpointsBoss  = MAUtils.getArenaBossSpawnpoints(config, world, configName);
        containers       = MAUtils.getArenaContainers(config, world, configName);
        
        // NEW WAVES
        singleWaves      = WaveUtils.getWaves(this, config, WaveBranch.SINGLE);
        recurrentWaves   = WaveUtils.getWaves(this, config, WaveBranch.RECURRENT);
        
        classes          = plugin.getAM().classes;
        classItems       = plugin.getAM().classItems;
        classArmor       = plugin.getAM().classArmor;
        classPerms       = plugin.getAM().classPerms;
        
        // Determine if the arena is properly set up. Then add the to arena list.
        setup            = MAUtils.verifyData(this);
        lobbySetup       = MAUtils.verifyLobby(this);
    }
    
    public void restoreRegion()
    {
        Collections.sort(repairables, new RepairableComparator());
        
        for (Repairable r : repairables)
            r.repair();
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
        for (Map.Entry<String,Location> entry : spawnpointsBoss.entrySet())
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
            MobArena.warning("Could not create region file. The arena will not be started!");
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
            MobArena.warning("Could not find region file. The arena cannot be restored!");
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
    
    public boolean isRunning()
    {
        return running;
    }
    
    public boolean isPvpEnabled()
    {
        return pvp;
    }
    
    public boolean isLightningEnabled()
    {
        return lightning;
    }
    
    public boolean isBossWave()
    {
        return bossWave != null;
    }
    
    public String configName()
    {
        return MAUtils.nameArenaToConfig(name);
    }
    
    public String arenaName()
    {
        return name;
    }
    
    public MobArena getPlugin()
    {
        return plugin;
    }
    
    public World getWorld()
    {
        return world;
    }
    
    public Wave getWave()
    {
        return currentWave;
    }
    
    public void setWave(Wave wave)
    {
        currentWave = wave;
    }

    public void setBossWave(BossWave bossWave)
    {
        this.bossWave = bossWave;
    }
    
    public List<String> getClasses()
    {
        return classes;
    }
    
    public List<Location> getAllSpawnpoints()
    {
        ArrayList<Location> result = new ArrayList<Location>(spawnpoints.size() + spawnpointsBoss.size());
        result.addAll(spawnpoints.values());
        result.addAll(spawnpointsBoss.values());
        return result;
    }
    
    public List<Location> getSpawnpoints()
    {
        return new ArrayList<Location>(spawnpoints.values());
    }
    
    public Location getBossSpawnpoint()
    {
        if (spawnpointsBoss.isEmpty())
            return getSpawnpoints().get(0);
        
        return new ArrayList<Location>(spawnpointsBoss.values()).get(MobArena.random.nextInt(spawnpointsBoss.size()));
    }
    
    public int getPlayerCount()
    {
        return spawnThread.getPlayerCount();
    }
    
    public void addBlock(Block b)
    {
        blocks.add(b);
    }
    
    public void addMonster(LivingEntity e)
    {
        monsters.add(e);
    }
    
    public void addExplodingSheep(LivingEntity e)
    {
        explodingSheep.add(e);
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
    
    public Set<Player> getArenaPlayers()
    {
        return arenaPlayers;
    }
    
    public List<Player> getNonreadyPlayers()
    {
        List<Player> result = new LinkedList<Player>();
        result.addAll(lobbyPlayers);
        result.removeAll(readyPlayers);
        return result;
    }
    
    public Set<LivingEntity> getMonsters()
    {
        Set<LivingEntity> tmp = new HashSet<LivingEntity>(monsters);
        tmp.addAll(explodingSheep);
        return tmp;
    }
    
    public Set<Wolf> getPets()
    {
        return pets;
    }
    
    public void resetIdleTimer()
    {
        if (maxIdleTime <= 0 || !running)
            return;
        
        // Reset the previousSize, cancel the previous timer, and start the new timer.
        spawnThread.setPreviousSize(getMonsters().size());
        Bukkit.getServer().getScheduler().cancelTask(spawnThread.getTaskId());
        int id = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin,
            new Runnable()
            {
                public void run()
                {
                    // Make sure to remove any dead/removed entities first.
                    List<Entity> ms = new LinkedList<Entity>(monsters);
                    for (Entity e : ms)
                        if (e.isDead())
                            monsters.remove(e);
                    
                    // Compare the current size with the previous size.
                    if (monsters.size() < spawnThread.getPreviousSize() || spawnThread.getPreviousSize() == 0 || bossWave != null)
                    {
                        resetIdleTimer();
                        return;
                    }
                    
                    // Clear all player inventories, and "kill" all players.
                    List<Player> ps = new LinkedList<Player>(arenaPlayers);
                    for (Player p : ps)
                    {
                        MAUtils.clearInventory(p);
                        MAUtils.tellPlayer(p, Msg.FORCE_END_IDLE);
                        playerDeath(p);
                    }
                }
            }, maxIdleTime);
        spawnThread.setTaskId(id);
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
                //if (plugin.Methods.hasMethod() && !plugin.Method.getAccount(p.getName()).hasEnough(stack.getAmount()))                
                if (plugin.Method != null && !(plugin.Method.getAccount(p.getName()).balance() >= stack.getAmount()))
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
        InventoryItem[] items = InventoryItem.parseItemStacks(inv.getContents());
        InventoryItem[] fee   = InventoryItem.parseItemStacks(entryFee);

        // Take some economy money
        for (InventoryItem item : InventoryItem.extractAllFromArray(MobArena.ECONOMY_MONEY_ID, fee))
            if (plugin.Method != null)
                plugin.Method.getAccount(p.getName()).subtract(item.getAmount());

        // Take any other items
        for (InventoryItem item : fee)
            InventoryItem.removeItemFromArray(item, items);
        
        // Turn everything back into ItemStacks
        for (int i = 0; i < items.length; i++)
            inv.setItem(i, items[i].toItemStack());

        return true;
    }
    
    public boolean canJoin(Player p)
    {
        if (!enabled)
            MAUtils.tellPlayer(p, Msg.JOIN_ARENA_NOT_ENABLED);
        else if (!setup || recurrentWaves.isEmpty())
            MAUtils.tellPlayer(p, Msg.JOIN_ARENA_NOT_SETUP);
        else if (edit)
            MAUtils.tellPlayer(p, Msg.JOIN_ARENA_EDIT_MODE);
        else if (running && (notifyPlayers.contains(p) || notifyPlayers.add(p)))
            MAUtils.tellPlayer(p, Msg.JOIN_ARENA_IS_RUNNING);
        else if (arenaPlayers.contains(p) || lobbyPlayers.contains(p))
            MAUtils.tellPlayer(p, Msg.JOIN_ALREADY_PLAYING);
        else if (!plugin.has(p, "mobarena.arenas." + configName()))
            MAUtils.tellPlayer(p, Msg.JOIN_ARENA_PERMISSION);
        else if (maxPlayers > 0 && lobbyPlayers.size() >= maxPlayers)
            MAUtils.tellPlayer(p, Msg.JOIN_PLAYER_LIMIT_REACHED);
        else if (joinDistance > 0 && !inRegionRadius(p.getLocation(), joinDistance))
            MAUtils.tellPlayer(p, Msg.JOIN_TOO_FAR);
        else if (emptyInvJoin && !MAUtils.hasEmptyInventory(p))
            MAUtils.tellPlayer(p, Msg.JOIN_EMPTY_INV);
        else if (!canAfford(p))// || !takeFee(p))
            MAUtils.tellPlayer(p, Msg.JOIN_FEE_REQUIRED, MAUtils.listToString(entryFee, plugin));
        else return true;
        
        return false;
    }
    
    public boolean canSpec(Player p)
    {
        if (!enabled)
            MAUtils.tellPlayer(p, Msg.JOIN_ARENA_NOT_ENABLED);
        else if (!setup)
            MAUtils.tellPlayer(p, Msg.JOIN_ARENA_NOT_SETUP);
        else if (edit)
            MAUtils.tellPlayer(p, Msg.JOIN_ARENA_EDIT_MODE);
        else if (arenaPlayers.contains(p) || lobbyPlayers.contains(p))
            MAUtils.tellPlayer(p, Msg.SPEC_ALREADY_PLAYING);
        else if (emptyInvSpec && !MAUtils.hasEmptyInventory(p))
            MAUtils.tellPlayer(p, Msg.SPEC_EMPTY_INV);
        else if (joinDistance > 0 && !inRegionRadius(p.getLocation(), joinDistance))
            MAUtils.tellPlayer(p, Msg.JOIN_TOO_FAR);
        else return true;
        
        return false;
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
