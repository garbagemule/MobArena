package com.garbagemule.MobArena;

import java.util.HashMap;
import java.util.Map;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.util.config.ConfigSection;
import com.garbagemule.MobArena.util.config.ConfigUtils;

public class ClassLimitManager
{
    private HashMap<ArenaClass, Integer> classLimits, classesInUse;
    private ConfigSection limits;
    private MobArena plugin;
    private Map<String, ArenaClass> classes;
    
    public ClassLimitManager(Arena arena, Map<String, ArenaClass> classes, ConfigSection limits) {
        this.plugin = arena.getPlugin();
        ConfigUtils.addMissingNodes(plugin, plugin.getMAConfig(), "arenas." + arena.configName() + ".class-limits", "class-limits.yml");
        this.limits       = limits;
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
     * This is the class a player left
     * @param ac the current/old ArenaClass
     */
    public void playerLeftClass(ArenaClass ac) {
        classesInUse.put(ac, classesInUse.get(ac) - 1);
    }
    
    /**
     * Checks to see if a player can pick a specific class
     * @param ac the ArenaClass to check
     * @return true/false
     */
    public boolean canPlayerJoinClass(ArenaClass ac) {
        if (classLimits.get(ac) == null) {
            limits.set(ac.getName(), -1);
            classLimits.put(ac, -1);
            classesInUse.put(ac, 0);
        }
        
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