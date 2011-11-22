package com.prosicraft.MobArena.leaderboards;

import com.garbagemule.MobArena.util.WaveUtils;

public enum Stats
{
    players("Players", "playerName"),
    className("Class", "className"),
    kills("Kills", "kills"),
    dmgDone("Damage Done", "dmgDone"),
    dmgTaken("Damage Taken", "dmgTaken"),
    swings("Swings", "swings"),
    hits("Hits", "hits"),
    lastWave("Last Wave", "lastWave");
    
    private String name, shortName;
    
    private Stats(String name, String shortName)
    {
        this.name      = name;
        this.shortName = shortName;
    }
    
    public String getShortName()
    {
        return shortName;
    }
    
    public String getFullName()
    {
        return name;
    }
    
    public static Stats fromString(String name)
    {
        if (name.equals("class"))
            return Stats.className;
        return WaveUtils.getEnumFromStringCaseSensitive(Stats.class, name);
    }
    
    public static Stats getByFullName(String name)
    {
        
        for (Stats s : Stats.values())
            if (s.name.equals(name))
                return s;
        return null;
    }
}
