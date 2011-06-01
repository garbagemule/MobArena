package com.garbagemule.MobArena;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import org.bukkit.Location;
import org.bukkit.entity.Wolf;
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
    private int wave, noOfSpawnPoints, noOfPlayers;
    private int dZombies, dSkeletons, dSpiders, dCreepers;
    private Random random;
    private Player target;
    private String reward, currentRewards;
    // TO-DO: Move this into MAUtils
    private static List<Player> playerList;
    
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
    }
    
    public void run()
    {        
        // Check if we need to grant more rewards with the recurrent waves.
        for (Integer i : ArenaManager.everyWaveMap.keySet())
        {
            if (wave % i != 0)
                continue;
                
            for (Player p : playerList)
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
            for (Player p : playerList)
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
        Location loc;
        int ran;
        
        for (int i = 0; i < wave + noOfPlayers; i++)
        {
            loc = ArenaManager.spawnpoints.get(i % noOfSpawnPoints);
            ran = random.nextInt(dCreepers);
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
            else continue;
            
            LivingEntity e = ArenaManager.world.spawnCreature(loc,mob);
            ArenaManager.monsterSet.add(e);
            
            // Grab a random target.
            Creature c = (Creature) e;
            c.setTarget(getClosestPlayer(e));
        }
    }
    
    /**
     * Spawns a special wave of monsters.
     */
    private void specialWave()
    {
        Location loc;
        CreatureType mob;
        
        int ran, count;
        boolean slime = false;
        boolean wolf  = false;
        
        // 5 on purpose - Ghasts act weird in Overworld.
        switch (random.nextInt(5))
        {
            case 0:
                mob   = CreatureType.CREEPER;
                count = noOfPlayers * 3;
                break;
            case 1:
                mob   = CreatureType.PIG_ZOMBIE;
                count = noOfPlayers * 2;
                break;
            case 2:
                mob   = CreatureType.SLIME;
                count = noOfPlayers * 4;
                slime = true;
                break;
            case 3:
                mob   = CreatureType.MONSTER;
                count = noOfPlayers + 1;
                break;
            case 4:
                mob   = CreatureType.WOLF;
                count = noOfPlayers * 2;
                wolf  = true;
                break;
            case 5:
                mob   = CreatureType.GHAST;
                count = Math.max(1, noOfPlayers - 2);
                break;
            default:
                mob   = CreatureType.CHICKEN;
                count = 50;
                break;
        }
        
        // Spawn the hippie monsters.
        for (int i = 0; i < count; i++)
        {
            loc = ArenaManager.spawnpoints.get(i % noOfSpawnPoints);
            
            LivingEntity e = ArenaManager.world.spawnCreature(loc,mob);
            ArenaManager.monsterSet.add(e);
            
            if (slime) ((Slime)e).setSize(2);
            if (wolf)  ((Wolf)e).setAngry(true);
            
            // Slimes can't have targets, apparently.
            if (!(e instanceof Creature))
                continue;
            
            // Grab a random target.
            Creature c = (Creature) e;
            c.setTarget(getClosestPlayer(e));
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
}