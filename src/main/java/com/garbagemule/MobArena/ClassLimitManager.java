package com.garbagemule.MobArena;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.util.MutableInt;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

public class ClassLimitManager
{
    private final HashMap<ArenaClass,MutableInt> classLimits;
    private final HashMap<ArenaClass, HashSet<String>> classesInUse;
    private final ConfigurationSection limits;
    private final Map<String,ArenaClass> classes;

    public ClassLimitManager(Arena arena, Map<String,ArenaClass> classes, ConfigurationSection limits) {
        this.limits       = limits;
        this.classes      = classes;
        this.classLimits  = new HashMap<>();
        this.classesInUse = new HashMap<>();

        loadLimitMap(arena.getPlugin());
        initInUseMap();
    }

    private void loadLimitMap(Plugin plugin) {
        // If the config-section is empty, create and populate it.
        if (limits.getKeys(false).isEmpty()) {
            classes.values().forEach(arenaClass -> limits.set(arenaClass.getConfigName(), -1));
            plugin.saveConfig();
        }

        // Populate the limits map using the values in the config-file.
        classes.values().forEach(arenaClass -> classLimits.put(arenaClass,
                new MutableInt(limits.getInt(arenaClass.getConfigName(), -1))));
    }

    private void initInUseMap() {
        // Initialize the in-use map with zeros.
        classes.values().forEach(arenaClass -> classesInUse.put(arenaClass, new HashSet<>()));
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
        Optional.ofNullable(ac).ifPresent(arenaClass ->
                classesInUse.get(arenaClass).remove(p.getName()));
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
            classesInUse.put(ac, new HashSet<>());
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
