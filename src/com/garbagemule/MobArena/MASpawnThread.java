package com.garbagemule.MobArena;

import java.util.Random;
import org.bukkit.Location;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Player;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.CreatureType;

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
    private int wave, ran, noOfSpawnPoints, noOfPlayers, modulo;
    private int dZombies, dSkeletons, dSpiders, dCreepers, dWolves;
    private int dPoweredCreepers, dPigZombies, dSlimes, dMonsters, dAngryWolves, dGiants, dGhasts;
    private Random random;
    private String reward, currentRewards;
    
    public MASpawnThread()
    {
        modulo = ArenaManager.specialModulo;
        if (modulo <= 0) modulo = -32768;
        
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
        if (wave % modulo == 0)
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
        
        for (int i = 0; i < wave + noOfPlayers; i++)
        {
            loc = ArenaManager.spawnpoints.get(i % noOfSpawnPoints);
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
        ran = random.nextInt(dGhasts);
        
        int count;
        boolean slime   = false;
        boolean wolf    = false;
        boolean ghast   = false;
        boolean creeper = false;
        
        if      (ran < dPoweredCreepers) mob = CreatureType.CREEPER;
        else if (ran < dPigZombies)      mob = CreatureType.PIG_ZOMBIE;
        else if (ran < dSlimes)          mob = CreatureType.SLIME;
        else if (ran < dMonsters)        mob = CreatureType.MONSTER;
        else if (ran < dAngryWolves)     mob = CreatureType.WOLF;
        else if (ran < dGiants)          mob = CreatureType.GIANT;
        else if (ran < dGhasts)          mob = CreatureType.GHAST;
        else return;
        
        // 5 on purpose - Ghasts act weird in Overworld.
        switch(mob)
        {
            case CREEPER:
                count = noOfPlayers * 3;
                creeper = true;
                break;
            case PIG_ZOMBIE:
                count = noOfPlayers * 2;
                break;
            case SLIME:
                count = noOfPlayers * 4;
                slime = true;
                break;
            case MONSTER:
                count = noOfPlayers + 1;
                break;
            case WOLF:
                count = noOfPlayers * 3;
                wolf  = true;
                break;
            case GIANT:
                count = 1;
                break;
            case GHAST:
                count = 2;
                ghast = true;
                break;
            default:
                count = 50;
                break;
        }
        
        // Spawn the hippie monsters.
        for (int i = 0; i < count; i++)
        {
            loc = ArenaManager.spawnpoints.get(i % noOfSpawnPoints);
            
            LivingEntity e = ArenaManager.world.spawnCreature(loc,mob);
            if (!ArenaManager.monsterSet.contains(e))
                ArenaManager.monsterSet.add(e);
            else
                System.out.println("MASpawnThread - monsterSet contains this entity");
            
            if (slime)   ((Slime)e).setSize(2);
            if (wolf)    ((Wolf)e).setAngry(true);
            if (ghast)   ((Ghast)e).setHealth(Math.min(noOfPlayers*25, 200));
            if (creeper) ((Creeper)e).setPowered(true);
            
            // Slimes can't have targets, apparently.
            if (!(e instanceof Creature))
                continue;
            
            // Grab a random target.
            Creature c = (Creature) e;
            c.setTarget(getClosestPlayer(e));
        }
        
        if (!ArenaManager.lightning)
            return;
            
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