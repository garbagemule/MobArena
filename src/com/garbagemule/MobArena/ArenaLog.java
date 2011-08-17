package com.garbagemule.MobArena;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;

public class ArenaLog
{
    protected MobArena plugin;
    protected Arena arena;
    protected List<String> log, classDistribution;
    protected Timestamp startTime, endTime;
    
    public ArenaLog(MobArena plugin, Arena arena)
    {
        this.plugin = plugin;
        this.arena  = arena;
        log = new LinkedList<String>();
        classDistribution = new LinkedList<String>();
    }
    
    public void start()
    {
        startTime  = new Timestamp((new Date()).getTime());
        
        // Class distribution
        int length = 0;
        for (String c : plugin.getAM().classes)
            if (c.length() > length)
                length = c.length();

        List<String> classList = new LinkedList<String>(arena.classMap.values());
        for (String c : plugin.getAM().classes)
        {
            int count = 0;
            int id = classList.indexOf(c);
            while (id != -1)
            {
                classList.remove(id);
                count++;
                id = classList.indexOf(c);
            }
            //int percentage = (int) (((double) count) / ((double) arena.livePlayers.size())) * 100;
            int percentage = (int) (((double) count) / ((double) arena.arenaPlayers.size())) * 100;
            classDistribution.add(MAUtils.padRight(c + ": ", length + 2) + MAUtils.padLeft("" + count, 2) + " (" + percentage + "%)");
        }
    }
    
    public void end()
    {
        endTime = new Timestamp((new Date()).getTime());
        
        // General stuff
        log.add("--------------------------------------------------- ENTRY ---");
        log.add("Start:     " + startTime);
        log.add("End:       " + endTime);
        log.add("Duration:  " + MAUtils.getDuration(endTime.getTime() - startTime.getTime()));
        log.add("Last wave: " + (arena.spawnThread.wave - 1));
        log.add(" ");
        
        // Class distribution
        log.add("Class Distribution: " + plugin.getAM().classes.size() + " classes");
        for (String c : classDistribution)
            log.add("- " + c);
        classDistribution.clear();
        log.add(" ");

        // Player data
        int NAME = 12; int CLASS = 0; int WAVE = 4; int KILLS = 5;
        for (String c : plugin.getAM().classes)
            if (c.length() > CLASS)
                CLASS = c.length();

        log.add("Player Data: " + arena.classMap.keySet().size() + " players");
        log.add("- " + MAUtils.padRight("Name", NAME + 2, ' ') + MAUtils.padRight("Class", CLASS + 2, ' ') + MAUtils.padRight("Wave", WAVE + 2, ' ') + MAUtils.padRight("Kills", KILLS + 2, ' ') + "Rewards");
        for (Map.Entry<Player,String> entry : arena.classMap.entrySet())
        {
            Player p = entry.getKey();
            StringBuffer buffy = new StringBuffer();
            buffy.append("  ");
            // Name
            String name = (p.getName().length() <= NAME) ? p.getName() : p.getName().substring(0, NAME+1);
            buffy.append(MAUtils.padRight(name, NAME + 2, ' '));
            // Class
            buffy.append(MAUtils.padRight(entry.getValue(), CLASS + 2, ' '));
            // Wave
            buffy.append(MAUtils.padLeft(String.valueOf(arena.waveMap.remove(p)), WAVE, ' ') + "  ");
            // Kills
            buffy.append(MAUtils.padLeft(String.valueOf(arena.killMap.remove(p)), KILLS, ' ') + "  ");
            // Rewards
            buffy.append(MAUtils.listToString(arena.rewardMap.get(p), plugin));
            log.add(buffy.toString());
        }
        
        log.add(" ");
    }
    
    public void serialize()
    {
        try
        {
            new File(plugin.getDataFolder() + File.separator + "logs").mkdir();
            File logFile = new File(plugin.getDataFolder() + File.separator + "logs" + File.separator + arena.configName() + ".log");
            if (logFile.exists())
                logFile.createNewFile();
            
            FileWriter fw = new FileWriter(logFile, true);
            BufferedWriter bw = new BufferedWriter(fw);
            for (String l : log)
            {
                bw.write(l);
                bw.write(System.getProperty("line.separator"));
            }
            
            bw.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("[MobArena] ERROR! Could not create log file!");
            return;
        }
    }

    public void add(String s) { log.add(s); }
    public void clear()       { log.clear(); }
}
