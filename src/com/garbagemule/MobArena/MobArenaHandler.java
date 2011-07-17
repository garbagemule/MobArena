package com.garbagemule.MobArena;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class MobArenaHandler
{
    MobArena plugin;
    boolean ma = false;
    
    /**
     * Primary constructor.
     * The boolean 'ma' is flagged true, and the field 'plugin' is initalized, if the server is running MobArena.
     */
    public MobArenaHandler()
    {
        Plugin maPlugin = (MobArena) Bukkit.getServer().getPluginManager().getPlugin("MobArena");
        
        if (maPlugin == null)
            return;

        ma = true;
        plugin = (MobArena) maPlugin;
    }
    
    /**
     * Check if a Location is inside of an arena region.
     * @param loc A location.
     * @return true, if the Location is inside of any arena region.
     */
    public boolean inRegion(Location loc)
    {
        // If the plugin doesn't exist, always return false.
        if (!ma || plugin.getAM() == null) return false;
        
        // Return true if location is within just one arena's region.
        for (Arena arena : plugin.getAM().arenas)
            if (arena.inRegion(loc))
                return true;
        
        return false;
    }
    
    /**
     * Check if a Location is inside of a specific arena region.
     * @param arena An Arena object
     * @param loc A location
     * @return true, if the Location is inside of the arena region.
     */
    public boolean inRegion(Arena arena, Location loc) { return (ma && arena != null && arena.inRegion(loc)); }
    
    /**
     * Check if a Location is inside of the region of an arena that is currently running.
     * @param loc A location.
     * @return true, if the Location is inside of the region of an arena that is currently running.
     */
    public boolean inRunningRegion(Location loc) { return inRegion(loc, false, true); }
    
    /**
     * Check if a Location is inside of the region of an arena that is currently enabled.
     * @param loc A location.
     * @return true, if the Location is inside of the region of an arena that is currently enabled.
     */
    public boolean inEnabledRegion(Location loc) { return inRegion(loc, true, false); }
    
    /**
     * Private helper method for inRunningRegion and inEnabledRegion
     * @param loc A location
     * @param enabled if true, the method will check if the arena is enabled
     * @param running if true, the method will check if the arena is running, overrides enabled
     * @return true, if the location is inside of the region of an arena that is currently enabled/running, depending on the parameters.
     */
    private boolean inRegion(Location loc, boolean enabled, boolean running)
    {
        // If the plugin doesn't exist, always return false.
        if (!ma || plugin.getAM() == null) return false;
        
        // Return true if location is within just one arena's region.
        for (Arena arena : plugin.getAM().arenas)
            if (arena.inRegion(loc))
                if ((running && arena.running) || (enabled && arena.enabled))
                    return true;
        
        return false;
    }
    
    /**
     * Get an Arena object at the given location.
     * @param loc A location
     * @return an Arena object, or null
     */
    public Arena getArenaAtLocation(Location loc) { return (ma) ? plugin.getAM().getArenaAtLocation(loc) : null; }
    
    /**
     * Get the Arena object that the given player is currently in.
     * @param p A player
     * @return an Arena object, or null
     */
    public Arena getArenaWithPlayer(Player p) { return (ma) ? plugin.getAM().getArenaWithPlayer(p) : null; }
    
    /**
     * Get the Arena object that the given pet is currently in.
     * @param wolf A pet wolf
     * @return an Arena object, or null
     */
    public Arena getArenaWithPet(Entity wolf) { return (ma) ? plugin.getAM().getArenaWithPet(wolf) : null; }
    
    /**
     * Get the Arena object that the given monster is currently in.
     * @param monster A monster
     * @return an Arena object, or null
     */
    public Arena getArenaWithMonster(Entity monster) { return (ma) ? plugin.getAM().getArenaWithMonster(monster) : null; }
    
    /**
     * Check if the server is running MobArena.
     * @return true, if MobArena exists on the server.
     */
    public boolean hasMA()
    {
        return ma;
    }
}
