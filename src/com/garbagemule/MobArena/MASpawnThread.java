package com.garbagemule.MobArena;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.bukkit.Location;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Player;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import com.garbagemule.MobArena.MAMessages.Msg;
import com.garbagemule.MobArena.util.WaveUtils;
import com.garbagemule.MobArena.waves.Wave;

/**
 * Core class for handling wave spawning.
 * Currently, every 4th wave is a special wave, and all other waves
 * are default waves. The distribution coefficients are used to spread
 * out the distribution of each default monster however the server
 * host chooses. It is possible to create default waves that consist of
 * only one type of monster, or ones that have no creepers, for example.
 */
public class MASpawnThread implements Runnable
{
    private MobArena plugin;
    private Arena arena;
    private int wave, taskId, previousSize, playerCount;
    
    // NEW WAVES
    private Wave defaultWave;
    private TreeSet<Wave> recurrentWaves;
    private TreeSet<Wave> singleWaves;
    
    public MASpawnThread(MobArena plugin, Arena arena)
    {
    	// WAVES
        defaultWave    = arena.recurrentWaves.first();
    	recurrentWaves = arena.recurrentWaves;
    	singleWaves    = new TreeSet<Wave>(arena.singleWaves);
    	
        this.plugin  = plugin;
        this.arena   = arena;
        wave         = 1;
        playerCount  = arena.arenaPlayers.size();
    }
    
    public void run()
    {
        // Clear out all dead monsters in the monster set.
        removeDeadMonsters();
        
        // If there are no players in the arena, return.
        if (arena.arenaPlayers.isEmpty())
            return;

        // Check if wave needs to be cleared first. If so, return!
        if (arena.waveClear && wave > 1 && !arena.monsters.isEmpty())
            return;
        
        // Grant rewards (if any) for this wave
        grantRewards(wave);
        
        // Detonate creepers if needed
        detonateCreepers(arena.detCreepers);
        
        // Find the wave to spawn
        spawnWave(wave);
        
        wave++;
        if (arena.monsters.isEmpty())
            arena.resetIdleTimer();
    }
    
    private void removeDeadMonsters()
    {
        List<Entity> tmp = new LinkedList<Entity>(arena.monsters);
        for (Entity e : tmp)
            if (e.isDead())
                arena.monsters.remove(e);
    }
    
    private void grantRewards(int wave)
    {
        for (Map.Entry<Integer,List<ItemStack>> entry : arena.everyWaveMap.entrySet())
            if (wave % entry.getKey() == 0)
                addReward(entry.getValue());

        if (arena.afterWaveMap.containsKey(wave))
            addReward(arena.afterWaveMap.get(wave));
    }
    
    private void spawnWave(int wave)
    {    	
        Wave w = null;
        
        // Check the first element of the single waves.
        if (!singleWaves.isEmpty() && singleWaves.first().matches(wave))
        {
            w = singleWaves.pollFirst();
        }
        else
        {
            SortedSet<Wave> matches = getMatchingRecurrentWaves(wave);
            w = matches.isEmpty() ? defaultWave : matches.last();
        }
        
        // Notify listeners.
        for (MobArenaListener listener : plugin.getAM().listeners)
            listener.onWave(arena, wave, w.getName(), w.getBranch(), w.getType());
        
        w.spawn(wave);
    }
    
    private SortedSet<Wave> getMatchingRecurrentWaves(int wave)
    {
        TreeSet<Wave> result = new TreeSet<Wave>(WaveUtils.getRecurrentComparator());
        
        for (Wave w : recurrentWaves)
        {
            if (w.matches(wave))
                result.add(w);
        }
        
        return result;
    }
    

    
    /*////////////////////////////////////////////////////////////////////
    //
    //      Getters/setters
    //
    ////////////////////////////////////////////////////////////////////*/
    
    public int getWave()
    {
        return wave;
    }
    
    public int getTaskId()
    {
        return taskId;
    }
    
    public int getPreviousSize()
    {
        return previousSize;
    }
    
    public int getPlayerCount()
    {
        return playerCount;
    }
    
    public void setTaskId(int taskId)
    {
        this.taskId = taskId;
    }
    
    public void setPreviousSize(int previousSize)
    {
        this.previousSize = previousSize;
    }
    
    /**
     * Rewards all players with an item from the input String.
     */
    private void addReward(List<ItemStack> rewards)
    {
        for (Player p : arena.arenaPlayers)
        {
            if (arena.log.players.get(p) == null)
                continue;
            
            ItemStack reward = MAUtils.getRandomReward(rewards);
            arena.log.players.get(p).rewards.add(reward);
            
            if (reward == null)
            {
                MAUtils.tellPlayer(p, "ERROR! Problem with economy rewards. Notify server host!");
                MobArena.warning("Could not add null reward. Please check the config-file!");
            }
            else if (reward.getTypeId() == MobArena.ECONOMY_MONEY_ID)
            {
                if (plugin.Methods.hasMethod())
                    MAUtils.tellPlayer(p, Msg.WAVE_REWARD, plugin.Method.format(reward.getAmount()));
                else MobArena.warning("Tried to add money, but no economy plugin detected!");
            }
            else
            {
                MAUtils.tellPlayer(p, Msg.WAVE_REWARD, MAUtils.toCamelCase(reward.getType().toString()) + ":" + reward.getAmount());
            }
        }
    }
    
    /**
     * "Detonates" all the Creepers in the monsterSet.
     */
    public void detonateCreepers(boolean really)
    {
        if (!really)
            return;
        
        Set<Entity> tmp = new HashSet<Entity>();
        for (Entity e : arena.monsters)
        {
            if (!(e instanceof Creeper) || e.isDead())
                continue;
            
            tmp.add(e);
        }

        Location loc;
        for (Entity e : tmp)
        {
            arena.monsters.remove(e);
            loc = e.getLocation().getBlock().getRelative(0,2,0).getLocation();
            arena.world.createExplosion(loc, 2);
            e.remove();
        }
    }
    
    /**
     * Update the targets of all monsters, if their targets aren't alive.
     */
    public void updateTargets()
    {
        Creature c;
        Entity target;
        for (Entity e : arena.monsters)
        {
            if (!(e instanceof Creature))
                continue; 

            // TODO: Remove the try-catch when Bukkit API is fixed.
            c = (Creature) e;
            try { target = c.getTarget(); } catch (ClassCastException cce) { continue; }
            
            if (target instanceof Player && arena.arenaPlayers.contains(target))
                continue;
            
            c.setTarget(MAUtils.getClosestPlayer(e, arena));
        }
    }
}