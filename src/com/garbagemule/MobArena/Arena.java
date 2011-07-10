package com.garbagemule.MobArena;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Wolf;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.config.Configuration;

import com.garbagemule.MobArena.MAMessages.Msg;

public class Arena
{
    private MobArena plugin;
    
    // Setup fields
    protected String name;
    protected World world;
    protected boolean enabled, running, setup, lobbySetup, protect, autoEquip, forceRestore, softRestore, softRestoreDrops, emptyInvJoin, emptyInvSpec, pvp, monsterInfight, allowWarp;
    protected boolean edit, waveClear, detCreepers, detDamage, lightning, hellhounds;
    protected Location p1, p2, l1, l2, arenaLoc, lobbyLoc, spectatorLoc;
    protected Map<String,Location> spawnpoints;

    // Wave/reward fields
    protected int spawnTaskId, waveDelay, waveInterval, specialModulo, spawnMonstersInt, maxIdleTime;
    protected MASpawnThread spawnThread;
    protected Map<Integer,List<ItemStack>> everyWaveMap, afterWaveMap;
    protected Map<String,Integer> distDefault, distSpecial;
    protected Map<Player,String> classMap;
    protected Map<String,List<ItemStack>>  classItems, classArmor;
    protected Map<Integer,Map<Player,List<ItemStack>>> classBonuses;
    protected Map<Player,List<ItemStack>> rewardMap;
    
    // Arena sets/maps
    protected Set<Player>         livePlayers, deadPlayers, readyPlayers, specPlayers;
    protected Set<LivingEntity>   monsters;
    protected Set<Block>          blocks;
    protected Set<Wolf>           pets;
    protected Map<Player,Integer> petMap;
    protected Map<Player,Integer> kills;
    protected List<int[]>         repairList;
    
    // Spawn overriding
    protected int spawnMonsters;
    protected boolean allowMonsters, allowAnimals;
    
    // Other settings
    protected int repairDelay, playerLimit;
    protected List<String> classes = new LinkedList<String>();
    protected Map<Player,Location> locations = new HashMap<Player,Location>();
    
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
        
        livePlayers   = new HashSet<Player>();
        deadPlayers   = new HashSet<Player>();
        readyPlayers  = new HashSet<Player>();
        specPlayers   = new HashSet<Player>();
        monsters      = new HashSet<LivingEntity>();
        blocks        = new HashSet<Block>();
        pets          = new HashSet<Wolf>();
        petMap        = new HashMap<Player,Integer>();
        classMap      = new HashMap<Player,String>();
        rewardMap     = new HashMap<Player,List<ItemStack>>();
        kills         = new HashMap<Player,Integer>();
        repairList    = new LinkedList<int[]>();
        
        running       = false;
        edit          = false;
        
        allowMonsters = world.getAllowMonsters();
        allowAnimals  = world.getAllowAnimals();
        spawnMonsters = ((net.minecraft.server.World) ((CraftWorld) world).getHandle()).spawnMonsters;
    }
    
    public void startArena()
    {
        if (running)
            return;
        
        if (!softRestore && forceRestore && !serializeRegion())
            return;
        
        // Set the spawn flags to enable monster spawning.
        //MAUtils.setSpawnFlags(plugin, world, 1, true, true);
        MAUtils.setSpawnFlags(plugin, world, 1, allowMonsters, allowAnimals);
        
        // Teleport players.
        for (Player p : livePlayers)
        {
            p.teleport(arenaLoc);
            p.setHealth(20);
            rewardMap.put(p, new LinkedList<ItemStack>());
        }

        running = true;
        
        // Spawn pets.
        for (Map.Entry<Player,Integer> entry : petMap.entrySet())
        {
            // Remove the bones from the inventory.
            Player p = entry.getKey();
            p.getInventory().removeItem(new ItemStack(Material.BONE, entry.getValue()));
            
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
        
        // Start the spawnThread.
        spawnThread = new MASpawnThread(plugin, this);
        spawnTaskId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, spawnThread, waveDelay, waveInterval);
        
        readyPlayers.clear();
        MAUtils.tellAll(this, MAMessages.get(Msg.ARENA_START));
        
        // Notify listeners.
        for (MobArenaListener listener : plugin.getAM().listeners)
            listener.onArenaStart();
    }
    
    /**
     * End this arena's session.
     */
    public void endArena()
    {        
        running = false;
        
        // If the arena was actually ever started, cancel the spawnthread.
        if (spawnThread != null)
        {
            Bukkit.getServer().getScheduler().cancelTask(spawnThread.taskId);
            Bukkit.getServer().getScheduler().cancelTask(spawnTaskId);
        }
        
        if (!emptyInvJoin)
            for (Player p : deadPlayers)
                MAUtils.restoreInventory(p);
        
        // Clean up the arena floor and give rewards
        cleanup();
        giveRewards();
        
        // Clear all the sets and maps.
        livePlayers.clear();
        deadPlayers.clear();
        pets.clear();
        classMap.clear();
        rewardMap.clear();
        
        if (softRestore)
            for (int[] buffer : repairList)
                world.getBlockAt(buffer[0], buffer[1], buffer[2]).setTypeIdAndData(buffer[3], (byte) buffer[4], false);
        else if (forceRestore)
            deserializeRegion();
        
        // Set the spawn flags to restore monster spawning.
        MAUtils.setSpawnFlags(plugin, world, spawnMonsters, allowMonsters, allowAnimals);
        MAUtils.tellAll(this, MAMessages.get(Msg.ARENA_END));
        
        // Notify listeners.
        for (MobArenaListener listener : plugin.getAM().listeners)
            listener.onArenaEnd();
    }
    
    /**
     * Force an arena start by forcing all not-ready players to leave.
     * @precondition - The arena musn't be running, and readyPlayers must not be empty.
     */
    public void forceStart()
    {
        // Set operations.
        Set<Player> tmp = new HashSet<Player>();
        tmp.addAll(livePlayers);
        tmp.removeAll(readyPlayers);
        
        // Force leave.
        for (Player p : tmp)
        {
            plugin.getAM().arenaMap.remove(p);
            playerLeave(p);
        }
    }
    
    /**
     * Force an arena end by forcing all players to leave.
     * @precondition - livePlayers must not be empty.
     */
    public void forceEnd()
    {        
        // Force leave.
        for (Player p : getAllPlayers())
        {
            plugin.getAM().arenaMap.remove(p);
            playerLeave(p);
        }
    }
    
    /**
     * Warp the player to the arena lobby and add to the set of live players.
     */
    public void playerJoin(Player p, Location loc)
    {
        if (!locations.containsKey(p))
            locations.put(p,loc);
        
        if (livePlayers.isEmpty())
        {
            Chunk chunk = world.getChunkAt(lobbyLoc);
            if (!world.isChunkLoaded(chunk))
                world.loadChunk(chunk);
            else
                world.refreshChunk(chunk.getX(), chunk.getZ());
        }

        MAUtils.sitPets(p);
        livePlayers.add(p);
        p.teleport(lobbyLoc);
        
        // Notify listeners.
        for (MobArenaListener listener : plugin.getAM().listeners)
            listener.onPlayerJoin(p);
    }
    
    /**
     * Add the player to the set of ready players.
     * If every is ready, the arena starts.
     */
    public void playerReady(Player p)
    {
        readyPlayers.add(p);
        
        if (readyPlayers.equals(livePlayers))
            startArena();
    }
    
    /**
     * Remove the player from all the player sets, and clear his inventory if necessary.
     * If the set of live players becomes empty, end the arena.
     * If the set of ready players becomes equal to the set of live players, start the arena.
     */
    public void playerLeave(Player p)
    {
        boolean clear = false;
        
        Location old = locations.get(p);
        Chunk chunk = old.getWorld().getChunkAt(old);
        if (!old.getWorld().isChunkLoaded(chunk))
            old.getWorld().loadChunk(chunk);
        else
            old.getWorld().refreshChunk(chunk.getX(), chunk.getZ());
        
        p.teleport(old);
        locations.remove(p); // get, then remove, because of Teleport Event
        
        // Only clear the inventory if the player has class items.
        if (readyPlayers.remove(p)) clear = true; 
        if (livePlayers.remove(p))  clear = true;
        deadPlayers.remove(p);
        specPlayers.remove(p);
        removePets(p);
        
        if (clear)         MAUtils.clearInventory(p);
        if (!emptyInvJoin) MAUtils.restoreInventory(p);
        
        if (running && livePlayers.isEmpty())
            endArena();
        else if (!readyPlayers.isEmpty() && readyPlayers.equals(livePlayers))
            startArena();
        
        // Notify listeners.
        for (MobArenaListener listener : plugin.getAM().listeners)
            listener.onPlayerLeave(p);
    }
    
    public void playerDeath(final Player p)
    {
        p.teleport(arenaLoc); // This will sometimes force players to drop any items held (not confirmed)
        p.teleport(spectatorLoc);
        p.setFireTicks(0);
        p.setHealth(20);
        
        // Add to the list of dead players.
        livePlayers.remove(p);
        deadPlayers.add(p);
        removePets(p);

        // Has to be delayed for TombStone not to fuck shit up.
        if (running && livePlayers.isEmpty())
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin,
                    new Runnable()
                    {
                        public void run()
                        {
                            MAUtils.restoreInventory(p);
                            endArena();
                        }
                    }, 10);
        
        MAUtils.tellAll(this, MAMessages.get(Msg.PLAYER_DIED, p.getName()));
        
        // Notify listeners.
        for (MobArenaListener listener : plugin.getAM().listeners)
            listener.onPlayerDeath(p);
    }
    
    public void playerSpec(Player p, Location loc)
    {
        if (!locations.containsKey(p))
            locations.put(p,loc);
        
        MAUtils.sitPets(p);
        specPlayers.add(p);
        p.teleport(spectatorLoc);
    }
    
    public void playerKill(Player p, LivingEntity e)
    {
        kills.put(p, kills.get(p) + 1);
    }
    
    
    
    /*////////////////////////////////////////////////////////////////////
    //
    //      Items & Cleanup
    //
    ////////////////////////////////////////////////////////////////////*/
    
    public void assignClass(Player p, String className)
    {
        petMap.remove(p);
        classMap.put(p, className);
        MAUtils.clearInventory(p);
        MAUtils.giveItems(p, classItems.get(className), autoEquip);
        MAUtils.giveItems(p, classArmor.get(className), autoEquip);
        
        int pets = MAUtils.getPetAmount(p);
        if (pets > 0) petMap.put(p, pets);
    }
    
    private void giveRewards()
    {
        for (Map.Entry<Player,List<ItemStack>> entry : rewardMap.entrySet())
        {
            MAUtils.tellPlayer(entry.getKey(), MAMessages.get(Msg.REWARDS_GIVE));
            MAUtils.giveRewards(entry.getKey(), entry.getValue());
        }
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
            w.remove();
    }
    
    private void removePets(Player p)
    {
        for (Wolf w : pets)
            if (((Player) w.getOwner()).getName().equals(p.getName()))
                w.remove();
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
    //      EventListener methods
    //
    ////////////////////////////////////////////////////////////////////*/
    
    // Block Listener
    public void onBlockBreak(BlockBreakEvent event)
    {
        if (!inRegion(event.getBlock().getLocation()) || edit)
            return;
        
        Block b = event.getBlock();
        if (blocks.remove(b) || b.getType() == Material.TNT)
            return;
        
        if (softRestore && running)
        {
            int[] buffer = new int[5];
            buffer[0] = b.getX();
            buffer[1] = b.getY();
            buffer[2] = b.getZ();
            buffer[3] = b.getTypeId();
            buffer[4] = (int) b.getData();
            repairList.add(buffer);
            if (!softRestoreDrops) event.getBlock().setTypeId(0);
            return;
        }
        
        event.setCancelled(true);
    }
    
    public void onBlockPlace(BlockPlaceEvent event)
    {
        if (!inRegion(event.getBlock().getLocation()) || edit)
            return;
        
        Block b = event.getBlock();
        if (running && livePlayers.contains(event.getPlayer()))
        {
            blocks.add(b);
            Material mat = b.getType();
            
            if (mat == Material.WOODEN_DOOR || mat == Material.IRON_DOOR_BLOCK)
                blocks.add(b.getRelative(0,1,0));
            return;
        }

        // If the arena isn't running, or if the player isn't in the arena, cancel.
        event.setCancelled(true);
    }
    
    // Monster Listener
    public void onCreatureSpawn(CreatureSpawnEvent event)
    {
        if (!inRegion(event.getLocation()))
            return;
        
        // If running == true, setCancelled(false), and vice versa.
        event.setCancelled(!running);
    }
    
    public void onEntityExplode(EntityExplodeEvent event)
    {
        if (!monsters.contains(event.getEntity()) && !inRegionRadius(event.getLocation(), 10))
            return;
        
        event.setYield(0);
        monsters.remove(event.getEntity());
        
        // If the arena isn't running
        if (!running || repairDelay == 0)
        {
            event.setCancelled(true);
            return;
        }
        
        // If there is a sign in the blocklist, cancel
        for (Block b : event.blockList())
        {
            if (!(b.getType() == Material.SIGN_POST || b.getType() == Material.WALL_SIGN))
            {
                continue;
            }
            
            event.setCancelled(true);
            return;
        }

        // Uncancel, just in case.
        event.setCancelled(false);

        int[] buffer;
        final HashMap<Block,Integer> blockMap = new HashMap<Block,Integer>();
        for (Block b : event.blockList())
        {
            Material mat = b.getType();
            
            if (mat == Material.WOODEN_DOOR || mat == Material.IRON_DOOR_BLOCK || mat == Material.FIRE || mat == Material.CAKE_BLOCK || mat == Material.WATER || mat == Material.LAVA)
            {
                blocks.remove(b);
            }
            else if (blocks.remove(b))
            {
                world.dropItemNaturally(b.getLocation(), new ItemStack(b.getTypeId(), 1));
            }
            else if (softRestore)
            {
                buffer = new int[5];
                buffer[0] = b.getX();
                buffer[1] = b.getY();
                buffer[2] = b.getZ();
                buffer[3] = b.getTypeId();
                buffer[4] = (int) b.getData();
                repairList.add(buffer);
                blockMap.put(b, b.getTypeId() + (b.getData() * 1000));
            }
            else
            {
                blockMap.put(b, b.getTypeId() + (b.getData() * 1000));
            }
        }
        
        if (!protect || softRestore)
            return;
        
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin,
            new Runnable()
            {
                public void run()
                {
                    for (Map.Entry<Block,Integer> entry : blockMap.entrySet())
                    {
                        Block b = entry.getKey();
                        int type = entry.getValue();
                        
                        b.getLocation().getBlock().setTypeId(type % 1000);
                        
                        if (type > 1000)
                            b.getLocation().getBlock().setData((byte) (type / 1000));
                    }
                }
            }, repairDelay);
    }
    
    public void onEntityCombust(EntityCombustEvent event)
    {
        if (monsters.contains(event.getEntity()))
            event.setCancelled(true);
    }
    
    public void onEntityTarget(EntityTargetEvent event)
    {
        if (!running) return;
        
        if (pets.contains(event.getEntity()))
        {
            if (event.getReason() != TargetReason.TARGET_ATTACKED_OWNER && event.getReason() != TargetReason.OWNER_ATTACKED_TARGET)
                return;
            
            if (!(event.getTarget() instanceof Player))
                return;
            
            // If the target is a player, cancel.
            event.setCancelled(true);
            return;
        }
        
        if (monsters.contains(event.getEntity()))
        {
            if (event.getReason() == TargetReason.FORGOT_TARGET)
            {
                event.setTarget(MAUtils.getClosestPlayer(event.getEntity(), this));
                return;
            }
                
            if (event.getReason() == TargetReason.TARGET_DIED)
            {
                event.setTarget(MAUtils.getClosestPlayer(event.getEntity(), this));
                return;
            }
            
            if (event.getReason() == TargetReason.CLOSEST_PLAYER)
                if (!livePlayers.contains(event.getTarget()))
                    event.setCancelled(true);
            return;
        }
    }
    
    // Death Listener
    public void onEntityRegainHealth(EntityRegainHealthEvent event)
    {
        if (!running) return;
        
        if (!(event.getEntity() instanceof Player) || !livePlayers.contains((Player)event.getEntity()))
            return;
        
        if (event.getRegainReason() == RegainReason.REGEN)
            event.setCancelled(true);
    }
    
    public void onEntityDeath(EntityDeathEvent event)
    {        
        if (event.getEntity() instanceof Player)
        {
            Player p = (Player) event.getEntity();
            
            if (!livePlayers.contains(p))
                return;
            
            event.getDrops().clear();
            playerDeath(p);
            p.getInventory().clear(); // For TombStone
            return;
        }
        
        if (monsters.remove(event.getEntity()))
        {
            event.getDrops().clear();
            resetIdleTimer();
            return;
        }
    }
    
    public void onEntityDamage(EntityDamageEvent event)
    {
        if (!running) return;
        
        EntityDamageByEntityEvent e = (event instanceof EntityDamageByEntityEvent) ? (EntityDamageByEntityEvent) event : null;
        Entity damager = (e != null) ? e.getDamager() : null;
        Entity damagee = event.getEntity();
        
        // Damagee - Pet Wolf - cancel all damage.
        if (damagee instanceof Wolf && pets.contains(damagee))
        {
            if (event.getCause() == DamageCause.FIRE_TICK)
            {
                damagee.setFireTicks(32768); // For mcMMO
                event.setCancelled(true);
            }
            if (e != null && damager instanceof Player)
                event.setCancelled(true);
            
            event.setDamage(0);
            return;
        }

        // Damager - Pet Wolf - lower damage
        if (e != null && damager instanceof Wolf && pets.contains(damager))
        {
            event.setDamage(1);
            return;
        }
        
        // Damagee & Damager - Player - cancel if pvp disabled
        if (damagee instanceof Player && damager instanceof Player)
        {
            if (livePlayers.contains(damagee) && !pvp)
                event.setCancelled(true);
            
            return;
        }
        
        // Damagee & Damager - Monsters - cancel if no monsterInfight
        if (e != null && monsters.contains(damagee) && monsters.contains(damager))
        {
            if (!monsterInfight)
                event.setCancelled(true);
            
            return;
        }
        
        // Creeper detonations
        if (inRegion(damagee.getLocation()))
        {
            if (!detDamage || !(damagee instanceof Player) || !livePlayers.contains((Player) damagee))
                return;
            
            if (event.getCause() == DamageCause.BLOCK_EXPLOSION)
                event.setCancelled(true);
            
            return;
        }
    }
    
    // Lobby Listener
    public void onPlayerDropItem(PlayerDropItemEvent event)
    {
        if (running) return;
        
        Player p = event.getPlayer();
        if (!livePlayers.contains(p))
            return;
        
        MAUtils.tellPlayer(p, MAMessages.get(Msg.LOBBY_DROP_ITEM));
        event.setCancelled(true);
    }

    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event)
    {
        if (!readyPlayers.contains(event.getPlayer()) && !livePlayers.contains(event.getPlayer()))
            return;
        
        if (!running)
        {
            event.getBlockClicked().getFace(event.getBlockFace()).setTypeId(0);
            event.setCancelled(true);
            return;
        }

        Block liquid = event.getBlockClicked().getFace(event.getBlockFace());
        blocks.add(liquid);
    }

    public void onPlayerInteract(PlayerInteractEvent event)
    {
        /*if (running && livePlayers.contains(event.getPlayer()))
        {
            Player p = event.getPlayer();
            Action a = event.getAction();
            if (a == Action.RIGHT_CLICK_AIR || a == Action.RIGHT_CLICK_BLOCK)
            {
                if (p.getItemInHand().getType() == Material.BOW)
                    p.shootArrow();
                event.setUseItemInHand(Result.DENY);
                event.setCancelled(true);
            }
            return;
        }*/
        
        if (running || !livePlayers.contains(event.getPlayer()))
            return;
        
        Player p = event.getPlayer();
        Action a = event.getAction();
        if ((a == Action.RIGHT_CLICK_AIR) || (a == Action.RIGHT_CLICK_BLOCK))
        {
            event.setUseItemInHand(Result.DENY);
            event.setCancelled(true);
        }
        
        // Iron block
        if (event.hasBlock() && event.getClickedBlock().getTypeId() == 42)
        {
            if (classMap.containsKey(p))
            {
                MAUtils.tellPlayer(p, MAMessages.get(Msg.LOBBY_PLAYER_READY));
                playerReady(p);
            }
            else
            {
                MAUtils.tellPlayer(p, MAMessages.get(Msg.LOBBY_PICK_CLASS));
            }
            return;
        }
        
        // Sign
        if (event.hasBlock() && event.getClickedBlock().getState() instanceof Sign)
        {
            if (a == Action.RIGHT_CLICK_BLOCK)
            {
                MAUtils.tellPlayer(p, MAMessages.get(Msg.LOBBY_RIGHT_CLICK));
                return;
            }
            
            // Cast the block to a sign to get the text on it.
            Sign sign = (Sign) event.getClickedBlock().getState();
            
            // Check if the first line of the sign is a class name.
            String className = sign.getLine(0);
            if (!classes.contains(className))
                return;
            
            if (!MobArena.hasDefTrue(p, "mobarena.classes." + className))
            {
                MAUtils.tellPlayer(p, MAMessages.get(Msg.LOBBY_CLASS_PERMISSION));
                return;
            }

            // Set the player's class.
            assignClass(p, className);
            MAUtils.tellPlayer(p, MAMessages.get(Msg.LOBBY_CLASS_PICKED, className));
            return;
        }
    }
    
    // Disconnect Listener
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        Player p = event.getPlayer();
        if (!enabled || !livePlayers.contains(p))
            return;
        
        //MAUtils.clearInventory(p);
        plugin.getAM().arenaMap.remove(p);
        playerLeave(p);
    }
    
    public void onPlayerKick(PlayerKickEvent event)
    {
        Player p = event.getPlayer();
        if (!enabled || !livePlayers.contains(p))
            return;
        
        //MAUtils.clearInventory(p);
        plugin.getAM().arenaMap.remove(p);
        playerLeave(p);
    }

    // Teleport Listener
    public void onPlayerTeleport(PlayerTeleportEvent event)
    {
        if (edit || !enabled || !setup || allowWarp)
            return;
        
        if (!inRegion(event.getTo()) && !inRegion(event.getFrom()))
            return;

        Player   p    = event.getPlayer();
        Location old  = locations.get(p);
        Location to   = event.getTo();
        Location from = event.getFrom();
        
        if (livePlayers.contains(p))
        {
            if (inRegion(from))
            {
                if (to.equals(arenaLoc) || to.equals(lobbyLoc) || to.equals(spectatorLoc) || to.equals(old))
                    return;
                
                MAUtils.tellPlayer(p, MAMessages.get(Msg.WARP_FROM_ARENA));
                event.setCancelled(true);
                return;
            }
            
            if (inRegion(to))
            {
                if (to.equals(arenaLoc) || to.equals(lobbyLoc) || to.equals(spectatorLoc) || to.equals(old))
                    return;
                
                MAUtils.tellPlayer(p, MAMessages.get(Msg.WARP_TO_ARENA));
                event.setCancelled(true);
                return;
            }
            
            return;
        }
        
        if (running && inRegion(to))
        {
            MAUtils.tellPlayer(p, MAMessages.get(Msg.WARP_TO_ARENA));
            event.setCancelled(true);
            return;
        }
    }

    // Command Listener
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
    {
        Player p = event.getPlayer();
        
        if (!livePlayers.contains(p))
            return;
        
        String[] args = event.getMessage().split(" ");
        
        if ((args.length > 1 && MACommands.COMMANDS.contains(args[1].trim())) ||
            MACommands.ALLOWED_COMMANDS.contains(event.getMessage().substring(1).trim()) ||
            MACommands.ALLOWED_COMMANDS.contains(args[0]))
            return;
        
        event.setCancelled(true);
        MAUtils.tellPlayer(p, MAMessages.get(Msg.MISC_COMMAND_NOT_ALLOWED));
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
        result.addAll(livePlayers);
        result.addAll(deadPlayers);
        result.addAll(specPlayers);
        return result;
    }
    
    public List<Player> getLivingPlayers()
    {
        List<Player> result = new LinkedList<Player>();
        result.addAll(livePlayers);
        return result;
    }
    
    public List<Player> getNonreadyPlayers()
    {
        List<Player> result = new LinkedList<Player>();
        result.addAll(livePlayers);
        result.removeAll(readyPlayers);
        return result;
    }
    
    public List<Player> getDeadPlayers()
    {
        List<Player> result = new LinkedList<Player>();
        result.addAll(deadPlayers);
        return result;
    }
    
    public void resetIdleTimer()
    {
        if (maxIdleTime <= 0)
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
                    {
                        out(e.getClass().getSimpleName());
                        if (e.isDead())
                        {
                            out("Removing monster");
                            monsters.remove(e);
                        }
                    }
                    
                    // Compare the current size with the previous size.
                    if (monsters.size() < spawnThread.previousSize || spawnThread.previousSize == 0)
                    {
                        resetIdleTimer();
                        return;
                    }
                    
                    // Clear all player inventories, and "kill" all players.
                    for (Player p : livePlayers)
                    {
                        MAUtils.clearInventory(p);
                        MAUtils.tellPlayer(p, MAMessages.get(Msg.FORCE_END_IDLE));
                        playerDeath(p);
                    }
                }
            }, maxIdleTime);
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
    
    private void out(String s)
    {
        System.out.println(s);
    }
}
