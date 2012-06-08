package com.garbagemule.MobArena;

import java.util.HashMap;
import java.util.Map;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.util.config.ConfigSection;

public class ClassLimitManager
{
    private HashMap<ArenaClass, Integer> classLimits, classesInUse;
    private ConfigSection limits;
    private MobArena plugin;
    private Map<String, ArenaClass> classes;
    
    public ClassLimitManager(Arena arena, Map<String, ArenaClass> classes) {
        this.plugin = arena.getPlugin();
        this.limits = new ConfigSection(plugin.getMAConfig(), "arenas." + arena.configName() + ".class-limits");
        
        this.classes      = classes;
        this.classLimits  = new HashMap<ArenaClass, Integer>();
        this.classesInUse = new HashMap<ArenaClass, Integer>();

        loadLimitMap();
        initInUseMap();
    }
    
    private void loadLimitMap() {
        for (ArenaClass ac : classes.values()) {
            classLimits.put(ac, limits.getInt(ac.getName(), -1));
        }
    }
    
    private void initInUseMap() {
        for (ArenaClass ac : classes.values()) {
            classesInUse.put(ac, 0);
        }
    }
    
    /**
     * This is the class a player is changing to
     * @param ac the new ArenaClass
     */
    public void playerPickedClass(ArenaClass ac) {
        classesInUse.put(ac, classesInUse.get(ac) + 1);
    }
    
    /**
     * This is the class a player changed from
     * @param ac the current/old ArenaClass
     */
    public void playerChangedClass(ArenaClass ac) {
        classesInUse.put(ac, classesInUse.get(ac) - 1);
    }
    
    /**
     * Checks to see if a player can pick a specific class
     * @param ac the ArenaClass to check
     * @return true/false
     */
    public boolean canPlayerJoinClass(ArenaClass ac) {
        if (classLimits.get(ac) <= -1)
            return true;
        else if (classesInUse.get(ac) >= classLimits.get(ac))
            return false;
        else
            return true;
    }
    
    public void clearClassesInUse() {
        classesInUse.clear();
        initInUseMap();
    }
}