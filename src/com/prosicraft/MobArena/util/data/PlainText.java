package com.prosicraft.MobArena.util.data;

import java.io.File;
import java.io.FileWriter;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;

import com.prosicraft.MobArena.ArenaLog;
import com.prosicraft.MobArena.ArenaPlayer;
import com.prosicraft.MobArena.MAUtils;
import com.prosicraft.MobArena.MobArena;
import com.prosicraft.MobArena.util.TextUtils;

public class PlainText
{
    public static void saveSessionData(ArenaLog log)
    {
        File dir = new File(MobArena.arenaDir, log.getArena().configName());
        if (!dir.exists()) dir.mkdirs();
        
        // General information
        List<String> toWrite = new LinkedList<String>();
        toWrite.add("Start:     " + log.getStartTime());
        toWrite.add("End:       " + log.getEndTime());
        toWrite.add("Duration:  " + log.getDuration());
        toWrite.add("Last wave: " + log.getLastWave());
        toWrite.add(" ");
        
        int classLength = longestClassName(log.distribution.keySet());
        int classCount  = log.distribution.keySet().size();
        int playerCount = log.players.keySet().size();
        
        // Class distribution
        toWrite.add("Class Distribution: " + classCount + " classes");
        toWrite.addAll(getClassDistribution(log.distribution, playerCount, classLength));
        toWrite.add(" ");
        
        // Player data
        toWrite.add("Player Data: " + playerCount + " players");
        toWrite.addAll(getPlayerData(log.players, classLength));
        
        // Serialize!
        serialize(log, dir, "lastsession.txt", toWrite);
    }
    
    public static void updateArenaTotals(ArenaLog log)
    {
        // Grab the Configuration
        //Configuration totals = Totals.getArenaTotals(log.arena);
        
        // Parse shit
        
        
        // Serialize
        
    }
    
    private static void serialize(ArenaLog log, File dir, String filename, List<String> toWrite)
    {
        try
        {
            File file = new File(dir, filename);
            if (!file.exists()) file.createNewFile();
            
            FileWriter fw = new FileWriter(file);
            String linebreak = System.getProperty("line.separator");
            for (String s : toWrite)
            {
                fw.write(s);
                fw.write(linebreak);
            }
            fw.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            MobArena.warning("Problem saving session data for arena '" + log.getArena().configName() + "'");
        }
    }
    
    private static List<String> getClassDistribution(Map<String,Integer> map, int playerCount, int classLength)
    {
        List<String> result = new LinkedList<String>();
        
        // Add each entry - class: # (percentage%)
        for (Map.Entry<String,Integer> entry : map.entrySet())
        {
            StringBuffer buffy = new StringBuffer(classLength + 15);
            
            buffy.append("- ");
            buffy.append(TextUtils.padRight(entry.getKey() + ":", classLength + 3));  // <classname>:
            buffy.append(TextUtils.padLeft(entry.getValue().toString(), 2));     // <count>
            buffy.append("(" + (entry.getValue()*100/playerCount) + "%)");  // (<count/playerCount>%)
            
            result.add(buffy.toString());
        }
        
        return result;
    }
    
    private static List<String> getPlayerData(Map<Player,ArenaPlayer> map, int classLength)
    {
        List<String> result = new LinkedList<String>();
        String pad = "  ";
        
        // Padding variables; +2 for extra padding
        int NAME = 12, CLASS = classLength, WAVE = 4, KILLS = 5, DMGDONE = 7, DMGTAKEN = 8, ACCURACY = 8;
        result.add("  " + TextUtils.padRight("NAME", NAME+2) +
                          TextUtils.padRight("CLASS", CLASS+2) + 
                          TextUtils.padRight("WAVE", WAVE+2) +
                          TextUtils.padRight("KILLS", KILLS+2) +
                          TextUtils.padRight("DMGDONE", DMGDONE+2) +
                          TextUtils.padRight("DMGTAKEN", DMGTAKEN+2) +
                          TextUtils.padRight("ACCURACY", ACCURACY+2) +
                          "REWARDS");
        
        // Add each player - name class wave dmgdone dmgtaken accuracy rewards
        for (Map.Entry<Player,ArenaPlayer> entry : map.entrySet())
        {
            Player p = entry.getKey();
            ArenaPlayer ap = entry.getValue();            
            StringBuffer buffy = new StringBuffer(80);
            
            String name = (p.getName().length() <= NAME) ? p.getName() : p.getName().substring(0, NAME+1);
            buffy.append("- ");
            buffy.append(TextUtils.padRight(name, NAME)); buffy.append(pad);
            buffy.append(TextUtils.padRight(ap.getClassName(), CLASS)); buffy.append(pad);
            buffy.append(TextUtils.padLeft(ap.getStats().lastWave, WAVE)); buffy.append(pad);
            buffy.append(TextUtils.padLeft(ap.getStats().kills, KILLS)); buffy.append(pad);
            buffy.append(TextUtils.padLeft(ap.getStats().dmgDone, DMGDONE)); buffy.append(pad);
            buffy.append(TextUtils.padLeft(ap.getStats().dmgTaken, DMGTAKEN)); buffy.append(pad);
            buffy.append(TextUtils.padLeft(((ap.getStats().swings != 0) ? ap.getStats().hits*100/ap.getStats().swings : 0), ACCURACY-1)); buffy.append("%"); buffy.append(pad);
            buffy.append(MAUtils.listToString(ap.rewards));
            
            result.add(buffy.toString());
        }
        
        return result;
    }
    
    private static int longestClassName(Collection<String> names)
    {
        int result = 0;
        
        for (String c : names)
            if (c.length() > result)
                result = c.length();
        
        return result;
    }
}
