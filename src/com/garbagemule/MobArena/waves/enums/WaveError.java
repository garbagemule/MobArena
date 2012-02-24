package com.garbagemule.MobArena.waves.enums;

public enum WaveError
{
    INVALID_WAVE("Wave '%s' for arena '%s' could not be parsed."),
    NO_RECURRENT_WAVES("No valid recurrent waves found for arena '%s'. Check the config-file. Using implicit default wave."),
    
    BRANCH_MISSING("The '%s' branch for arena '%s' is empty. Check the config-file."),
    INVALID_TYPE("Invalid type '%s' for wave '%s' in arena '%s'. Skipping..."),
    
    RECURRENT_NODES("Recurrent wave '%s' in arena '%s' is missing either the 'frequency' or the 'priority' node. Skipping..."),
    SINGLE_NODES("Single wave '%s' in arena '%s' is missing the 'wave' node. Skipping..."),
    
    MONSTER_MAP_MISSING("Missing 'monsters' node for wave '%s' in arena '%s'."),
    SINGLE_MONSTER_MISSING("Missing 'monster' node for wave '%s' in arena '%s'."),
    
    BOSS_ABILITY("Invalid boss ability '%s' for wave '%s' in arena '%s'."),
    
    SUPPLY_DROPS("Missing 'drops' node for wave '%s' in arena '%s'."),
    
    UPGRADE_MAP_MISSING("Missing 'upgrades' node for wave '%s' in arena '%s'.");
    
    private String msg;
    
    private WaveError(String msg) {
        this.msg = msg;
    }
    
    public String format(Object... args) {
        return String.format(msg, args);
    }
}