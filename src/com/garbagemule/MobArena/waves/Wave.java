package com.garbagemule.MobArena.waves;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.garbagemule.MobArena.Arena;
import com.garbagemule.MobArena.util.WaveUtils;

public interface Wave
{
    public enum WaveBranch
    {
        SINGLE, RECURRENT;
    }
    
    public enum WaveType
    {
        DEFAULT, SPECIAL, SWARM, BOSS;

        public static WaveType fromString(String string)
        {
            return WaveUtils.getEnumFromString(WaveType.class, string);
        }
    }
    
    public enum WaveGrowth
    {
        OLD(0), SLOW(0.5), MEDIUM(0.65), FAST(0.8), PSYCHO(1.1);
        private double exp;
        
        private WaveGrowth(double exp)
        {
            this.exp = exp;
        }
        
        public static WaveGrowth fromString(String string)
        {
            return WaveUtils.getEnumFromString(WaveGrowth.class, string, OLD); 
        }
        
        public int getAmount(int wave, int playerCount)
        {
            if (this == OLD) return wave + playerCount;
            
            double pc = (double) playerCount;
            double w  = (double) wave;
            
            double base = Math.min(Math.ceil(pc/2) + 1, 13);
            return (int) ( base * Math.pow(w, exp) );
        }
    }
    
    public enum BossAbility
    {
        ARROWS, FIREBALLS, FIRE_AURA, THROW_TARGET, THROW_NEARBY, FETCH_TARGET, FETCH_DISTANT;
        
        public static BossAbility fromString(String string)
        {
            return WaveUtils.getEnumFromString(BossAbility.class, string);
        }
        
        public void activate(Arena arena, LivingEntity boss)
        {
            LivingEntity target = getTarget(boss);
            Location bLoc = boss.getLocation();
            Location loc;
            
            switch (this)
            {
                // Fire an arrow in the direction the boss is facing.
                case ARROWS:
                    System.out.println("Shooting arrow");
                    boss.shootArrow();
                    break;
                // Hurl a fireball in the direction the boss is facing.
                case FIREBALLS:
                    System.out.println("Shooting fireball");
                    loc = bLoc.add(bLoc.getDirection().normalize().multiply(2).toLocation(boss.getWorld(), bLoc.getYaw(), bLoc.getPitch()));
                    Fireball fireball = boss.getWorld().spawn(loc, Fireball.class);
                    fireball.setIsIncendiary(false);
                    break;
                // Set fire to all players nearby.
                case FIRE_AURA:
                    System.out.println("Fire aura");
                    for (Player p : getNearbyPlayers(arena, boss, 5))
                            p.setFireTicks(20);
                    break;
                // Throw target back
                case THROW_TARGET:
                    System.out.println("Throw target");
                    if (target != null)
                    {
                        loc = target.getLocation();
                        Vector v   = new Vector(loc.getX() - bLoc.getX(), 0, loc.getZ() - bLoc.getZ());
                        target.setVelocity(v.normalize().setY(0.8));
                    }
                    break;
                // Throw nearby players back
                case THROW_NEARBY:
                    System.out.println("Throw nearby");
                    for (Player p : getNearbyPlayers(arena, boss, 5))
                    {
                        loc      = p.getLocation();
                        Vector v = new Vector(loc.getX() - bLoc.getX(), 0, loc.getZ() - bLoc.getZ());
                        p.setVelocity(v.normalize().setY(0.8));
                    }
                    break;
                // Warp target to boss
                case FETCH_TARGET:
                    System.out.println("Fetch target");
                    if (target != null) target.teleport(boss);
                    break;
                // Warp nearby players to boss
                case FETCH_DISTANT:
                    System.out.println("Fetch distant");
                    for (Player p : getDistantPlayers(arena, boss, 8))
                        p.teleport(boss);
                default:
                    break;
            }
        }
        
        private LivingEntity getTarget(LivingEntity entity)
        {
            if (entity instanceof Creature)
            {
                LivingEntity target = ((Creature) entity).getTarget();
                if (target instanceof Player)
                    return target;
            }
            return null;
        }
        
        private List<Player> getNearbyPlayers(Arena arena, Entity boss, int x)
        {
            List<Player> result = new LinkedList<Player>();
            for (Entity e : boss.getNearbyEntities(x, x, x))
                if (arena.getLivingPlayers().contains(e))
                    result.add((Player) e);
            return result;
        }
        
        private List<Player> getDistantPlayers(Arena arena, Entity boss, int x)
        {
            List<Player> result = new LinkedList<Player>();
            for (Player p : arena.getLivingPlayers())
                if (p.getLocation().distanceSquared(boss.getLocation()) > x*x)
                    result.add(p);
            return result;
        }
    }
    
    public enum BossHealth
    {
        LOW(5), MEDIUM(8), HIGH(12), PSYCHO(20);
        private int multiplier;
        
        private BossHealth(int multiplier)
        {
            this.multiplier = multiplier;
        }
        
        public int getAmount(int playerCount)
        {
            return (playerCount + 1) * 20 * multiplier;
        }
        
        public static BossHealth fromString(String string)
        {
            return WaveUtils.getEnumFromString(BossHealth.class, string);
        }
    }
    
    public enum SwarmAmount
    {
        LOW(10), MEDIUM(20), HIGH(30), PSYCHO(50);
        private int multiplier;
        
        private SwarmAmount(int multiplier)
        {
            this.multiplier = multiplier;
        }
        
        public int getAmount(int playerCount)
        {
            return Math.max(1, playerCount / 2) * multiplier;
        }
        
        public static SwarmAmount fromString(String string)
        {
            return WaveUtils.getEnumFromString(SwarmAmount.class, string);
        }
    }

    /**
     * The spawn() method must spawn one or more monsters in
     * the arena. The monster count, damage, health, etc. can
     * be modified by the wave parameter.
     * @param wave Wave number
     */
    public void spawn(int wave);
    
    /**
     * Get the type of wave.
     * @return The WaveType of this Wave.
     */
    public WaveType getType();
    
    /**
     * Get the growth rate of this wave.
     * @return The growth rate
     */
    public WaveGrowth getGrowth();
    
    /**
     * Get the first wave number for this wave
     * @return The wave number
     */
    public int getWave();
    
    /**
     * Get the wave's frequency, i.e. wave number "modulo"
     * @return The wave's frequency
     */
    public int getFrequency();
    
    /**
     * Get the wave's priority value.
     * @return The priority
     */
    public int getPriority();
    
    /**
     * Get the wave's name.
     * @return The name
     */
    public String getName();
    
    /**
     * Get the arena of this wave.
     * @return The arena
     */
    public Arena getArena();

    /**
     * Set the wave's growth
     * @param growth How fast the wave will grow
     */
    public void setGrowth(WaveGrowth growth);
    
    /**
     * Check if this wave matches the wave number.
     * The SingleWave class does a simple check if its wave == the parameter.
     * The RecurrentWave class is more complex in that it needs to do some
     * calculations based on the initial wave and the frequency.
     * @param wave The current wave number
     * @return true, if the wave should spawn, false otherwise
     */
    public boolean matches(int wave);
}