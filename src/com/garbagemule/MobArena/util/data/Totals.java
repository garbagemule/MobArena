package com.garbagemule.MobArena.util.data;

import java.io.File;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.config.Configuration;

import com.garbagemule.MobArena.Arena;
import com.garbagemule.MobArena.ArenaLog;
import com.garbagemule.MobArena.ArenaPlayer;
import com.garbagemule.MobArena.MAUtils;
import com.garbagemule.MobArena.MobArena;
import com.garbagemule.MobArena.util.FileUtils;

public class Totals
{
    private static final String totals_yml = "totals.yml";
    
    /**
     * Update the totals-file with the session data from the ArenaLog object
     * @param log ArenaLog to update with
     */
    public static void updateArenaTotals(ArenaLog log)
    {
        // Grab the configuration
        Configuration totals = getArenaTotals(log.arena);
        totals.load();
        
        // General data
        updateInt(totals, "general-info.total-games-played",            1, true);
        updateInt(totals, "general-info.most-players",                  log.players.keySet().size(), false);
        updateInt(totals, "general-info.highest-wave-reached",          log.lastWave, false);
        updateInt(totals, "general-info.total-monsters-killed",         countKills(log), true);
        updateDuration(totals, "general-info.total-duration",           log.getDurationLong(), true);
        updateDuration(totals, "general-info.longest-session-duration", log.getDurationLong(), false);
        
        // Classes
        for (String c : log.arena.getClasses())
        {
            // Array {kills, dmgDone, dmgTaken}
            int[] a = getKillsAndDamageByClass(log, c);
            updateInt(totals, "classes." + c + ".kills",        a[0], true);
            updateInt(totals, "classes." + c + ".damage-done",  a[1], true);
            updateInt(totals, "classes." + c + ".damage-taken", a[2], true);
            updateInt(totals, "classes." + c + ".played", log.distribution.get(c), true);
        }
        
        // Rewards
        for (ArenaPlayer ap : log.players.values())
        {
            for (ItemStack stack : ap.rewards)
            {
                boolean money = stack.getTypeId() == MobArena.ECONOMY_MONEY_ID;
                updateInt(totals, "rewards." + (money ? "money" : stack.getType().toString().toLowerCase()), stack.getAmount(), true);
            }
        }
        
        // Players
        for (ArenaPlayer ap : log.players.values())
        {
            // Basic values
            updateInt(totals,"players." + ap.player.getName() + ".games-played", 1,                      true);
            updateInt(totals,"players." + ap.player.getName() + ".kills",        ap.getStats().kills,    true);
            updateInt(totals,"players." + ap.player.getName() + ".damage-done",  ap.getStats().dmgDone,  true);
            updateInt(totals,"players." + ap.player.getName() + ".damage-taken", ap.getStats().dmgTaken, true);
            updateInt(totals,"players." + ap.player.getName() + ".swings",       ap.getStats().swings,   true);
            updateInt(totals,"players." + ap.player.getName() + ".hits",         ap.getStats().hits,     true);
            
            // Class count
            updateInt(totals,"players." + ap.player.getName() + ".classes." + ap.className,1,true);
        }
        
        // Save everything
        totals.save();
    }
    
    /**
     * Get or create a Configuration from the totals-file
     * @param arena Arena to get or create a Configuration for
     * @return Configuration from the arena's totals-file
     */
    public static Configuration getArenaTotals(Arena arena)
    {
        // Create the folder if it doesn't exist.
        File dir = new File(MobArena.arenaDir, arena.configName());
        if (!dir.exists()) dir.mkdirs();
        
        File file = new File(dir, totals_yml);
        if (!file.exists()) FileUtils.extractFile(dir, totals_yml);
        
        // Grab the totals-file and return the Configuration
        return new Configuration(file);
    }
    
    /**
     * Update an integer in a config-file
     * @param totals Configuration to alter
     * @param node Node to alter
     * @param b Integer for comparison
     * @param increment If true, the node will be incremented by b, otherwise overwritten if greater
     */
    private static void updateInt(Configuration totals, String node, int b, boolean increment)
    {
        int a = totals.getInt(node, 0);
        
        if (increment)  totals.setProperty(node, a+b);
        else if (b > a) totals.setProperty(node, b);
    }
    
    /**
     * Update a duration in a config-file
     * @param totals Configuration to alter
     * @param node Node to alter
     * @param b Duration for comparison. This is a java.sql.Timestamp.getTime() long
     * @param increment If true, the node will be incremented by b, otherwise overwritten if greater
     */
    private static void updateDuration(Configuration totals, String node, long b, boolean increment)
    {
        long a = MAUtils.parseDuration(totals.getString(node, "0:00:00"));
        
        if (increment)  totals.setProperty(node, MAUtils.getDuration(a+b));
        else if (b > a) totals.setProperty(node, MAUtils.getDuration(b));
    }
    
    /**
     * Get total kills from an arena session
     * @param log The ArenaLog to count kills in
     * @return Total kills
     */
    private static int countKills(ArenaLog log)
    {
        int kills = 0;
        for (ArenaPlayer ap : log.players.values())
            kills += ap.getStats().kills;
        return kills;
    }
    
    /**
     * Get a (dirty) int-array in the form {kills, damage done, damage taken} for a class
     * @param log ArenaLog to count kills, damage done/taken from
     * @param className The class to count for
     * @return Resulting kills, damage done and damage taken in an int-array
     */
    private static int[] getKillsAndDamageByClass(ArenaLog log, String className)
    {
        int kills = 0, dmgDone = 0, dmgTaken = 0;
        for (ArenaPlayer ap : log.players.values())
        {
            if (!ap.className.equals(className))
                continue;
            
            kills    += ap.getStats().kills;
            dmgDone  += ap.getStats().dmgDone;
            dmgTaken += ap.getStats().dmgTaken;
        }
        return new int[]{kills, dmgDone, dmgTaken};
    }
}
