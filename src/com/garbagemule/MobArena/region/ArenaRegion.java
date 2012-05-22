package com.garbagemule.MobArena.region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.garbagemule.MobArena.Messenger;
import com.garbagemule.MobArena.MobArena;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.util.config.ConfigSection;

public class ArenaRegion
{
    private Arena arena;
    private World world;
    
    private Location p1, p2, l1, l2, arenaWarp, lobbyWarp, specWarp, leaderboard;
    private Map<String,Location> spawnpoints, containers;
    
    private boolean setup, lobbySetup;
    
    private ConfigSection coords;
    private ConfigSection spawns;
    private ConfigSection chests;
    
    public ArenaRegion(ConfigSection coords, Arena arena) {
        this.arena  = arena;
        this.world  = arena.getWorld();
        
        this.coords = coords;
        this.spawns = coords.getConfigSection("spawnpoints");
        this.chests = coords.getConfigSection("containers");
        
        reloadAll();
        adjustRegion();
    }
    
    public void reloadAll() {
        reloadRegion();
        reloadWarps();
        reloadLeaderboards();
        reloadSpawnpoints();
        reloadChests();
        
        verifyData();
    }
    
    public void reloadRegion() {
        p1 = coords.getLocation("p1", world);
        p2 = coords.getLocation("p2", world);
        
        l1 = coords.getLocation("l1", world);
        l2 = coords.getLocation("l2", world);
    }
    
    public void reloadWarps() {
        arenaWarp = coords.getLocation("arena", world);
        lobbyWarp = coords.getLocation("lobby", world);
        specWarp  = coords.getLocation("spectator", world);

        leaderboard = coords.getLocation("leaderboard", world);
    }
    
    public void reloadLeaderboards() {
        leaderboard = coords.getLocation("leaderboard", world);
    }
    
    public void reloadSpawnpoints() {
        spawnpoints = new HashMap<String,Location>();
        Set<String> keys = spawns.getKeys();
        if (keys != null) {
            for (String spwn : keys) {
                spawnpoints.put(spwn, spawns.getLocation(spwn, world));
            }
        }
    }
    
    public void reloadChests() {
        containers = new HashMap<String,Location>();
        Set<String> keys = chests.getKeys();
        if (keys != null) {
            for (String chst : keys) {
                containers.put(chst, chests.getLocation(chst, world));
            }
        }
    }
    
    public void verifyData() {
        setup = (p1 != null &&
                 p2 != null &&
                 arenaWarp != null &&
                 lobbyWarp != null &&
                 specWarp != null &&
                 !spawnpoints.isEmpty());
        
        lobbySetup = (l1 != null &&
                      l2 != null);
    }
    
    public void checkData(MobArena plugin, CommandSender s) {
        if (arenaWarp == null)
            Messenger.tellPlayer(s, "Missing warp: arena");
        if (lobbyWarp == null)
            Messenger.tellPlayer(s, "Missing warp: lobby");
        if (specWarp == null)
            Messenger.tellPlayer(s, "Missing warp: spectator");
        if (p1 == null)
            Messenger.tellPlayer(s, "Missing region point: p1");
        if (p2 == null)
            Messenger.tellPlayer(s, "Missing region point: p2");
        if (spawnpoints.isEmpty())
            Messenger.tellPlayer(s, "Missing spawnpoints");
        if (setup)
            Messenger.tellPlayer(s, "Arena is ready to be used!");
    }
    
    public boolean isDefined() {
        return (p1 != null && p2 != null);
    }
    
    public boolean isLobbyDefined() {
        return (l1 != null && l2 != null);
    }
    
    public boolean isSetup() {
        return setup;
    }
    
    public boolean isLobbySetup() {
        return lobbySetup;
    }
    
    public boolean isWarp(Location l) {
        return (l.equals(arenaWarp) ||
                l.equals(lobbyWarp) ||
                l.equals(specWarp));
    }
    
    public boolean contains(Location l) {
        if (!l.getWorld().getName().equals(world.getName()) || !setup) {
            return false;
        }
        
        int x = l.getBlockX();
        int y = l.getBlockY();
        int z = l.getBlockZ();
        
        // Check the lobby first.
        if (lobbySetup) {
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
    
    public boolean contains(Location l, int radius) {
        if (!l.getWorld().getName().equals(world.getName()) || !setup) {
            return false;
        }
        
        int x = l.getBlockX();
        int y = l.getBlockY();
        int z = l.getBlockZ();
        
        if (lobbySetup) {
            if ((x + radius >= l1.getBlockX() && x - radius <= l2.getBlockX()) &&            
                (z + radius >= l1.getBlockZ() && z - radius <= l2.getBlockZ()) &&           
                (y + radius >= l1.getBlockY() && y - radius <= l2.getBlockY()))
                return true;
        }
        
        return ((x + radius >= p1.getBlockX() && x - radius <= p2.getBlockX()) &&
                (z + radius >= p1.getBlockZ() && z - radius <= p2.getBlockZ()) &&
                (y + radius >= p1.getBlockY() && y - radius <= p2.getBlockY()));
    }
    
    // Region expand
    public void expandUp(int amount) {
        p2.setY(Math.min(arena.getWorld().getMaxHeight(), p2.getY() + amount));
        set(RegionPoint.P2, p2);
    }
    
    public void expandDown(int amount) {
        p1.setY(Math.max(0D, p1.getY() - amount));
        set(RegionPoint.P1, p1);
    }
    
    public void expandP1(int x, int z) {
        p1.setX(p1.getX() - x);
        p1.setZ(p1.getZ() - z);
        set(RegionPoint.P1, p1);
    }
    
    public void expandP2(int x, int z) {
        p2.setX(p2.getX() + x);
        p2.setZ(p2.getZ() + z);
        set(RegionPoint.P2, p2);
    }
    
    public void expandOut(int amount) {
        expandP1(amount, amount);
        expandP2(amount, amount);
    }
    
    // Lobby expand
    public void expandLobbyUp(int amount) {
        l2.setY(Math.min(arena.getWorld().getMaxHeight(), l2.getY() + amount));
        set(RegionPoint.L2, l2);
    }
    
    public void expandLobbyDown(int amount) {
        l1.setY(Math.max(0D, l1.getY() + amount));
        set(RegionPoint.L1, l1);
    }
    
    public void expandL1(int x, int z) {
        l1.setX(l1.getX() - x);
        l1.setZ(l1.getZ() - z);
        set(RegionPoint.L1, l1);
    }
    
    public void expandL2(int x, int z) {
        l2.setX(l2.getX() + x);
        l2.setZ(l2.getZ() + z);
        set(RegionPoint.L2, l2);
    }
    
    public void expandLobbyOut(int amount) {
        expandL1(amount, amount);
        expandL2(amount, amount);
    }
    
    public void fixRegion() {
        fix("p1", "p2");
    }
    
    public void fixLobbyRegion() {
        fix("l1", "l2");
    }
    
    private void fix(String location1, String location2) {
        Location loc1 = coords.getLocation(location1, world);
        Location loc2 = coords.getLocation(location2, world);
        
        if (loc1 == null || loc2 == null) {
            return;
        }

        if (loc1.getX() > loc2.getX()) {
            double tmp = loc1.getX();
            loc1.setX(loc2.getX());
            loc2.setX(tmp);
        }
        
        if (loc1.getZ() > loc2.getZ()) {
            double tmp = loc1.getZ();
            loc1.setZ(loc2.getZ());
            loc2.setZ(tmp);
        }
        
        if (loc1.getY() > loc2.getY()) {
            double tmp = loc1.getY();
            loc1.setY(loc2.getY());
            loc2.setY(tmp);
        }
        
        if (!arena.getWorld().getName().equals(world.getName()))
            arena.setWorld(world);

        coords.set(location1, loc1);
        coords.set(location2, loc2);
    }
    
    private void adjustRegion() {
        if (!setup) {
            return;
        }

        // Make sure the arena warp is inside the region.
        readjustRegion(arenaWarp);
        
        // Re-adjust for all spawnpoints and containers.
        for (Location spawnpoint : spawnpoints.values()) {
            readjustRegion(spawnpoint);
        }
        
        for (Location chest : containers.values()) {
            readjustRegion(chest);
        }
    }
    
    private void readjustRegion(Location l) {
        if (p1 == null || p2 == null) {
            return;
        }
        
        int x = l.getBlockX();
        int y = l.getBlockY();
        int z = l.getBlockZ();
        
        int p1x = p1.getBlockX();
        int p1y = p1.getBlockY();
        int p1z = p1.getBlockZ();
        
        int p2x = p2.getBlockX();
        int p2y = p2.getBlockY();
        int p2z = p2.getBlockZ();
        
        if (x <= p1x) {
            expandP1(p1x - x + 2, 0);
        }
        else if (x >= p2x) {
            expandP2(x - p2x + 2, 0);
        }
        
        if (y <= p1y) {
            expandDown((int) (p1y - y + 2));
        }
        else if (y >= p2y) {
            expandUp((int) (y - p2y + 2));
        }
        
        if (z <= p1z) {
            expandP1(0, p1z - z + 2);
        }
        else if (z >= p2z) {
            expandP2(0, z - p2z + 2);
        }
    }
    
    public List<Chunk> getChunks() {
        List<Chunk> result = new ArrayList<Chunk>();
        
        if (p1 == null || p2 == null) {
            return result;
        }
        
        Chunk c1 = world.getChunkAt(p1);
        Chunk c2 = world.getChunkAt(p2);
        
        for (int i = c1.getX(); i <= c2.getX(); i++) {
            for (int j = c1.getZ(); j <= c2.getZ(); j++) {
                result.add(world.getChunkAt(i,j));
            }
        }
        
        return result;
    }
    
    public Location getArenaWarp() {
        return arenaWarp;
    }
    
    public Location getLobbyWarp() {
        return lobbyWarp;
    }
    
    public Location getSpecWarp() {
        return specWarp;
    }
    
    public Location getSpawnpoint(String name) {
        return spawnpoints.get(name);
    }
    
    public Collection<Location> getSpawnpoints() {
        return spawnpoints.values();
    }
    
    public List<Location> getSpawnpointList() {
        return new ArrayList<Location>(spawnpoints.values());
    }
    
    public Collection<Location> getContainers() {
        return containers.values();
    }
    
    public Location getLeaderboard() {
        return leaderboard;
    }
    
    public void set(String point, Location loc) {
        coords.set(point, loc);
        
        // Adjust the region to accomodate any bounding box breaking.
        if (point.equals("arena") || point.equals("lobby") || point.equals("spectator")) {
            readjustRegion(loc);
        }
        
        fixRegion();
        fixLobbyRegion();
        reloadRegion();
        reloadWarps();
        reloadLeaderboards();
        verifyData();
        save();
    }
    
    public void addSpawn(String name, Location loc) {
        spawns.set(name, loc);
        readjustRegion(loc);
        reloadSpawnpoints();
        verifyData();
        save();
    }
    
    public boolean removeSpawn(String name) {
        if (spawns.getString(name) == null) {
            return false;
        }
        
        spawns.set(name, null);
        reloadSpawnpoints();
        verifyData();
        save();
        return true;
    }
    
    public void addChest(String name, Location loc) {
        chests.set(name, loc);
        reloadChests();
        save();
    }
    
    public boolean removeChest(String name) {
        if (chests.getString(name) == null) {
            return false;
        }
        
        chests.set(name, null);
        reloadChests();
        save();
        return true;
    }
    
    public void save() {
        spawns.getParent().save();
    }
    
    public void showRegion(final Player p) {
        if (!isDefined()) {
            return;
        }
        
        // Grab all the blocks, and send block change events.
        final Map<Location,BlockState> blocks = new HashMap<Location,BlockState>();
        for (Location l : getFramePoints()) {
            Block b = l.getBlock();
            blocks.put(l, b.getState());
            p.sendBlockChange(l, 35, (byte) 14);
        }
        
        arena.scheduleTask(new Runnable() {
                public void run() {
                    // If the player isn't online, just forget it.
                    if (!p.isOnline()) {
                        return;
                    }
                    
                    // Send block "restore" events.
                    for (Map.Entry<Location,BlockState> entry : blocks.entrySet()) {
                        Location l   = entry.getKey();
                        BlockState b = entry.getValue();
                        int id       = b.getTypeId();
                        byte data    = b.getRawData();
                        
                        p.sendBlockChange(l, id, data);
                    }
                }
            }, 100);
    }
    
    private List<Location> getFramePoints() {
        List<Location> result = new ArrayList<Location>();
        int x1 = p1.getBlockX(); int y1 = p1.getBlockY(); int z1 = p1.getBlockZ();
        int x2 = p2.getBlockX(); int y2 = p2.getBlockY(); int z2 = p2.getBlockZ();
        
        for (int i = x1; i <= x2; i++) {
            result.add(world.getBlockAt(i, y1, z1).getLocation());
            result.add(world.getBlockAt(i, y1, z2).getLocation());
            result.add(world.getBlockAt(i, y2, z1).getLocation());
            result.add(world.getBlockAt(i, y2, z2).getLocation());
        }
        
        for (int j = y1; j <= y2; j++) {
            result.add(world.getBlockAt(x1, j, z1).getLocation());
            result.add(world.getBlockAt(x1, j, z2).getLocation());
            result.add(world.getBlockAt(x2, j, z1).getLocation());
            result.add(world.getBlockAt(x2, j, z2).getLocation());
        }
        
        for (int k = z1; k <= z2; k++) {
            result.add(world.getBlockAt(x1, y1, k).getLocation());
            result.add(world.getBlockAt(x1, y2, k).getLocation());
            result.add(world.getBlockAt(x2, y1, k).getLocation());
            result.add(world.getBlockAt(x2, y2, k).getLocation());
        }
        return result;
    }
}
