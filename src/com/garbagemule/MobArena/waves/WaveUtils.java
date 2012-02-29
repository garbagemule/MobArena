package com.garbagemule.MobArena.waves;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.enums.*;
import com.garbagemule.MobArena.MAUtils;
import com.garbagemule.MobArena.Messenger;
import com.garbagemule.MobArena.MobArena;

public class WaveUtils
{
    /**
     * Get all the spawnpoints that have players nearby.
     */
    public static List<Location> getValidSpawnpoints(Arena arena, List<Location> spawnpoints, Collection<Player> players) {
        MobArena plugin = arena.getPlugin();
        List<Location> result = new ArrayList<Location>();
        
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
            Messenger.warning("Spawnpoints of arena '" + arena.configName() + "' may be too far apart!");
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
                Messenger.info("Player '" + p.getName() + "' is not in the right world. Kicking...");
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

    /**
     * Grab and process all the waves in the config-file for the arena.
     */
    /*public static TreeSet<Wave> getWaves(Arena arena, Config config, WaveBranch branch)
    {
        MobArena plugin = arena.getPlugin();
        // Determine the branch type of the wave, and grab the appropriate comparator
        String b = branch.toString().toLowerCase();
        TreeSet<Wave> result = new TreeSet<Wave>(getComparator(branch));
        
        // Grab the waves from the config-file
        String path = "arenas." + arena.configName() + ".waves." + b; // waves.yml, change to either "waves." + b, or simply b
        Set<String> waves = config.getKeys(path);
        
        // If there are any waves, process them
        if (waves != null)
        {
            Wave wave;            
            for (String w : waves)
            {
                //                         ---------- path -----------  -- w --
                // path argument becomes: "arenas.<arena>.waves.<branch>.<wave>."
                wave = getWave(arena, config, path + "." + w + ".", w, branch);
                if (wave != null)
                    result.add(wave);
                else plugin.warning("Wave '" + w + "' in " + path + " was not added!");
            }
        }
        
        // If there are no waves and the type is 'recurrent', add a couple of auto-generated waves.
        if (branch == WaveBranch.RECURRENT && (result.isEmpty() || waves == null))
        {
            plugin.info("No valid recurrent waves detected for arena '" + arena.configName() + "'. Using defaults...");
            DefaultWave def  = new DefaultWave(arena, "DEF_WAVE_AUTO", 1, 1, 1, config, path + ".DEF_WAVE_AUTO.");
            SpecialWave spec = new SpecialWave(arena, "SPEC_WAVE_AUTO", 4, 4, 2, config, path + ".SPEC_WAVE_AUTO.");
            result.add(def);
            result.add(spec);
        }
        
        return result;
    }*/
    
    /**
     * Get a single wave based on the config-file, the path, and branch
     * @return A Wave object if it is well defined, null otherwise.
     */
    /*private static Wave getWave(Arena arena, Config config, String path, String name, WaveBranch branch)
    {
        MobArena plugin = arena.getPlugin();
        
        // Grab the wave type, if null or not well defined, return null
        WaveType type = WaveType.fromString(config.getString(path + "type"));
        if (type == null || !isWaveWellDefined(plugin, config, path, branch, type))
            return null;
        
        Wave result = null;
        if (branch == WaveBranch.RECURRENT)
        {
            int frequency = config.getInt(path + "frequency", 0);
            int priority = config.getInt(path + "priority", 0);
            int wave = MAUtils.getInt(config, path + "wave");
            
            if (type == WaveType.DEFAULT)
            	result = new DefaultWave(arena, name, wave, frequency, priority, config, path);
            else if (type == WaveType.SPECIAL)
            	result = new SpecialWave(arena, name, wave, frequency, priority, config, path);
            else if (type == WaveType.SWARM)
                result = new SwarmWave(arena, name, wave, frequency, priority, config, path);
            else if (type == WaveType.BOSS)
                result = new BossWave(arena, name, wave, frequency, priority, config, path);
        }
        else
        {
            int wave = MAUtils.getInt(config, path + "wave");
            
            if (type == WaveType.DEFAULT)
            	result = new DefaultWave(arena, name, wave, config, path);
            else if (type == WaveType.SPECIAL)
            	result = new SpecialWave(arena, name, wave, config, path);
            else if (type == WaveType.SWARM)
                result = new SwarmWave(arena, name, wave, config, path);
            else if (type == WaveType.BOSS)
                result = new BossWave(arena, name, wave, config, path);
        }
        return result;
    }*/
    
    
    
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
     * @param config Config-file Config
     * @param path The absolute path of the wave
     * @param branch The branch of the wave
     * @param type The wave type
     * @return true, only if the entire wave-node is well-defined.
     */
    /*private static boolean isWaveWellDefined(MobArena plugin, Config config, String path, WaveBranch branch, WaveType type)
    {
        // This boolean is used in the "leaf methods" 
        boolean wellDefined = true;
        
        // OPTIONAL: Health multiplier
        int hMulti = MAUtils.getInt(config, path + "health-multiplier");
        if (hMulti < 0)
        {
            plugin.warning("Invalid health multiplier '" + hMulti + "' in " + path);
            wellDefined = false;
        }
        
        // OPTIONAL: Amount multiplier
        int aMulti = MAUtils.getInt(config, path + "amount-multiplier");
        if (aMulti < 0)
        {
            plugin.warning("Invalid amount multiplier '" + aMulti + "' in " + path);
            wellDefined = false;
        }
        
        // Branch-specific nodes.
        if (branch == WaveBranch.RECURRENT)
        {
            // REQUIRED: Priority and frequency
            int priority  = config.getInt(path + "priority",  0);
            int frequency = config.getInt(path + "frequency", 0);
            if (priority == 0)
            {
                plugin.warning("Missing 'priority'-node in " + path);
                wellDefined = false;
            }
            if (frequency == 0)
            {
                plugin.warning("Missing 'frequency'-node in " + path);
                wellDefined = false;
            }
            
            // OPTIONAL: Wave
            int wave = MAUtils.getInt(config, path + "wave");
            if (wave < 0)
            {
                plugin.warning("'wave' must be greater than 0 in " + path);
                wellDefined = false;
            }
        }
        else if (branch == WaveBranch.SINGLE)
        {
            // REQUIRED: Wave number
            int wave = MAUtils.getInt(config, path + "wave");
            if (wave == 0)
            {
                plugin.warning("Missing 'wave'-node in " + path);
                wellDefined = false;
            }
            else if (wave < 0)
            {
                plugin.warning("'wave' must be greater than 0 in " + path);
                wellDefined = false;
            }
        }
        else wellDefined = false;

        // Passed branch-checks; check type
        return isTypeWellDefined(plugin, config, path, type, wellDefined);
    }*/
    
    /**
     * Check if a wave type in the config-file is well-defined.
     * The method calls the appropriate sub-method to check if the type
     * is well-defined.
     * @param config Config-file Config
     * @param path The absolute path of the wave
     * @param type The wave type
     * @param wellDefined Pass-through boolean for "leaf methods".
     * @return true, only if the entire wave-node is well-defined.
     */
    /*private static boolean isTypeWellDefined(MobArena plugin, Config config, String path, WaveType type, boolean wellDefined)
    {
        if (type == WaveType.DEFAULT)
            return isDefaultWaveWellDefined(plugin, config, path, wellDefined);
        else if (type == WaveType.SPECIAL)
            return isSpecialWaveWellDefined(plugin, config, path, wellDefined);
        else if (type == WaveType.BOSS)
            return isBossWaveWellDefined(plugin, config, path, wellDefined);
        else if (type == WaveType.SWARM)
            return isSwarmWaveWellDefined(plugin, config, path, wellDefined);
        
        return false;
    }*/
    
    /**
     * Check if a default wave is well-defined.
     * The default waves have an optional wave growth node. Otherwise,
     * they share nodes with special waves.
     * @param config Config-file Config
     * @param path The absolute path of the wave
     * @param wellDefined Pass-through boolean for "leaf methods".
     * @return true, only if the entire wave-node is well-defined.
     */
    /*private static boolean isDefaultWaveWellDefined(MobArena plugin, Config config, String path, boolean wellDefined)
    {
        // OPTIONAL: Wave growth
        String growth = config.getString(path + "growth");
        if (growth != null && WaveGrowth.fromString(growth) == null)
        {
            plugin.warning("Invalid wave growth '" + growth + "' in " + path);
            wellDefined = false;
        }
        
        return isNormalWaveWellDefined(plugin, config, path, wellDefined);
    }*/
    
    /**
     * Check if a special wave is well-defined.
     * The special waves have no unique nodes.
     * @param config Config-file Config
     * @param path The absolute path of the wave
     * @param wellDefined Pass-through boolean for "leaf methods".
     * @return true, only if the entire wave-node is well-defined.
     */
    /*private static boolean isSpecialWaveWellDefined(MobArena plugin, Config config, String path, boolean wellDefined)
    {
        return isNormalWaveWellDefined(plugin, config, path, wellDefined);
    }*/
    
    /**
     * Check if a default or special wave is well-defined.
     * There are no REQUIRED nodes for default or special wave types, besides
     * the ones for the branch they belong to.
     * The only OPTIONAL node is (currently) 'monsters'
     * @param config Config-file Config
     * @param path The absolute path of the wave
     * @param wellDefined Pass-through boolean for "leaf methods".
     * @return true, wellDefined is true.
     */
    /*private static boolean isNormalWaveWellDefined(MobArena plugin, Config config, String path, boolean wellDefined)
    {
        // OPTIONAL: Monsters
        Set<String> monsters = config.getKeys(path + "monsters");
        if (monsters != null)
        {
            for (String monster : monsters)
            {
                if (MACreature.fromString(monster) != null)
                    continue;
                
                plugin.warning("Invalid monster type '" + monster + "' in " + path);
                wellDefined = false;
            }
        }
        else plugin.info("No monsters listed in " + path + ", using defaults...");
        
        return wellDefined;
    }*/
    
    /**
     * Check if a swarm wave is well defined
     * @param config Config-file Config
     * @param path The absolute path of the wave
     * @param wellDefined Pass-through boolean for "leaf methods".
     * @return true, wellDefined is true.
     */
    /*private static boolean isSwarmWaveWellDefined(MobArena plugin, Config config, String path, boolean wellDefined)
    {
        // REQUIRED: Monster type
        String monster = config.getString(path + "monster");
        if (monster == null)
        {
            plugin.warning("Missing monster type in '" + path);
            wellDefined = false;
        }
        else if (MACreature.fromString(monster) == null)
        {
            plugin.warning("Invalid monster type '" + monster + "' in " + path);
            wellDefined = false;
        }
        
        // OPTIONAL: Amount
        String amount = config.getString(path + "amount");
        if (amount != null && SwarmAmount.fromString(amount) == null)
        {
            plugin.warning("Invalid swarm amount '" + amount + "' in " + path);
            wellDefined = false;
        }
        
        return wellDefined;
    }*/
    
    /**
     * Check if a boss wave is well defined.
     * @param config Config-file Config
     * @param path The absolute path of the wave
     * @param wellDefined Pass-through boolean for "leaf methods".
     * @return true, wellDefined is true.
     */
    /*private static boolean isBossWaveWellDefined(MobArena plugin, Config config, String path, boolean wellDefined)
    {
        // REQUIRED: Monster type
        String monster = config.getString(path + "monster");
        if (monster == null)
        {
            plugin.warning("Missing monster type in '" + path);
            wellDefined = false;
        }
        else if (MACreature.fromString(monster) == null)
        {
            plugin.warning("Invalid monster type '" + monster + "' in " + path);
            wellDefined = false;
        }
        
        // OPTIONAL: Abilities
        String abilities = config.getString(path + "abilities");
        if (abilities != null)
        {
            for (String ability : abilities.split(","))
            {
                if (BossAbility.fromString(ability.trim()) != null)
                    continue;

                plugin.warning("Invalid boss ability '" + ability + "' in " + path);
                wellDefined = false;
            }
        }
        
        // OPTIONAL: Ability-interval
        int abilityDelay = config.getInt(path + "ability-interval", 3);
        if (abilityDelay <= 0)
        {
            plugin.warning("Boss ability-delay must be greater than 0, " + path);
            wellDefined = false;
        }
        
        // OPTIONAL: Ability-announce
        
        // TODO: OPTIONAL: Adds
        // Unsure about config-file implementation...
        
        // OPTIONAL: Health
        String health = config.getString(path + "health");
        if (health != null && BossHealth.fromString(health) == null)
        {
            plugin.warning("Invalid boss health '" + health + "' in " + path);
            wellDefined = false;
        }
        
        return wellDefined;
    }*/
    
    
    
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
    
    /**
     * Get the enum value of a string, null if it doesn't exist.
     */
    public static <T extends Enum<T>> T getEnumFromStringCaseSensitive(Class<T> c, String string)
    {
        if(c != null && string != null)
        {
            try
            {
                return Enum.valueOf(c, string);
            }
            catch(IllegalArgumentException ex) { }
        }
        return null;
    }
}
