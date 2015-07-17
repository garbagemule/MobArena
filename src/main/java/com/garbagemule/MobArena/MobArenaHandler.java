package com.garbagemule.MobArena;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.garbagemule.MobArena.MobArena;
import com.garbagemule.MobArena.framework.Arena;

public class MobArenaHandler
{
    private MobArena plugin;
    
    /**
     * Primary constructor.
     * The field 'plugin' is initalized, if the server is running MobArena.
     */
    public MobArenaHandler() {
        plugin = (MobArena) Bukkit.getServer().getPluginManager().getPlugin("MobArena");
    }
    
    
    
    /*//////////////////////////////////////////////////////////////////
    
                 REGION/LOCATION METHODS
     
    //////////////////////////////////////////////////////////////////*/
    
    /**
     * Check if a Location is inside of any arena region.
     * @param loc A location.
     * @return true, if the Location is inside of any arena region.
     */
    public boolean inRegion(Location loc) {
        for (Arena arena : plugin.getArenaMaster().getArenas()) {
            if (arena.getRegion().contains(loc)) {
                return true;
            }
        }

        return false;
    }
    
    /**
     * Check if a Location is inside of a specific arena region (by arena object).
     * @param arena An Arena object
     * @param loc A location
     * @return true, if the Location is inside of the arena region.
     */
    public boolean inRegion(Arena arena, Location loc) {
        return (arena != null && arena.getRegion().contains(loc));
    }
    
    /**
     * Check if a Location is inside of a specific arena region (by arena name).
     * @param arenaName The name of an arena
     * @param loc A location
     * @return true, if the Location is inside of the arena region.
     */
    public boolean inRegion(String arenaName, Location loc) {
        Arena arena = plugin.getArenaMaster().getArenaWithName(arenaName);
        if (arena == null)
            throw new NullPointerException("There is no arena with that name");

        return arena.getRegion().contains(loc);
    }
    
    /**
     * Check if a Location is inside of the region of an arena that is currently running.
     * @param loc A location.
     * @return true, if the Location is inside of the region of an arena that is currently running.
     */
    public boolean inRunningRegion(Location loc) {
        return inRegion(loc, false, true);
    }
    
    /**
     * Check if a Location is inside of the region of an arena that is currently enabled.
     * @param loc A location.
     * @return true, if the Location is inside of the region of an arena that is currently enabled.
     */
    public boolean inEnabledRegion(Location loc) {
        return inRegion(loc, true, false);
    }
    
    /**
     * Private helper method for inRunningRegion and inEnabledRegion
     * @param loc A location
     * @param enabled if true, the method will check if the arena is enabled
     * @param running if true, the method will check if the arena is running, overrides enabled
     * @return true, if the location is inside of the region of an arena that is currently enabled/running, depending on the parameters.
     */
    private boolean inRegion(Location loc, boolean enabled, boolean running) {
        // If the plugin doesn't exist, always return false.
        if (plugin.getArenaMaster() == null) return false;

        // Return true if location is within just one arena's region.
        for (Arena arena : plugin.getArenaMaster().getArenas()) {
            if (arena.getRegion().contains(loc)) {
                if ((running && arena.isRunning()) || (enabled && arena.isEnabled())) {
                    return true;
                }
            }
        }

        return false;
    }
    
    
    
    /*//////////////////////////////////////////////////////////////////
    
                 PLAYER/MONSTER/PET METHODS
     
    //////////////////////////////////////////////////////////////////*/
    
    /**
     * Check if a player is in a MobArena arena (by Player).
     * @param player The player
     * @return true, if the player is in an arena
     */
    public boolean isPlayerInArena(Player player) {
        return (plugin.getArenaMaster().getArenaWithPlayer(player) != null);
    }
    
    /**
     * Check if a player is in a MobArena arena (by name).
     * @param playerName The name of the player
     * @return true, if the player is in an arena
     */
    public boolean isPlayerInArena(String playerName) {
        return (plugin.getArenaMaster().getArenaWithPlayer(playerName) != null);
    }
    
    /**
     * Get the MobArena class of a given player.
     * @param player The player
     * @return The class name of the player if the player is in the arena, null otherwise
     */
    public String getPlayerClass(Player player) {
        Arena arena = plugin.getArenaMaster().getArenaWithPlayer(player);
        if (arena == null) return null;

        return getPlayerClass(arena, player);
    }
    
    /**
     * Get the MobArena class of a given player in a given arena.
     * This method is faster than the above method, granted the Arena object is known.
     * @param arena The MobArena arena to check in
     * @param player The player to look up
     * @return The class name of the player, if the player is in the arena, null otherwise
     */
    public String getPlayerClass(Arena arena, Player player) {
        ArenaPlayer ap = arena.getArenaPlayer(player);
        if (ap == null) return null;
        
        ArenaClass ac = ap.getArenaClass();
        if (ac == null) return null;
        
        return ac.getLowercaseName();
    }
    
    /**
     * Check if a monster is in a MobArena arena.
     * @param entity The monster entity
     * @return true, if the monster is in an arena
     */
    public boolean isMonsterInArena(LivingEntity entity) {
        return plugin.getArenaMaster().getArenaWithMonster(entity) != null;
    }
    
    /**
     * Check if a pet is in a MobArena arena.
     * @param wolf The pet wolf
     * @return true, if the pet is in an arena
     */
    public boolean isPetInArena(LivingEntity wolf) {
        return plugin.getArenaMaster().getArenaWithPet(wolf) != null;
    }
    
    
    
    /*//////////////////////////////////////////////////////////////////
    
                 ARENA GETTERS
     
    //////////////////////////////////////////////////////////////////*/
    
    /**
     * Get an Arena object at the given location.
     * @param loc A location
     * @return an Arena object, or null
     */
    public Arena getArenaAtLocation(Location loc) {
        return plugin.getArenaMaster().getArenaAtLocation(loc);
    }
    
    /**
     * Get the Arena object that the given player is currently in.
     * @param p A player
     * @return an Arena object, or null
     */
    public Arena getArenaWithPlayer(Player p) {
        return plugin.getArenaMaster().getArenaWithPlayer(p);
    }
    
    /**
     * Get the Arena object that the given pet is currently in.
     * @param wolf A pet wolf
     * @return an Arena object, or null
     */
    public Arena getArenaWithPet(Entity wolf) {
        return plugin.getArenaMaster().getArenaWithPet(wolf);
    }
    
    /**
     * Get the Arena object that the given monster is currently in.
     * @param monster A monster
     * @return an Arena object, or null
     */
    public Arena getArenaWithMonster(Entity monster) {
        return plugin.getArenaMaster().getArenaWithMonster(monster);
    }
}
