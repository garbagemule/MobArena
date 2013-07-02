package com.garbagemule.MobArena;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.bukkit.entity.Player;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.util.MutableInt;
import com.garbagemule.MobArena.util.config.ConfigSection;

public class ClassLimitManager
{
    private HashMap<ArenaClass,MutableInt> classLimits;
    private HashMap<ArenaClass, HashSet<String>> classesInUse;
    private ConfigSection limits;
    private Map<String,ArenaClass> classes;
    
    public ClassLimitManager(Arena arena, Map<String,ArenaClass> classes, ConfigSection limits) {
        this.limits       = limits;
        this.classes      = classes;
        this.classLimits  = new HashMap<ArenaClass,MutableInt>();
        this.classesInUse = new HashMap<ArenaClass, HashSet<String>>();

        loadLimitMap();
        initInUseMap();
    }
    
    private void loadLimitMap() {
        // If the config-section is empty, create and populate it.
        if (limits.getKeys() == null) {
            for (ArenaClass ac : classes.values()) {
                limits.set(ac.getConfigName(), -1);
            }
            limits.getParent().save();
        }
        
        // Populate the limits map using the values in the config-file.
        for (ArenaClass ac : classes.values()) {
            classLimits.put(ac, new MutableInt(limits.getInt(ac.getConfigName(), -1)));
        }
    }
    
    private void initInUseMap() {
        // Initialize the in-use map with zeros.
        for (ArenaClass ac : classes.values()) {
            classesInUse.put(ac, new HashSet<String>());
        }
    }
    
    /**
     * This is the class a player is changing to
     * @param ac the new ArenaClass
     */
    public void playerPickedClass(ArenaClass ac, Player p) {
        classesInUse.get(ac).add(p.getName());
    }
    
    /**
     * This is the class a player left
     * @param ac the current/old ArenaClass
     */
    public void playerLeftClass(ArenaClass ac, Player p) {
        if (ac != null) {
            classesInUse.get(ac).remove(p.getName());
        }
    }
    
    /**
     * Checks to see if a player can pick a specific class
     * @param ac the ArenaClass to check
     * @return true/false
     */
    public boolean canPlayerJoinClass(ArenaClass ac) {
        if (classLimits.get(ac) == null) {
            limits.set(ac.getConfigName(), -1);
            classLimits.put(ac, new MutableInt(-1));
            classesInUse.put(ac, new HashSet<String>());
        }
        
        if (classLimits.get(ac).value() <= -1)
            return true;
        
        return classesInUse.get(ac).size() < classLimits.get(ac).value();
    }
    
    /**
     * returns a set of Player Names who have picked an ArenaClass
     * @param ac the ArenaClass in question
     * @return the Player Names who have picked the provided ArenaClass
     */
    public HashSet<String> getPlayersWithClass(ArenaClass ac) {
        return classesInUse.get(ac);
    }
    
    /**
     * Clear the classes in use map and reinitialize it for the next match
     */
    public void clearClassesInUse() {
        classesInUse.clear();
        initInUseMap();
    }
}