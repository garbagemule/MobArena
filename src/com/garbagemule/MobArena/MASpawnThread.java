package com.garbagemule.MobArena;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

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
import org.bukkit.inventory.ItemStack;

import com.garbagemule.MobArena.MAMessages.Msg;

/**
 * Core class for handling wave spawning.
 * Currently, every 4th wave is a special wave, and all other waves
 * are default waves. The distribution coefficients are used to spread
 * out the distribution of each default monster however the server
 * host chooses. It is possible to create default waves that consist of
 * only one type of monster, or ones that have no creepers, for example.
 */
// TODO: Allow custom special wave monsters.
// TODO: Allow additional "default" waves.
public class MASpawnThread implements Runnable
{
    protected int wave, previousSize, taskId;
    private int ran, noOfPlayers, modulo;
    private int dZombies, dSkeletons, dSpiders, dCreepers, dWolves;
    private int dPoweredCreepers, dPigZombies, dSlimes, dMonsters, dAngryWolves, dGiants, dGhasts;
    private Random random;
    private MobArena plugin;
    private Arena arena;
    
    public MASpawnThread(MobArena plugin, Arena arena)
    {
        this.plugin = plugin;
        this.arena = arena;
        modulo = arena.specialModulo;
        if (modulo <= 0) modulo = -32768;
        
        taskId = -32768;
        
        noOfPlayers = arena.livePlayers.size();
        wave = 1;
        random = new Random();
        
        // Set up the distribution variables for the random spawner.
        // Note: Updating these means MAUtils.getArenaDistributions() must also be updated!
        dZombies   = arena.distDefault.get("zombies");
        dSkeletons = dZombies   + arena.distDefault.get("skeletons");
        dSpiders   = dSkeletons + arena.distDefault.get("spiders");
        dCreepers  = dSpiders   + arena.distDefault.get("creepers");
        dWolves    = dCreepers  + arena.distDefault.get("wolves");
        
        dPoweredCreepers = arena.distSpecial.get("powered-creepers");
        dPigZombies      = dPoweredCreepers + arena.distSpecial.get("zombie-pigmen");
        dSlimes          = dPigZombies      + arena.distSpecial.get("slimes");
        dMonsters        = dSlimes          + arena.distSpecial.get("humans");
        dAngryWolves     = dMonsters        + arena.distSpecial.get("angry-wolves");
        dGiants          = dAngryWolves     + arena.distSpecial.get("giants");
        dGhasts          = dGiants          + arena.distSpecial.get("ghasts");
    }
    
    public void run()
    {
        List<Entity> tmp = new LinkedList<Entity>(arena.monsters);
        for (Entity e : tmp)
            if (e.isDead())
                arena.monsters.remove(e);
        
        // Check if wave needs to be cleared first. If so, return!
        if (arena.waveClear && wave > 1)
        {            
            if (!arena.monsters.isEmpty())
                return;
        }

        // Check if we need to grant more rewards with the recurrent waves.
        for (Map.Entry<Integer,List<ItemStack>> entry : arena.everyWaveMap.entrySet())
            if (wave % entry.getKey() == 0)
                addReward(entry.getValue());

        // Same deal, this time with the one-time waves.
        if (arena.afterWaveMap.containsKey(wave))
            addReward(arena.afterWaveMap.get(wave));
        
        // Check if this is a special wave.
        if (wave % modulo == 0)
        {
            MAUtils.tellAll(arena, MAMessages.get(Msg.WAVE_SPECIAL, ""+wave));
            detonateCreepers(arena.detCreepers);
            specialWave();
            
            // Notify listeners.
            for (MobArenaListener listener : plugin.getAM().listeners)
                listener.onSpecialWave(wave, wave/modulo);
        }
        else
        {
            MAUtils.tellAll(arena, MAMessages.get(Msg.WAVE_DEFAULT, ""+wave));
            detonateCreepers(arena.detCreepers);
            defaultWave();
            
            // Notify listeners.
            for (MobArenaListener listener : plugin.getAM().listeners)
                listener.onDefaultWave(wave);
        }

        wave++;
        if (arena.maxIdleTime > 0 && arena.monsters.isEmpty()) arena.resetIdleTimer();
    }
    
    /**
     * Rewards all players with an item from the input String.
     */
    private void addReward(List<ItemStack> rewards)
    {
        for (Player p : arena.livePlayers)
        {
            ItemStack reward = MAUtils.getRandomReward(rewards);
            arena.rewardMap.get(p).add(reward);
            
            if (reward == null)
            {
                MAUtils.tellPlayer(p, "ERROR! Problem with economy rewards. Notify server host!");
                System.out.println("[MobArena] ERROR! Could not add null reward. Please check the config-file!");
            }
            else if (reward.getTypeId() == MobArena.ECONOMY_MONEY_ID)
            {
                if (plugin.Methods.hasMethod())
                    MAUtils.tellPlayer(p, MAMessages.get(Msg.WAVE_REWARD, plugin.Method.format(reward.getAmount())));
                else System.out.println("[MobArena] ERROR! No economy plugin detected!");
            }
            else
            {
                MAUtils.tellPlayer(p, MAMessages.get(Msg.WAVE_REWARD, MAUtils.toCamelCase(reward.getType().toString()) + ":" + reward.getAmount()));
            }
        }
    }
    
    /**
     * Spawns a default wave of monsters.
     */
    private void defaultWave()
    {
        Location loc;
        List<Location> spawnpoints = getValidSpawnpoints();
        int noOfSpawnpoints = spawnpoints.size();
        int count = wave + noOfPlayers;
        CreatureType mob;
            
        for (int i = 0; i < count; i++)
        {
            loc = spawnpoints.get(i % noOfSpawnpoints);
            ran = random.nextInt(dWolves);
            
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
            
            LivingEntity e = arena.world.spawnCreature(loc,mob);
            arena.monsters.add(e);
            
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
        List<Location> spawnpoints = getValidSpawnpoints();
        int noOfSpawnpoints = spawnpoints.size();
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
            loc = spawnpoints.get(i % noOfSpawnpoints);
            
            LivingEntity e = arena.world.spawnCreature(loc,mob);
            arena.monsters.add(e);
            
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
        
        if (!arena.lightning)
            return;
            
        // Lightning, just for effect ;)
        for (Location spawn : arena.spawnpoints.values())
            arena.world.strikeLightningEffect(spawn);
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
     * Get all the spawnpoints that have players nearby.
     */
    public List<Location> getValidSpawnpoints()
    {
        List<Location> result = new ArrayList<Location>();
        
        for (Location s : arena.spawnpoints.values())
        {
            for (Player p : arena.livePlayers)
            {
                if (!arena.world.equals(p.getWorld()))
                {
                    System.out.println("[MobArena] MASpawnThread:291: Player '" + p.getName() + "' is not in the right world. Force leaving...");
                    arena.playerLeave(p);
                    MAUtils.tellPlayer(p, "You warped out of the arena world.");
                    continue;
                }
                
                if (s.distanceSquared(p.getLocation()) > MobArena.MIN_PLAYER_DISTANCE)
                    continue;
                
                result.add(s);
                break;
            }
        }
        
        // If no players are in range, just use all the spawnpoints.
        if (result.isEmpty())
            result.addAll(arena.spawnpoints.values());
        
        return result;
    }
    
    /**
     * Get the player closest to the input entity.
     */
    // TODO: Move this into MAUtils
    public Player getClosestPlayer(Entity e)
    {
        // Set up the comparison variable and the result.
        double dist    = 0;
        double current = Double.POSITIVE_INFINITY;
        Player result = null;
        
        /* Iterate through the ArrayList, and update current and result every
         * time a squared distance smaller than current is found. */
        for (Player p : arena.livePlayers)
        {
            if (!arena.world.equals(p.getWorld()))
            {
                System.out.println("[MobArena] MASpawnThread:329: Player '" + p.getName() + "' is not in the right world. Force leaving...");
                arena.playerLeave(p);
                MAUtils.tellPlayer(p, "You warped out of the arena world.");
                continue;
            }
            dist = p.getLocation().distanceSquared(e.getLocation());
            //double dist = MAUtils.distance(p.getLocation(), e.getLocation());
            if (dist < current && dist < MobArena.MIN_PLAYER_DISTANCE)
            {
                current = dist;
                result = p;
            }
        }
        return result;
    }
    
    /**
     * Update the targets of all monsters, if their targets aren't alive.
     */
    public void updateTargets()
    {
        Creature c;
        LivingEntity target;
        for (Entity e : arena.monsters)
        {
            c = (Creature) e;
            target = c.getTarget();
            
            if (target instanceof Player && arena.livePlayers.contains(target))
                continue;
            
            c.setTarget(getClosestPlayer(e));
        }
    }
}