package com.garbagemule.MobArena.waves;

import com.garbagemule.MobArena.MAUtils;
import com.garbagemule.MobArena.MobArena;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.enums.WaveBranch;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class WaveUtils
{
    /**
     * Get all the spawnpoints that have players nearby.
     */
    public static List<Location> getValidSpawnpoints(Arena arena, List<Location> spawnpoints, Collection<Player> players) {
        MobArena plugin = arena.getPlugin();
        List<Location> result = new ArrayList<>();

        // Ensure that we do have some spawnpoints.
        if (spawnpoints == null || spawnpoints.isEmpty()) {
            spawnpoints = arena.getRegion().getSpawnpointList();
        }

        // Loop through each one and check if any players are in range.
        for (Location l : spawnpoints) {
            for (Player p : players) {
                if (MAUtils.distanceSquared(plugin, p, l) >= MobArena.MIN_PLAYER_DISTANCE_SQUARED) {
                    continue;
                }
                result.add(l);
                break;
            }
        }

        // If no spawnpoints in range, just return all of them.
        if (result.isEmpty()) {
            String locs = "";
            for (Player p : players) {
                Location l = p.getLocation();
                locs += "(" + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ() + ") ";
            }
            plugin.getLogger().warning("The following locations in arena '" + arena.configName() + "' are not covered by any spawnpoints:" + locs);
            return spawnpoints;
        }
        return result;
    }

    public static Player getClosestPlayer(Arena arena, Entity e)
    {
        // Set up the comparison variable and the result.
        double dist    = 0;
        double current = Double.POSITIVE_INFINITY;
        Player result = null;

        /* Iterate through the ArrayList, and update current and result every
         * time a squared distance smaller than current is found. */
        for (Player p : arena.getPlayersInArena())
        {
            if (!arena.getWorld().equals(p.getWorld()))
            {
                arena.getPlugin().getLogger().info("Player '" + p.getName() + "' is not in the right world. Kicking...");
                p.kickPlayer("[MobArena] Cheater! (Warped out of the arena world.)");
                continue;
            }

            dist = p.getLocation().distanceSquared(e.getLocation());
            if (dist < current && dist < MobArena.MIN_PLAYER_DISTANCE_SQUARED)
            {
                current = dist;
                result = p;
            }
        }
        return result;
    }

    /*////////////////////////////////////////////////////////////////////
    //
    //      Comparators
    //
    ////////////////////////////////////////////////////////////////////*/

    /**
     * Get a comparator based on the WaveBranch parameter.
     */
    public static Comparator<Wave> getComparator(WaveBranch branch)
    {
        if (branch == WaveBranch.SINGLE)
            return getSingleComparator();
        else if (branch == WaveBranch.RECURRENT)
            return getRecurrentComparator();
        else
            return null;
    }

    /**
     * Get a Comparator that compares Wave objects by wave number.
     * If the wave numbers are equal, the waves are equal. This is to
     * DISALLOW "duplicates" in the SINGLE WAVES collection.
     * @return Comparator whose compare()-method compares wave numbers.
     */
    public static Comparator<Wave> getSingleComparator()
    {
        return new Comparator<Wave>()
            {
                public int compare(Wave w1, Wave w2)
                {
                    if (w1.getFirstWave() < w2.getFirstWave())
                        return -1;
                    else if (w1.getFirstWave() > w2.getFirstWave())
                        return 1;
                    else return 0;
                }
            };
    }

    /**
     * Get a Comparator that compares Wave objects by priority.
     * If the priorities are equal, the names are compared. This is to
     * ALLOW "duplicates" in the RECURRENT WAVES collection.
     * @return Comparator whose compare()-method compares wave priorities.
     */
    public static Comparator<Wave> getRecurrentComparator()
    {
        return new Comparator<Wave>()
            {
                public int compare(Wave w1, Wave w2)
                {
                    if (w1.getPriority() < w2.getPriority())
                        return -1;
                    else if (w1.getPriority() > w2.getPriority())
                        return 1;
                    else return w1.getName().compareTo(w2.getName());
                }
            };
    }
}
