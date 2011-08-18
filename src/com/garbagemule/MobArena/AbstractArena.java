package com.garbagemule.MobArena;

import java.util.Map;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.garbagemule.ArenaPlugin.ArenaPlugin;

public abstract class AbstractArena implements ArenaInterface
{
    private String      name;
    private World       world;
    private ArenaPlugin plugin;
    
    private boolean enabled, setup, running;
    private Set<Player> arenaPlayers, lobbyPlayers, readyPlayers, specPlayers;
    private Map<Player,String> playerClassMap;
    private Map<Player,Location> locations;
    private Map<Player,Integer> healths;
    
    public AbstractArena(String name, World world, ArenaPlugin plugin)
    {
        if (world == null)
            throw new NullPointerException("[" + plugin.getClass().getSimpleName() + "] ERROR! World for arena '" + name + "' does not exist!");
        
        this.name   = name;
        this.world  = world;
        this.plugin = plugin;
    }

    public abstract void startArena();
    
    public abstract void endArena();

    public abstract boolean forceStart();

    public abstract boolean forceEnd();
/*
    public void playerJoin(Player p)
    {
        storePlayerData(p, p.getLocation());
        MAUtils.sitPets(p);
        p.setHealth(20);
        movePlayerToLobby(p);
    }
*/
    /*
    public void playerLeave(Player p)
    {
        if (arenaPlayers.contains(p) || lobbyPlayers.contains(p))
            finishArenaPlayer(p);
        
        movePlayerToEntry(p);
        discardPlayer(p);
        
        // End arena if possible.
        endArena();
    }*/
    

    public abstract void playerSpec(Player p);

    public abstract void playerDeath(Player p);

    public abstract void playerReady(Player p);

    public abstract boolean canJoin(Player p);

    public abstract boolean canLeave(Player p);

    public abstract boolean canSpec(Player p);

    public boolean isEnabled()
    {
        return enabled;
    }

    public boolean isSetup()
    {
        return setup;
    }

    public boolean isRunning()
    {
        return running;
    }
    
    public void storePlayerData(Player p, Location loc)
    {
        // TODO: Get this sorted out
        //plugin.getAM().arenaMap.put(p, this);
        
        if (!locations.containsKey(p))
            locations.put(p, loc);

        if (!healths.containsKey(p))
            healths.put(p, p.getHealth());
    }
    
    public abstract void movePlayerToLobby(Player p);
    
    public abstract void movePlayerToSpec(Player p);
    
    public void movePlayerToEntry(Player p)
    {
        Location entry = locations.get(p);
        if (entry == null) return;
        
        p.teleport(entry);
    }
    
    public abstract void restoreInvAndGiveRewards(final Player p);
}
