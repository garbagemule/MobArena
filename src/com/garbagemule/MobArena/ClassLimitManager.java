package com.garbagemule.MobArena;

import java.util.HashMap;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.util.config.ConfigSection;

public class ClassLimitManager
{
    private HashMap<ArenaClass, Integer> classLimits, classesInUse;
    private ConfigSection limits;
    private MobArena plugin;
    
    public ClassLimitManager(Arena arena) {
        this.plugin = arena.getPlugin();
        this.limits = new ConfigSection(plugin.getMAConfig(), "arenas." + arena.configName() + ".class-limits");
        
        this.classLimits  = new HashMap<ArenaClass, Integer>();
        this.classesInUse = new HashMap<ArenaClass, Integer>();

        loadLimitMap();
        initInUseMap();
    }
    
    public int getClassLimit(ArenaClass ac) {
        if (classLimits.get(ac) != null)
            return classLimits.get(ac).intValue();
        else 
            return addNewClass(ac);
    }
    
    public int getClassInUse(ArenaClass ac) {
        if (classesInUse.get(ac) != null)
            return classesInUse.get(ac).intValue();
        else {
            addNewClass(ac);
            return 0;
        }
    }
    
    private void loadLimitMap() {
        for (ArenaClass ac : plugin.getArenaMaster().getClasses().values()) {
            classLimits.put(ac, limits.getInt(ac.getName(), -1));
        }
    }
    
    private void initInUseMap() {
        for (ArenaClass ac : plugin.getArenaMaster().getClasses().values()) {
            classesInUse.put(ac, Integer.valueOf(0));
        }
    }
    
    private int addNewClass(ArenaClass ac) {
        classLimits.put(ac, Integer.valueOf(-1));
        classesInUse.put(ac, Integer.valueOf(0));
        limits.set(ac.getName(), -1);
        return -1;
    }
    
    public void playerPickedClass(ArenaClass ac) {
        classesInUse.put(ac, classesInUse.get(ac) + 1);
    }
    
    public void playerChangedClass(ArenaClass ac) {
        classesInUse.put(ac, classesInUse.get(ac) - 1);
    }
    
    public boolean canPlayerJoinClass(ArenaClass ac) {
        if (classLimits.get(ac).intValue() <= -1)
            return true;
        else if (classesInUse.get(ac).intValue() >= classLimits.get(ac).intValue())
            return false;
        else
            return true;
    }
    
    public void clearClassesInUse() {
        classesInUse.clear();
        initInUseMap();
    }
}