package com.garbagemule.MobArena.util.data;

import java.io.File;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.config.Configuration;

import com.garbagemule.MobArena.ArenaLog;
import com.garbagemule.MobArena.ArenaPlayer;
import com.garbagemule.MobArena.MobArena;

public class YAML
{
    public static void saveSessionData(ArenaLog log)
    {
        File dir = new File(MobArena.arenaDir, log.getArena().configName());
        if (!dir.exists()) dir.mkdirs();
        
        // Make Configuration object
        Configuration config = new Configuration(new File(dir, "lastsession.yml"));
        config.load();
        
        // Reset any existing data
        config.setProperty("general-info", null);
        config.setProperty("class-distribution", null);
        config.setProperty("player-data", null);
        
        // General information
        config.setProperty("general-info.start-time", log.getStartTime());
        config.setProperty("general-info.end-time", log.getEndTime());
        config.setProperty("general-info.duration", log.getDuration());
        config.setProperty("general-info.last-wave", log.getLastWave());
        
        // Class distribution
        for (Map.Entry<String,Integer> entry : log.distribution.entrySet())
            config.setProperty("class-distribution." + entry.getKey(), entry.getValue());
        
        // Player data
        for (Map.Entry<Player,ArenaPlayer> entry : log.players.entrySet())
        {
            String p = entry.getKey().getName();
            ArenaPlayer ap = entry.getValue();

            config.setProperty("player-data." + p + ".last-wave", ap.getStats().lastWave);
            config.setProperty("player-data." + p + ".kills", ap.getStats().kills);
            config.setProperty("player-data." + p + ".damage-done", ap.getStats().dmgDone);
            config.setProperty("player-data." + p + ".damage-taken", ap.getStats().dmgTaken);
            config.setProperty("player-data." + p + ".swings", ap.getStats().swings);
            config.setProperty("player-data." + p + ".hits", ap.getStats().hits);
            for (ItemStack stack : ap.rewards)
            {
                boolean money = stack.getTypeId() == MobArena.ECONOMY_MONEY_ID;
                String path = "player-data." + p + ".rewards." + (money ? "money" : stack.getType().toString().toLowerCase());
                config.setProperty(path, config.getInt(path, 0) + stack.getAmount());
            }
        }
        
        // Save the file
        config.save();
    }
}
