package com.garbagemule.MobArena.region;

import static com.garbagemule.MobArena.util.config.ConfigUtils.makeSection;
import static com.garbagemule.MobArena.util.config.ConfigUtils.parseLocation;
import static com.garbagemule.MobArena.util.config.ConfigUtils.setLocation;

import com.garbagemule.MobArena.ConfigError;
import com.garbagemule.MobArena.MAUtils;
import com.garbagemule.MobArena.MobArena;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.util.Enums;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ArenaRegion
{
    private Arena arena;
    private World world;

    private Location lastP1, lastP2, lastL1, lastL2;
    private Location p1, p2, l1, l2, arenaWarp, lobbyWarp, specWarp, exitWarp, leaderboard;
    private Map<String,Location> spawnpoints, containers;

    private boolean setup, lobbySetup;

    private ConfigurationSection coords;
    private ConfigurationSection spawns;
    private ConfigurationSection chests;

    public ArenaRegion(ConfigurationSection section, Arena arena) {
        this.arena  = arena;
        refreshWorld();

        this.coords = makeSection(section, "coords");
        this.spawns = makeSection(coords,  "spawnpoints");
        this.chests = makeSection(coords,  "containers");

        reloadAll();
    }

    public void refreshWorld() {
        this.world = arena.getWorld();
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
        p1 = parseLocation(coords, "p1", world);
        p2 = parseLocation(coords, "p2", world);
        //fixRegion();

        l1 = parseLocation(coords, "l1", world);
        l2 = parseLocation(coords, "l2", world);
        //fixLobbyRegion();
    }

    public void reloadWarps() {
        arenaWarp = parseLocation(coords, "arena", world);
        lobbyWarp = parseLocation(coords, "lobby", world);
        specWarp  = parseLocation(coords, "spectator", world);

        try {
            exitWarp = parseLocation(coords, "exit", null);
        } catch (IllegalArgumentException e) {
            throw new ConfigError("Failed to parse exit warp for arena " + arena.configName() + " because: " + e.getMessage());
        }
    }

    public void reloadLeaderboards() {
        // try-catch for backwards compatibility
        try {
            leaderboard = parseLocation(coords, "leaderboard", null);
        } catch (IllegalArgumentException e) {
            throw new ConfigError("Failed to parse leaderboard location for arena " + arena.configName() + " because: " + e.getMessage());
        }
        if (leaderboard != null && leaderboard.getWorld() == null) {
            leaderboard.setWorld(world);
        }
    }

    public void reloadSpawnpoints() {
        spawnpoints = new HashMap<>();
        Set<String> keys = spawns.getKeys(false);
        if (keys != null) {
            for (String spwn : keys) {
                spawnpoints.put(spwn, parseLocation(spawns, spwn, world));
            }
        }
    }

    public void reloadChests() {
        containers = new HashMap<>();
        Set<String> keys = chests.getKeys(false);
        if (keys != null) {
            for (String chst : keys) {
                containers.put(chst, parseLocation(chests, chst, world));
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

    public void checkData(MobArena plugin, CommandSender s, boolean ready, boolean region, boolean warps, boolean spawns) {
        // Verify data first
        verifyData();

        // Prepare the list
        List<String> list = new ArrayList<>();

        // Region points
        if (region) {
            if (p1 == null) list.add("p1");
            if (p2 == null) list.add("p2");
            if (!list.isEmpty()) {
                arena.getGlobalMessenger().tell(s, "Missing region points: " + MAUtils.listToString(list, plugin));
                list.clear();
            }
        }

        // Warps
        if (warps) {
            if (arenaWarp == null) list.add("arena");
            if (lobbyWarp == null) list.add("lobby");
            if (specWarp  == null) list.add("spectator");
            if (!list.isEmpty()) {
                arena.getGlobalMessenger().tell(s, "Missing warps: " + MAUtils.listToString(list, plugin));
                list.clear();
            }
        }

        // Spawnpoints
        if (spawns) {
            if (spawnpoints.isEmpty()) {
                arena.getGlobalMessenger().tell(s, "Missing spawnpoints");
            }
        }

        // Ready?
        if (ready && setup) {
            arena.getGlobalMessenger().tell(s, "Arena is ready to be used!");
        }
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
                l.equals(specWarp)  ||
                l.equals(exitWarp));
    }

    public boolean contains(Location l) {
        if (!l.getWorld().getName().equals(world.getName()) || !isDefined()) {
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
        if (!l.getWorld().getName().equals(world.getName()) || !isDefined()) {
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

    public boolean intersects(ArenaRegion other) {
        return intersects(p1, p2, other.p1, other.p2)
            || intersects(p1, p2, other.l1, other.l2)
            || intersects(l1, l2, other.p1, other.p2)
            || intersects(l1, l2, other.l1, other.l2);
    }

    private boolean intersects(Location a1, Location a2, Location b1, Location b2) {
        return (a1 != null && a2 != null && b1 != null && b2 != null)
            && b1.getBlockX() <= a2.getBlockX() && a1.getBlockX() <= b2.getBlockX()
            && b1.getBlockZ() <= a2.getBlockZ() && a1.getBlockZ() <= b2.getBlockZ()
            && b1.getBlockY() <= a2.getBlockY() && a1.getBlockY() <= b2.getBlockY();
    }

    // Region expand
    public void expandUp(int amount) {
        int x = p2.getBlockX();
        int y = Math.min(p2.getWorld().getMaxHeight(), p2.getBlockY() + amount);
        int z = p2.getBlockZ();
        setSaveReload(coords, "p2", p2.getWorld(), x ,y ,z);
    }

    public void expandDown(int amount) {
        int x = p1.getBlockX();
        int y = Math.max(0, p1.getBlockY() - amount);
        int z = p1.getBlockZ();
        setSaveReload(coords, "p1", p1.getWorld(), x ,y ,z);
    }

    public void expandP1(int dx, int dz) {
        int x = p1.getBlockX() - dx;
        int y = p1.getBlockY();
        int z = p1.getBlockZ() - dz;
        setSaveReload(coords, "p1", p1.getWorld(), x ,y ,z);
    }

    public void expandP2(int dx, int dz) {
        int x = p2.getBlockX() + dx;
        int y = p2.getBlockY();
        int z = p2.getBlockZ() + dz;
        setSaveReload(coords, "p2", p2.getWorld(), x ,y ,z);
    }

    public void expandOut(int amount) {
        expandP1(amount, amount);
        expandP2(amount, amount);
    }

    // Lobby expand
    public void expandLobbyUp(int amount) {
        int x = l2.getBlockX();
        int y = Math.min(l2.getWorld().getMaxHeight(), l2.getBlockY() + amount);
        int z = l2.getBlockZ();
        setSaveReload(coords, "l2", l2.getWorld(), x ,y ,z);
    }

    public void expandLobbyDown(int amount) {
        int x = l1.getBlockX();
        int y = Math.max(0, l1.getBlockY() - amount);
        int z = l1.getBlockZ();
        setSaveReload(coords, "l1", l1.getWorld(), x ,y ,z);
    }

    public void expandL1(int dx, int dz) {
        int x = l1.getBlockX() - dx;
        int y = l1.getBlockY();
        int z = l1.getBlockZ() - dz;
        setSaveReload(coords, "l1", l1.getWorld(), x ,y ,z);
    }

    public void expandL2(int dx, int dz) {
        int x = l2.getBlockX() + dx;
        int y = l2.getBlockY();
        int z = l2.getBlockZ() + dz;
        setSaveReload(coords, "l2", l2.getWorld(), x ,y ,z);
    }

    public void expandLobbyOut(int amount) {
        expandL1(amount, amount);
        expandL2(amount, amount);
    }

    private void setSaveReload(ConfigurationSection section, String key, World w, double x, double y, double z) {
        Location loc = new Location(w, x, y, z);
        setLocation(section, key, loc);
        save();
        reloadRegion();
    }

    public void fixRegion() {
        fix("p1", "p2");
    }

    public void fixLobbyRegion() {
        fix("l1", "l2");
    }

    private void fix(String location1, String location2) {
        Location loc1 = parseLocation(coords, location1, world);
        Location loc2 = parseLocation(coords, location2, world);

        if (loc1 == null || loc2 == null) {
            return;
        }

        boolean modified = false;

        if (loc1.getX() > loc2.getX()) {
            double tmp = loc1.getX();
            loc1.setX(loc2.getX());
            loc2.setX(tmp);
            modified = true;
        }

        if (loc1.getZ() > loc2.getZ()) {
            double tmp = loc1.getZ();
            loc1.setZ(loc2.getZ());
            loc2.setZ(tmp);
            modified = true;
        }

        if (loc1.getY() > loc2.getY()) {
            double tmp = loc1.getY();
            loc1.setY(loc2.getY());
            loc2.setY(tmp);
            modified = true;
        }

        if (!arena.getWorld().getName().equals(world.getName())) {
            arena.setWorld(world);
            modified = true;
        }

        if (!modified) {
            return;
        }

        setLocation(coords, location1, loc1);
        setLocation(coords, location2, loc2);
        save();
    }

    public List<Chunk> getChunks() {
        List<Chunk> result = new ArrayList<>();

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

    public Location getExitWarp() {
        return exitWarp;
    }

    public Location getSpawnpoint(String name) {
        return spawnpoints.get(name);
    }

    public Collection<Location> getSpawnpoints() {
        return spawnpoints.values();
    }

    public List<Location> getSpawnpointList() {
        return new ArrayList<>(spawnpoints.values());
    }

    public Collection<Location> getContainers() {
        return containers.values();
    }

    public Location getLeaderboard() {
        return leaderboard;
    }

    public void set(RegionPoint point, Location loc) {
        // Act based on the point
        switch (point) {
            case P1:
            case P2:
            case L1:
            case L2: setPoint(point, loc); return;
            case ARENA:
            case LOBBY:
            case EXIT:
            case SPECTATOR:   setWarp(point, loc); return;
            case LEADERBOARD: setLeaderboard(loc); return;
        }

        throw new IllegalArgumentException("Invalid region point!");
    }

    private void setPoint(RegionPoint point, Location l) {
        // Lower and upper locations
        RegionPoint r1, r2;
        Location lower, upper;

        /* Initialize the bounds.
         *
         * To allow users to set a region point without paying attention to
         * the 'fixed' points, we continuously store the previously stored
         * location for the given point. These location references are only
         * ever overwritten when using the set commands, and remain fully
         * decoupled from the 'fixed' points.
         *
         * Effectively, the config-file and region store 'fixed' locations
         * that allow fast membership tests, but the region also stores the
         * 'unfixed' locations for a more intuitive setup process.
         */
        switch (point) {
            case P1:
                lastP1 = l.clone();
                lower = lastP1.clone();
                upper = (lastP2 != null ? lastP2.clone() : p2);
                r1 = RegionPoint.P1; r2 = RegionPoint.P2;
                break;
            case P2:
                lastP2 = l.clone();
                lower = (lastP1 != null ? lastP1.clone() : p1);
                upper = lastP2.clone();
                r1 = RegionPoint.P1; r2 = RegionPoint.P2;
                break;
            case L1:
                lastL1 = l.clone();
                lower = lastL1.clone();
                upper = (lastL2 != null ? lastL2.clone() : l2);
                r1 = RegionPoint.L1; r2 = RegionPoint.L2;
                break;
            case L2:
                lastL2 = l.clone();
                lower = (lastL1 != null ? lastL1.clone() : l1);
                upper = lastL2.clone();
                r1 = RegionPoint.L1; r2 = RegionPoint.L2;
                break;
            default:
                lower = upper = null;
                r1    = r2    = null;
        }

        // Min-max if both locations are non-null
        if (lower != null && upper != null) {
            double tmp;
            if (lower.getX() > upper.getX()) {
                tmp = lower.getX();
                lower.setX(upper.getX());
                upper.setX(tmp);
            }
            if (lower.getY() > upper.getY()) {
                tmp = lower.getY();
                lower.setY(upper.getY());
                upper.setY(tmp);
            }
            if (lower.getZ() > upper.getZ()) {
                tmp = lower.getZ();
                lower.setZ(upper.getZ());
                upper.setZ(tmp);
            }
        }

        // Set the coords and save
        if (lower != null) setLocation(coords, r1.name().toLowerCase(), lower);
        if (upper != null) setLocation(coords, r2.name().toLowerCase(), upper);
        save();

        // Reload regions and verify data
        reloadRegion();
        verifyData();
    }

    public void set(String point, Location loc) {
        // Get the region point enum
        RegionPoint rp = Enums.getEnumFromString(RegionPoint.class, point);
        if (rp == null) throw new IllegalArgumentException("Invalid region point '" + point + "'");

        // Then delegate
        set(rp, loc);
    }

    public void setWarp(RegionPoint point, Location l) {
        // Set the point and save
        setLocation(coords, point.toString(), l);
        save();

        // Then reload warps
        reloadWarps();
    }

    public void setLeaderboard(Location l) {
        // Set the point and save
        setLocation(coords, "leaderboard", l);
        save();

        // Then reload the leaderboards
        reloadLeaderboards();
    }

    public void addSpawn(String name, Location loc) {
        // Add the spawn and save
        setLocation(spawns, name, loc);
        save();

        // Reload spawnpoints and verify data
        reloadSpawnpoints();
        verifyData();
    }

    public boolean removeSpawn(String name) {
        // Check if the spawnpoint exists
        if (spawns.getString(name) == null) {
            return false;
        }

        // Null the spawnpoint and save
        setLocation(spawns, name, null);
        save();

        // Reload spawnpoints and verify data
        reloadSpawnpoints();
        verifyData();
        return true;
    }

    public void addChest(String name, Location loc) {
        // Add the chest location and save
        setLocation(chests, name, loc);
        save();

        // Reload the chests
        reloadChests();
    }

    public boolean removeChest(String name) {
        // Check if the chest exists
        if (chests.getString(name) == null) {
            return false;
        }

        // Null the chest and save
        setLocation(chests, name, null);
        save();

        // Reload the chests
        reloadChests();
        return true;
    }

    public void save() {
        arena.getPlugin().saveConfig();
    }

    public void showRegion(Player p) {
        if (!isDefined()) {
            return;
        }
        showBlocks(p, getFramePoints(p1, p2));
    }

    public void showLobbyRegion(Player p) {
        if (!isLobbyDefined()) {
            return;
        }
        showBlocks(p, getFramePoints(l1, l2));
    }

    public void showSpawns(Player p) {
        if (spawnpoints.isEmpty()) {
            return;
        }
        showBlocks(p, spawnpoints.values());
    }

    public void showChests(Player p) {
        if (containers.isEmpty()) {
            return;
        }
        showBlocks(p, containers.values());
    }

    public void checkSpawns(Player p) {
        if (spawnpoints.isEmpty()) {
            return;
        }

        // Find all the spawnpoints that cover the location
        Map<String,Location> map = new HashMap<>();
        for (Map.Entry<String,Location> entry : spawnpoints.entrySet()) {
            if (p.getLocation().distanceSquared(entry.getValue()) < MobArena.MIN_PLAYER_DISTANCE_SQUARED) {
                map.put(entry.getKey(), entry.getValue());
            }
        }

        if (map.isEmpty()) {
            arena.getGlobalMessenger().tell(p, "No spawnpoints cover your location!");
            return;
        }

        // Notify the player
        arena.getGlobalMessenger().tell(p, "The following points cover your location:");
        for (Map.Entry<String,Location> entry : map.entrySet()) {
            Location l = entry.getValue();
            String coords = l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ();
            p.sendMessage(ChatColor.AQUA + entry.getKey() + ChatColor.WHITE + " :  " + coords);
        }

        // And show the blocks
        showBlocks(p, map.values());
    }

    public void showBlock(final Player p, final Location loc) {
        BlockData wool = Material.RED_WOOL.createBlockData();
        arena.scheduleTask(new Runnable() {
            @Override
            public void run() {
                p.sendBlockChange(loc, wool);
                arena.scheduleTask(new Runnable() {
                    @Override
                    public void run() {
                        if (!p.isOnline()) return;

                        Block b = loc.getBlock();
                        p.sendBlockChange(loc, b.getBlockData());
                    }
                }, 100);
            }
        }, 0);
    }

    private void showBlocks(final Player p, final Collection<Location> points) {
        BlockData wool = Material.RED_WOOL.createBlockData();
        arena.scheduleTask(new Runnable() {
            @Override
            public void run() {
                // Grab all the blocks, and send block change events.
                final Map<Location,BlockState> blocks = new HashMap<>();
                for (Location l : points) {
                    Block b = l.getBlock();
                    blocks.put(l, b.getState());
                    p.sendBlockChange(l, wool);
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
                            BlockData data = b.getBlockData();

                            p.sendBlockChange(l, data);
                        }
                    }
                }, 100);
            }
        }, 0);
    }

    private List<Location> getFramePoints(Location loc1, Location loc2) {
        List<Location> result = new ArrayList<>();
        int x1 = loc1.getBlockX(); int y1 = loc1.getBlockY(); int z1 = loc1.getBlockZ();
        int x2 = loc2.getBlockX(); int y2 = loc2.getBlockY(); int z2 = loc2.getBlockZ();

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
