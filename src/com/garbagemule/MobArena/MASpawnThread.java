package com.garbagemule.MobArena;

import java.util.List;
import java.util.Random;
import org.bukkit.Location;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Player;
import org.bukkit.entity.Creature;
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
// TO-DO: Allow custom special wave modulus.
// TO-DO: Allow custom special wave monsters.
// TO-DO: Allow additional "default" waves.
// TO-DO: 
public class MASpawnThread implements Runnable
{
    private int wave, noOfSpawnPoints, noOfPlayers;
    private int dZombies, dSkeletons, dSpiders, dCreepers;
    private Random random;
    private Player target;
    private Object[] playerArray;
    private String reward, currentRewards;
    
    public MASpawnThread()
    {
        // Turn the set into an array for lookup with random numbers.
        playerArray = ArenaManager.playerSet.toArray();
        noOfPlayers = playerArray.length;
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
            ran = random.nextInt(noOfPlayers);
            Creature c = (Creature) e;
            c.setTarget((Player)playerArray[ran]);
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
        boolean lightning = false;
        boolean slime     = false;
        
        switch (random.nextInt(4))
        {
            case 0:
                mob   = CreatureType.CREEPER;
                count = noOfPlayers * 3;
                lightning = true;
                break;
            case 1:
                mob   = CreatureType.PIG_ZOMBIE;
                count = noOfPlayers * 2;
                break;
            case 2:
                mob   = CreatureType.SLIME;
                count = noOfPlayers * 5;
                slime = true;
                break;
            case 3:
                mob   = CreatureType.MONSTER;
                count = Math.max(2, noOfPlayers);
                break;
            case 4:
                mob   = CreatureType.GHAST;
                count = Math.max(1, noOfPlayers - 2);
                break;
            default:
                mob   = CreatureType.CHICKEN;
                count = 50;
                break;
        }
        
        for (int i = 0; i < count; i++)
        {
            loc = ArenaManager.spawnpoints.get(i % noOfSpawnPoints);
            
            LivingEntity e = ArenaManager.world.spawnCreature(loc,mob);
            ArenaManager.monsterSet.add(e);
            
            if (slime) ((Slime)e).setSize(2);
            
            // Slimes can't have targets, apparently.
            if (!(e instanceof Creature))
                continue;
            
            // Grab a random target.
            ran = random.nextInt(noOfPlayers);
            Creature c = (Creature) e;
            c.setTarget((Player)playerArray[ran]);
        }
        
        // Lightning, just for effect ;)
        for (Location l : ArenaManager.spawnpoints)
        {
            ArenaManager.world.strikeLightningEffect(l);
        }
    }
}