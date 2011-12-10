package com.garbagemule.MobArena;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
//import org.bukkit.util.config.Configuration;

import com.garbagemule.MobArena.MAMessages.Msg;

//import com.garbagemule.ArenaPlugin.Master;

public abstract class ArenaMaster //implements Master
{
    protected Arena selectedArena;
    
    // Settings
    protected boolean enabled, updateNotify;
    
    // Classes
    protected Set<String> classes;
    protected Map<String,List<ItemStack>>  classItems, classArmor;
    protected Map<String,Map<String,Boolean>> classPerms;
    //protected Map<Integer,Map<Player,List<ItemStack>>> classBonuses;
    protected Map<Player,Arena> arenaMap;
    
    // Location map
    protected Map<Player,Location> locations = new HashMap<Player,Location>();
    
    // Arena list
    protected List<Arena> arenas;
    
    // Listeners
    protected Set<MobArenaListener> listeners = new HashSet<MobArenaListener>();
    
    
    
    /*/////////////////////////////////////////////////////////////////////////
    //
    //      Arena getters
    //
    /////////////////////////////////////////////////////////////////////////*/
    

    public abstract List<Arena> getEnabledArenas();
    
    public abstract List<Arena> getPermittedArenas(Player p);
    
    public abstract Arena getArenaAtLocation(Location loc);
    
    public abstract List<Arena> getArenasInWorld(World world);
    
    public abstract List<Player> getAllPlayers();
    
    public abstract List<Player> getAllPlayersInArena(String arenaName);
    
    public abstract List<Player> getAllLivingPlayers();
    
    public abstract List<Player> getLivingPlayersInArena(String arenaName);
    
    public abstract Arena getArenaWithPlayer(Player p);
    
    public abstract Arena getArenaWithPlayer(String playerName);
    
    public abstract Arena getArenaWithSpectator(Player p);
    
    public abstract Arena getArenaWithMonster(Entity e);
    
    public abstract Arena getArenaWithPet(Entity e);
    
    public abstract Arena getArenaWithName(String configName);
    
    
    
    /*/////////////////////////////////////////////////////////////////////////
    //
    //      Initialization
    //
    /////////////////////////////////////////////////////////////////////////*/
    
    public abstract void initialize();

    /**
     * Load the global settings.
     */
    public abstract void loadSettings();
    
    /**
     * Load all class-related stuff.
     */
    public abstract void loadClasses();
    
    /**
     * Load all arena-related stuff.
     */
    public abstract void loadArenas();
    
    public abstract Arena createArenaNode(String configName, World world);
    
    public abstract void removeArenaNode(String configName);
    
    
    
    /*/////////////////////////////////////////////////////////////////////////
    //
    //      Manipulation
    //
    /////////////////////////////////////////////////////////////////////////*/
    
    public boolean joinArena(Player p, String arenaName) {
        List<Arena> arenas = getEnabledArenas();
        if (!enabled || arenas.size() < 1)
        {
            MAUtils.tellPlayer(p, Msg.JOIN_NOT_ENABLED);
            return true;
        }
        
        // Grab the arena to join
        Arena arena = arenas.size() == 1 ? arenas.get(0) : getArenaWithName(arenaName);
        
        // Run a couple of basic sanity checks
        if (!sanityChecks(p, arena, arenaName, arenas))
            return true;
        
        // Run a bunch of per-arena sanity checks
        if (!arena.canJoin(p))
            return true;
        
        // If player is in a boat/minecart, eject!
        if (p.isInsideVehicle())
            p.leaveVehicle();
        
        // Take entry fee and store inventory
        arena.takeFee(p);
        if (!arena.emptyInvJoin) MAUtils.storeInventory(p);
        
        // If player is in a bed, unbed!
        if (p.isSleeping())
        {
            p.kickPlayer("Banned for life... Nah, just don't join from a bed ;)");
            return true;
        }
        
        // Join the arena!
        arena.playerJoin(p, p.getLocation());
        
        MAUtils.tellPlayer(p, Msg.JOIN_PLAYER_JOINED);
        if (!arena.entryFee.isEmpty())
            MAUtils.tellPlayer(p, Msg.JOIN_FEE_PAID.get(MAUtils.listToString(arena.entryFee, plugin)));
        if (arena.hasPaid.contains(p))
            arena.hasPaid.remove(p);
        
        return true;
    }
    
    public boolean leaveArena(Player p) {
        if (!arenaMap.containsKey(p))
        {
            Arena arena = getArenaWithSpectator(p);
            if (arena != null)
            {            
                arena.playerLeave(p);
                MAUtils.tellPlayer(p, Msg.LEAVE_PLAYER_LEFT);
                return true;
            }
            
            MAUtils.tellPlayer(p, Msg.LEAVE_NOT_PLAYING);
            return true;
        }
        
        Arena arena = arenaMap.get(p);            
        arena.playerLeave(p);
        MAUtils.tellPlayer(p, Msg.LEAVE_PLAYER_LEFT);
        return true;
    }
    
    public boolean spectateArena(Player p, String arenaName) {
        List<Arena> arenas = getEnabledArenas();
        if (!enabled || arenas.size() < 1)
        {
            MAUtils.tellPlayer(p, Msg.JOIN_NOT_ENABLED);
            return true;
        }

        // Grab the arena to join
        Arena arena = arenas.size() == 1 ? arenas.get(0) : getArenaWithName(arenaName);

        // Run a couple of basic sanity checks
        if (!sanityChecks(p, arena, arenaName, arenas))
            return true;

        // Run a bunch of arena-specific sanity-checks
        if (!arena.canSpec(p))
            return true;
        
        // If player is in a boat/minecart, eject!
        if (p.isInsideVehicle())
            p.leaveVehicle();
        
        // If player is in a bed, unbed!
        if (p.isSleeping())
        {
            p.kickPlayer("Banned for life... Nah, just don't join from a bed ;)");
            return true;
        }
        
        // Spectate the arena!
        arena.playerSpec(p, p.getLocation());
        
        MAUtils.tellPlayer(p, Msg.SPEC_PLAYER_SPECTATE);
        return true;
    }

    
    private boolean sanityChecks(Player p, Arena arena, String arenaName, List<Arena> arenas)
    {
        if (arenas.size() > 1 && arenaName.isEmpty())
            MAUtils.tellPlayer(p, Msg.JOIN_ARG_NEEDED);
        else if (arena == null)
            MAUtils.tellPlayer(p, Msg.ARENA_DOES_NOT_EXIST);
        else if (arenaMap.containsKey(p) && (arenaMap.get(p).arenaPlayers.contains(p) || arenaMap.get(p).lobbyPlayers.contains(p)))
            MAUtils.tellPlayer(p, Msg.JOIN_IN_OTHER_ARENA);
        else
            return true;
        
        return false;
    }
    
    
    
    /*/////////////////////////////////////////////////////////////////////////
    //
    //      Update and serialization methods
    //
    /////////////////////////////////////////////////////////////////////////*/
    
    /**
     * Update one, two or all three of global settings, classes
     * and arenas (arenas with deserialization).
     */
    public abstract void update(boolean settings, boolean classes, boolean arenalist);
    
    /**
     * Serialize the global settings.
     */
    public abstract void serializeSettings();
    
    /**
     * Serialize all arena configs.
     */
    public abstract void serializeArenas();
    
    /**
     * Deserialize all arena configs. Updates the arena list to
     * include only the current arenas (not ones added in the
     * actual file) that are also in the config-file.
     */
    public abstract void deserializeArenas();
    
    public abstract void updateSettings();
    public abstract void updateClasses();
    public abstract void updateArenas();
    public abstract void updateAll();
}
