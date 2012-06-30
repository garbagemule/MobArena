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
    
    public ClassLimitManager(Arena arena, Map<String, ArenaClass> classes) {
        this.plugin = arena.getPlugin();
        ConfigUtils.addMissingNodes(plugin, plugin.getMAConfig(), "arenas." + arena.configName() + ".class-limits", "class-limits.yml");
        this.limits = new ConfigSection(plugin.getMAConfig(), "arenas." + arena.configName() + ".class-limits");
        //TODO figure out why limits is not loading the proper values from the config
        //TODO perhaps send along the limits config section from the ArenaImpl class?
        //TODO try to use ArenaRegion's config stuff to set up CLM properly
        
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
        debug();
    }
    
    /**
     * This is the class a player left
     * @param ac the current/old ArenaClass
     */
    public void playerLeftClass(ArenaClass ac) {
        classesInUse.put(ac, classesInUse.get(ac) - 1);
        debug();
    }
    
    /**
     * Checks to see if a player can pick a specific class
     * @param ac the ArenaClass to check
     * @return true/false
     */
    public boolean canPlayerJoinClass(ArenaClass ac) {
        debug();
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
        debug();
    }
    
    private void debug() {
        System.out.println("classesInUse:");
        for (ArenaClass ac : classesInUse.keySet())
            System.out.println(ac.getName() + " has " + classesInUse.get(ac).intValue() + " players using it.");
        System.out.println();
        System.out.println("classLimits:");
        for (ArenaClass ac : classLimits.keySet())
            System.out.println(ac.getName() + " has a limit of " + classLimits.get(ac).intValue() + " players.");
    }
}