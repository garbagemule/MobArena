package com.garbagemule.MobArena.leaderboards;

public enum Stats
{
    PLAYER_NAME("Players", "playerName"),
    CLASS_NAME("Class", "class"),
    KILLS("Kills", "kills"),
    DAMAGE_DONE("Damage Done", "dmgDone"),
    DAMAGE_TAKEN("Damage Taken", "dmgTaken"),
    SWINGS("Swings", "swings"),
    HITS("Hits", "hits"),
    LAST_WAVE("Last Wave", "lastWave");
    
    private String name, shortName;
    
    private Stats(String name, String shortName) {
        this.name      = name;
        this.shortName = shortName;
    }
    
    public String getShortName() {
        return shortName;
    }
    
    public String getFullName() {
        return name;
    }
    
    public static Stats getByFullName(String name) {
        for (Stats s : Stats.values())
            if (s.name.equals(name))
                return s;
        return null;
    }
    
    public static Stats getByShortName(String name) {
        for (Stats s : Stats.values()) {
            if (s.shortName.equalsIgnoreCase(name)) {
                return s;
            }
        }
        return null;
    }
}
