package com.garbagemule.MobArena.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import org.bukkit.Location;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;

import com.garbagemule.MobArena.Arena;
import com.garbagemule.MobArena.MAUtils;
import com.garbagemule.MobArena.MobArena;
import com.garbagemule.MobArena.waves.*;
import com.garbagemule.MobArena.waves.Wave.*;

public class WaveUtils
{
    /**
     * Get all the spawnpoints that have players nearby.
     */
    public static List<Location> getValidSpawnpoints(Collection<Location> spawnpoints, Collection<Player> players)
    {
        List<Location> result = new ArrayList<Location>();
        
        for (Location s : spawnpoints)
        {
            for (Player p : players)
            {
                // If the player somehow got out of the arena world, kick him.
                if (!s.getWorld().getName().equals(p.getWorld().getName()))
                {
                    System.out.println("[MobArena] Player '" + p.getName() + "' is not in the right world. Kicking...");
                    p.kickPlayer("[MobArena] Cheater! (Warped out of the arena world.)");
                    continue;
                }
                
                if (s.distanceSquared(p.getLocation()) > MobArena.MIN_PLAYER_DISTANCE)
                    continue;
                
                result.add(s);
                break;
            }
        }
        
        // If no players are in range, just use all the spawnpoints.
        if (result.isEmpty())
            result.addAll(spawnpoints);
        
        // Else, return the valid spawnpoints.
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
        //for (Player p : arena.livePlayers)
        for (Player p : arena.getLivingPlayers())
        {
            if (!arena.getWorld().equals(p.getWorld()))
            {
                System.out.println("[MobArena] Player '" + p.getName() + "' is not in the right world. Force leaving...");
                arena.playerLeave(p);
                MAUtils.tellPlayer(p, "You warped out of the arena world.");
                continue;
            }
            
            dist = p.getLocation().distanceSquared(e.getLocation());
            if (dist < current && dist < MobArena.MIN_PLAYER_DISTANCE)
            {
                current = dist;
                result = p;
            }
        }
        return result;
    }

    /**
     * Grab and process all the waves in the config-file for the arena.
     */
    public static TreeSet<Wave> getWaves(Arena arena, Configuration config, WaveBranch branch)
    {
        // Determine the branch type of the wave, and grab the appropriate comparator
        String b = branch.toString().toLowerCase();
        TreeSet<Wave> result = new TreeSet<Wave>(getComparator(branch));
        
        // Grab the waves from the config-file
        String path = "arenas." + arena.configName() + ".waves." + b; // waves.yml, change to either "waves." + b, or simply b
        List<String> waves = config.getKeys(path);
        
        // If there are any waves, process them
        if (waves != null)
        {
            Wave wave;            
            for (String w : waves)
            {
                // path argument becomes: "arenas.<arena>.waves.<branch>.<wave>."
                wave = getWave(arena, config, path + "." + w + ".", w, branch);
                if (wave != null) result.add(wave);
            }
        }
        
        // If there are no waves and the type is 'recurrent', add a default wave.
        if (branch == WaveBranch.RECURRENT && (result.isEmpty() || waves == null))
        {
            /*
            DefaultWave def = new DefaultWave(arena, "DEF_WAVE_AUTO", 1, 1, 1, null, null);
            def.setType(WaveType.DEFAULT);
            def.setGrowth(WaveGrowth.MEDIUM);
            result.add(def);
            */
        }
        
        return result;
    }
    
    /**
     * Get a single wave based on the config-file, the path, and branch
     * @return A Wave object if it is well defined, null otherwise.
     */
    private static Wave getWave(Arena arena, Configuration config, String path, String name, WaveBranch branch)
    {
        // Grab the wave type, if null or not well defined, return null
        WaveType type = WaveType.fromString(config.getString(path + "type"));
        if (type == null || !isWaveWellDefined(config, path, branch, type))
            return null;
        
        // TODO: Generate waves properly. These are place-holders!
        Wave result;
        if (branch == WaveBranch.RECURRENT)
        {
            int frequency = config.getInt(path + "frequency", 0);
            int priority = config.getInt(path + "priority", 0);
            int wave = config.getInt(path + "wave", frequency);
            
            //if (type == WaveType.DEFAULT)
            	result = new DefaultWave(arena, name, wave, frequency, priority, config, path);
            	result.setGrowth(WaveGrowth.OLD);
            //else
            //	result = new SpecialWave(arena, name, wave, frequency, priority, config, path);
        }
        else
        {
            int wave = config.getInt(path + "wave", 0);
            
            //if (type == WaveType.DEFAULT)
            	result = new DefaultWave(arena, name, wave, config, path);
                result.setGrowth(WaveGrowth.OLD);
            //else
            //	result = new SpecialWave(arena, name, wave, config, path);
        }
        return result;
    }
    
    
    
    /*////////////////////////////////////////////////////////////////////
    //
    //      Well definedness checks
    //
    ////////////////////////////////////////////////////////////////////*/
    
    /**
     * Check if a wave in the config-file is well-defined.
     * The method first checks if the wave is well-defined according to
     * the branch-specific requirements. Recurrent waves must have the
     * two nodes 'priority' and 'frequency', and single waves must have
     * the node 'wave'.
     * Any other requirements are type-specific, and thus we check if the
     * type is well-defined.
     * @param config Config-file Configuration
     * @param path The absolute path of the wave
     * @param branch The branch of the wave
     * @param type The wave type
     * @return true, if the wave is well-defined, false otherwise
     */
    private static boolean isWaveWellDefined(Configuration config, String path, WaveBranch branch, WaveType type)
    {
        if (branch == WaveBranch.RECURRENT)
        {
            // REQUIRED: Priority and frequency
            int priority  = config.getInt(path + "priority",  0);
            int frequency = config.getInt(path + "frequency", 0);
            if (priority == 0 || frequency == 0)
                return false;
            
            // TODO: OPTIONAL: Wave growth, others?
        }
        else if (branch == WaveBranch.SINGLE)
        {
            // REQUIRED: Wave number
            int wave = config.getInt(path + "wave", 0);
            if (wave == 0)
                return false;
        }
        else return false;

        // Passed branch-checks; check type
        return isTypeWellDefined(config, path, type);
    }
    
    /**
     * Check if a wave type in the config-file is well-defined.
     * The method calls the appropriate sub-method to check if the type
     * is well-defined.
     * @param config Config-file Configuration
     * @param path The absolute path of the wave
     * @param type The wave type
     * @return true, if the wave type is well-defined, false otherwise
     */
    private static boolean isTypeWellDefined(Configuration config, String path, WaveType type)
    {
        if (type == WaveType.DEFAULT || type == WaveType.SPECIAL)
            return isNormalWaveWellDefined(config, path);
        else if (type == WaveType.BOSS)
            return isBossWaveWellDefined(config, path);
        else if (type == WaveType.SWARM)
            return isSwarmWaveWellDefined(config, path);
        
        return false;
    }
    
    /**
     * Check if a default or special wave is well-defined.
     * There are no REQUIRED nodes for default or special wave types, besides
     * the ones for the branch they belong to.
     * The only OPTIONAL node is (currently) 'monsters'
     * @param config Config-file Configuration
     * @param path The absolute path of the wave
     * @return true, if the wave type is well-defined, false otherwise
     */
    private static boolean isNormalWaveWellDefined(Configuration config, String path)
    {
        // OPTIONAL: Monsters
        List<String> monsters = config.getKeys(path + "monsters");
        if (monsters == null)
            return true;
        
        for (String monster : monsters)
        {
            //if (getEnumFromString(CreatureType.class, monster) != null)
            if (getEnumFromString(MACreature.class, monster) != null)
                continue;
            
            MAUtils.error("Invalid monster type '" + monster + "' in " + path);
            return false;
        }
        
        return true;
    }
    
    /**
     * Check if a swarm wave is well defined
     * @param config Config-file Configuration
     * @param path The absolute path of the wave
     * @return true, if the wave type is well-defined, false otherwise
     */
    private static boolean isSwarmWaveWellDefined(Configuration config, String path)
    {
        // REQUIRED: Monster type
        String monster = config.getString(path + "monster");
        if (monster == null)
        {
            MAUtils.error("Missing monster type in '" + path);
            return false;
        }
        else if (getEnumFromString(CreatureType.class, monster) == null)
        {
            MAUtils.error("Invalid monster type '" + monster + "' in " + path);
            return false;
        }
        
        // OPTIONAL: Amount
        String amount = config.getString(path + "amount");
        if (amount != null && SwarmAmount.fromString(amount) == null)
            return false;
        
        return true;
    }
    
    /**
     * Check if a boss wave is well defined.
     * @param config Config-file Configuration
     * @param path The absolute path of the wave
     * @return true, if the wave type is well-defined, false otherwise
     */
    private static boolean isBossWaveWellDefined(Configuration config, String path)
    {
        // REQUIRED: Monster type
        String monster = config.getString(path + "monster");
        if (monster == null)
        {
            MAUtils.error("Missing monster type in '" + path);
            return false;
        }
        else if (getEnumFromString(CreatureType.class, monster) == null)
        {
            MAUtils.error("Invalid monster type '" + monster + "' in " + path);
            return false;
        }
        
        // OPTIONAL: Abilities
        String abilities = config.getString(path + "abilities");
        if (abilities != null)
        {
            for (String ability : abilities.split(","))
            {
                if (BossAbility.fromString(ability.trim().toUpperCase()) != null)
                    continue;

                MAUtils.error("Invalid boss ability '" + ability + "' in " + path);
                return false;
            }
        }
        
        // TODO: OPTIONAL: Adds
        // Unsure about config-file implementation...
        
        // OPTIONAL: Health
        String health = config.getString(path + "health");
        if (health != null && BossHealth.fromString(health) == null)
            return false;
        
        return true;
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
                    if (w1.getWave() < w2.getWave())
                        return -1;
                    else if (w1.getWave() > w2.getWave())
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
    
    
    
    /*////////////////////////////////////////////////////////////////////
    //
    //      Misc - Perhaps move into MAUtils?
    //
    ////////////////////////////////////////////////////////////////////*/
    
    /**
     * Get the num value of a string, def if it doesn't exist.
     */
    public static <T extends Enum<T>> T getEnumFromString(Class<T> c, String string, T def)
    {
        if (c != null && string != null)
        {
            try
            {
                return Enum.valueOf(c, string.trim().toUpperCase());
            }
            catch (IllegalArgumentException ex) { }
        }
        return def;
    }
    
    /**
     * Get the enum value of a string, null if it doesn't exist.
     */
    public static <T extends Enum<T>> T getEnumFromString(Class<T> c, String string)
    {
        if(c != null && string != null)
        {
            try
            {
                return Enum.valueOf(c, string.trim().toUpperCase());
            }
            catch(IllegalArgumentException ex) { }
        }
        return null;
    }
}
