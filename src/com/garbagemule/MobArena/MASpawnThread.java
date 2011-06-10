package com.garbagemule.MobArena;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import org.bukkit.Location;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Player;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.CreatureType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * Core class for handling wave spawning.
 * Currently, every 4th wave is a special wave, and all other waves
 * are default waves. The distribution coefficients are used to spread
 * out the distribution of each default monster however the server
 * host chooses. It is possible to create default waves that consist of
 * only one type of monster, or ones that have no creepers, for example.
 */
// TO-DO: Allow custom special wave interval.
// TO-DO: Allow custom special wave monsters.
// TO-DO: Allow additional "default" waves.
public class MASpawnThread implements Runnable
{
    private int wave, ran, noOfSpawnPoints, noOfPlayers;
    private int dZombies, dSkeletons, dSpiders, dCreepers, dWolves;
    private int dPoweredCreepers, dPigZombies, dSlimes, dMonsters, dAngryWolves, dGiants, dGhasts;
    private Random random;
    private String reward, currentRewards;
    
    public MASpawnThread()
    {
        noOfPlayers = ArenaManager.playerSet.size();
        noOfSpawnPoints = ArenaManager.spawnpoints.size();
        wave = 1;
        random = new Random();
        
        // Set up the distribution variables for the random spawner.
        dZombies   = ArenaManager.dZombies;
        dSkeletons = dZombies   + ArenaManager.dSkeletons;
        dSpiders   = dSkeletons + ArenaManager.dSpiders;
        dCreepers  = dSpiders   + ArenaManager.dCreepers;
        dWolves    = dCreepers  + ArenaManager.dWolves;
        
        dPoweredCreepers = ArenaManager.dPoweredCreepers;
        dPigZombies      = dPoweredCreepers + ArenaManager.dPigZombies;
        dSlimes          = dPigZombies      + ArenaManager.dSlimes;
        dMonsters        = dSlimes          + ArenaManager.dMonsters;
        dAngryWolves     = dMonsters        + ArenaManager.dAngryWolves;
        dGiants          = dAngryWolves     + ArenaManager.dGiants;
        dGhasts          = dGiants          + ArenaManager.dGhasts;
    }
    
    public void run()
    {
        // Check if we need to grant more rewards with the recurrent waves.
        for (Integer i : ArenaManager.everyWaveMap.keySet())
        {
            if (wave % i != 0)
                continue;
                
            for (Player p : ArenaManager.playerSet)
            {                
                currentRewards = ArenaManager.rewardMap.get(p);
                reward = MAUtils.getRandomReward(ArenaManager.everyWaveMap.get(i));
                currentRewards += reward + ",";
                ArenaManager.rewardMap.put(p, currentRewards);
                ArenaManager.tellPlayer(p, "You just earned a reward: " + reward);
            }
        }
        
        // Same deal, this time with the one-time waves.
        if (ArenaManager.afterWaveMap.containsKey(wave))
        {
            for (Player p : ArenaManager.playerSet)
            {
                currentRewards = ArenaManager.rewardMap.get(p);
                reward = MAUtils.getRandomReward(ArenaManager.afterWaveMap.get(wave));
                currentRewards += reward + ",";
                ArenaManager.rewardMap.put(p, currentRewards);
                ArenaManager.tellPlayer(p, "You just earned a reward: " + reward);
            }
        }
        
        // Check if this is a special wave.
        // TO-DO: Get this value from the config-file.
        // System.out.println("Spawning wave #" + wave);
        if (wave % 4 == 0)
        {
            ArenaManager.tellAll("Get ready for wave #" + wave + "! [SPECIAL]");
            specialWave();
        }
        else
        {
            ArenaManager.tellAll("Get ready for wave #" + wave + "!");
            defaultWave();
        }
        
        wave++;
    }
    
    /**
     * Spawns a default wave of monsters.
     */
    private void defaultWave()
    {
        Map<Location, Integer> weightedSpawnPoints = getWeightedSpawnPoints(wave + noOfPlayers);
        for (Map.Entry<Location, Integer> entry : weightedSpawnPoints.entrySet()) {
        	Location loc = entry.getKey();
        	int numToSpawn = entry.getValue();
        	
        	for (int i = 0; i < numToSpawn; i++) {
                ran = random.nextInt(dWolves);
                CreatureType mob;
                
                /* Because of the nature of the if-elseif-else statement,
                 * we're able to evaluate the random number in this way.
                 * If dSpiders = 0, then dSpiders = dSkeletons, which
                 * means if the random number is below that value, we will
                 * spawn a skeleton and break out of the statement. */
                if      (ran < dZombies)   mob = CreatureType.ZOMBIE;
                else if (ran < dSkeletons) mob = CreatureType.SKELETON;
                else if (ran < dSpiders)   mob = CreatureType.SPIDER;
                else if (ran < dCreepers)  mob = CreatureType.CREEPER;
                else if (ran < dWolves)    mob = CreatureType.WOLF;
                else continue;
                
                LivingEntity e = ArenaManager.world.spawnCreature(loc,mob);
                ArenaManager.monsterSet.add(e);
                
                //if (mob == CreatureType.WOLF)
                //    ((Wolf)e).setAngry(true);
                
                // Grab a random target.
                Creature c = (Creature) e;
                c.setTarget(getClosestPlayer(e));
        	}
        }
    }
    
    /**
     * Spawns a special wave of monsters.
     */
    private void specialWave()
    {
        CreatureType mob;
        ran = random.nextInt(dGhasts);
        
        int count;
        boolean slime = false;
        boolean wolf  = false;
        boolean ghast = false;
        
        if      (ran < dPoweredCreepers) mob = CreatureType.CREEPER;
        else if (ran < dPigZombies)      mob = CreatureType.PIG_ZOMBIE;
        else if (ran < dSlimes)          mob = CreatureType.SLIME;
        else if (ran < dMonsters)        mob = CreatureType.MONSTER;
        else if (ran < dAngryWolves)     mob = CreatureType.WOLF;
        else if (ran < dGiants)          mob = CreatureType.GIANT;
        else if (ran < dGhasts)          mob = CreatureType.GHAST;
        else return;
        
        // 5 on purpose - Ghasts act weird in Overworld.
        switch (mob)
        //switch (random.nextInt(5))
        {
            case CREEPER:
                //mob   = CreatureType.CREEPER;
                count = noOfPlayers * 3;
                break;
            case PIG_ZOMBIE:
                //mob   = CreatureType.PIG_ZOMBIE;
                count = noOfPlayers * 2;
                break;
            case SLIME:
                //mob   = CreatureType.SLIME;
                count = noOfPlayers * 4;
                slime = true;
                break;
            case MONSTER:
                //mob   = CreatureType.MONSTER;
                count = noOfPlayers + 1;
                break;
            case WOLF:
                //mob   = CreatureType.WOLF;
                count = noOfPlayers * 3;
                wolf  = true;
                break;
            case GIANT:
                //mob   = CreatureType.GIANT;
                count = 1;
                break;
            case GHAST:
                //mob   = CreatureType.GHAST;
                count = 2;
                ghast = true;
                break;
            default:
                //mob   = CreatureType.CHICKEN;
                count = 50;
                break;
        }
        
        Map<Location, Integer> weightedSpawnPoints = getWeightedSpawnPoints(count);
        for (Map.Entry<Location, Integer> entry : weightedSpawnPoints.entrySet()) {
        	Location loc = entry.getKey();
        	int numToSpawn = entry.getValue();

        	// Spawn the hippie monsters.
        	for (int i = 0; i < numToSpawn; i++) {
	            LivingEntity e = ArenaManager.world.spawnCreature(loc,mob);
	            if (!ArenaManager.monsterSet.contains(e))
	                ArenaManager.monsterSet.add(e);
	            else
	                System.out.println("MASpawnThread - monsterSet contains this entity");
	            
	            if (slime) ((Slime)e).setSize(2);
	            if (wolf)  ((Wolf)e).setAngry(true);
	            if (ghast) ((Ghast)e).setHealth(Math.min(noOfPlayers*25, 200));
	            
	            // Slimes can't have targets, apparently.
	            if (!(e instanceof Creature))
	                continue;
	            
	            // Grab a random target.
	            Creature c = (Creature) e;
	            c.setTarget(getClosestPlayer(e));
        	}
        }
        
        // Lightning, just for effect ;)
        for (Location spawn : ArenaManager.spawnpoints)
        {
            ArenaManager.world.strikeLightningEffect(spawn);
        }
    }
    
    /**
     * Gets the player closest to the input entity. ArrayList implementation
     * means a complexity of O(n).
     */
    // TO-DO: Move this into MAUtils
    public static Player getClosestPlayer(Entity e)
    {
        // Grab the coordinates.
        double x = e.getLocation().getX();
        double y = e.getLocation().getY();
        double z = e.getLocation().getZ();
        
        // Set up the comparison variable and the result.
        double current = Double.POSITIVE_INFINITY;
        Player result = null;
        
        /* Iterate through the ArrayList, and update current and result every
         * time a squared distance smaller than current is found. */
        for (Player p : ArenaManager.playerSet)
        {
            double dist = distance(p.getLocation(), x, y, z);
            if (dist < current)
            {
                current = dist;
                result = p;
            }
        }
        return result;
    }
    
    /**
     * Calculates the squared distance between locations.
     */
    // TO-DO: Move this into MAUtils
    private static double distance(Location loc, double d1, double d2, double d3)
    {
        double d4 = loc.getX() - d1;
        double d5 = loc.getY() - d2;
        double d6 = loc.getZ() - d3;
        
        return d4*d4 + d5*d5 + d6*d6;
    }

    /**
     * Get the number of players in aggro range of a specific location.
     * @param l The location to check
     * @return The number of players in aggro range
     */
    private int getNumPlayersInAggroRangeOf(Location l) {
    	final int aggroRange = 16;
    	int locX = l.getBlockX();
    	int locY = l.getBlockY();
    	int locZ = l.getBlockZ();

    	int playersInRange = 0;

    	// Loop each player and check if they are in aggro range
        for (Player p : ArenaManager.playerSet) {
        	int diffX = Math.abs(locX - p.getLocation().getBlockX());
        	int diffY = Math.abs(locY - p.getLocation().getBlockY());
        	int diffZ = Math.abs(locZ - p.getLocation().getBlockZ());
        	if (diffX < aggroRange && diffY < aggroRange && diffZ < aggroRange) {
        		playersInRange++;
        	}
        }
        return playersInRange;
    }

    /**
     * Weight the list of spawn points 
     * @param mobsToSpawn
     * @return A map of locations with the number of mobs to spawn
     */
    private Map<Location, Integer> getWeightedSpawnPoints(int mobsToSpawn) {
    	//System.out.println("Weighting spawn points. Attempting to set locations for " + mobsToSpawn + " monsters.");
    	int remainingMobsCount = mobsToSpawn;
    	Map<Location, Integer> weightedSpawns = new HashMap<Location, Integer>();
    	Map<Location, Integer> spawnToPlayers = new HashMap<Location, Integer>();
    	// First, work out how many candidate spawn points there are (ie. ones with people in range)
    	// Also keep a running total of the number of players in range as some people may be in range
    	// of multiple spawn points.
    	int totalPlayersInRange = 0;
    	for (Location l : ArenaManager.spawnpoints) {
    		int playersInRange = getNumPlayersInAggroRangeOf(l);
    		if (playersInRange > 0) {
    			spawnToPlayers.put(l, playersInRange);
    			totalPlayersInRange += playersInRange;
    		}
    	}
    	// Convert the number of players in aggro range of each spawn point to a percentage
    	// of overall spawn point coverage, then allocate that percentage of mobs to the loc
    	for (Map.Entry<Location, Integer> entry : spawnToPlayers.entrySet()) {
    		Location l = entry.getKey();
    		double ratio = (double) entry.getValue() / (double) totalPlayersInRange;
    		//System.out.println("This Spawn: " + entry.getValue() + " Total Players: " + totalPlayersInRange + " ratio: " + ratio);
    		int mobsForThisSpawn =  (int) Math.abs(ratio * mobsToSpawn);
    		weightedSpawns.put(l, mobsForThisSpawn);
    		//System.out.println("Allocated " + mobsForThisSpawn);
    		remainingMobsCount -= mobsForThisSpawn;
    	}

    	// Now, it's possible we have un-allocated mobs to spawn. This can either happen if
    	// no player is in range of any spawn point, or due to a remainder from the weighted allocation.
    	//System.out.println("Remaining mob spawns to allocate: " + remainingMobsCount);
    	// First try to put any remaining mobs into a spawn point that already has allocation
    	for (Map.Entry<Location, Integer> entry : spawnToPlayers.entrySet()) {
    		Location l = entry.getKey();
    		if (weightedSpawns.containsKey(l)) {
    			weightedSpawns.put(l, weightedSpawns.get(l) + remainingMobsCount);
    		} else {
    			weightedSpawns.put(l, remainingMobsCount);
    		}
    		remainingMobsCount = 0;
    		break;
    	}
    	// No spawn points had players in range, distribute the remaining mobs out to
    	// each spawn point evenly.
    	for (int i = remainingMobsCount; i > 0; i--) {
    		Location l = ArenaManager.spawnpoints.get(i % noOfSpawnPoints);
    		if (weightedSpawns.containsKey(l)) {
    			weightedSpawns.put(l, weightedSpawns.get(l) + 1);
    		} else {
    			weightedSpawns.put(l, 1);
    		}
    	}
    	return weightedSpawns;
    }
}