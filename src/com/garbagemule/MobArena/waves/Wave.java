package com.garbagemule.MobArena.waves;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
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
        OLD(0), SLOW(0.5), MEDIUM(0.65), FAST(0.8), PSYCHO(1.2);
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
        ARROWS("Arrow")
        {
            public void run(Arena arena, LivingEntity boss)
            {
                boss.shootArrow();
            }
        },
        FIREBALLS("Fireball")
        {
            public void run(Arena arena, LivingEntity boss)
            {
                Location bLoc = boss.getLocation();
                Location loc = bLoc.add(bLoc.getDirection().normalize().multiply(2).toLocation(boss.getWorld(), bLoc.getYaw(), bLoc.getPitch()));
                Fireball fireball = boss.getWorld().spawn(loc, Fireball.class);
                fireball.setIsIncendiary(false);
            }
        },
        FIREAURA("Fire aura")
        {
            public void run(Arena arena, LivingEntity boss)
            {
                for (Player p : getNearbyPlayers(arena, boss, 5))
                        p.setFireTicks(20);
            }
        },
        LIGHTNINGAURA("Lightning aura")
        {
            public void run(Arena arena, LivingEntity boss)
            {
                Location base = boss.getLocation();
                Location ne = base.getBlock().getRelative( 2,  0,  2).getLocation();
                Location nw = base.getBlock().getRelative(-2,  0,  2).getLocation();
                Location se = base.getBlock().getRelative( 2,  0, -2).getLocation();
                Location sw = base.getBlock().getRelative(-2,  0, -2).getLocation();

                arena.getWorld().strikeLightning(ne);
                arena.getWorld().strikeLightning(nw);
                arena.getWorld().strikeLightning(se);
                arena.getWorld().strikeLightning(sw);
            }
        },
        DISORIENTTARGET("Disorient target")
        {
            public void run(Arena arena, LivingEntity boss)
            {
                LivingEntity target = getTarget(boss);
                if (target == null) return;

                Location loc = target.getLocation();
                loc.setYaw(target.getLocation().getYaw() + 45 + (new Random()).nextInt(270));
                target.teleport(loc);
            }
        },
        ROOTTARGET("Root target")
        {
            public void run(final Arena arena, LivingEntity boss)
            {
                final LivingEntity target = getTarget(boss);
                if (target == null) return;
                
                final Location loc = target.getLocation();
                final int freezeTaskId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(arena.getPlugin(),
                    new Runnable()
                    {
                        public void run()
                        {
                            if (arena.getLivingPlayers().contains(target))
                                    target.teleport(loc);
                        }
                    }, 3, 3);
                
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(arena.getPlugin(),
                    new Runnable()
                    {
                        public void run()
                        {
                            Bukkit.getServer().getScheduler().cancelTask(freezeTaskId);
                        }
                    }, 45);
            }
        },
        WARPTOPLAYER("Warp to player")
        {
            public void run(Arena arena, LivingEntity boss)
            {
                List<Player> list = arena.getLivingPlayers();
                boss.teleport(list.get((new Random()).nextInt(list.size())));
            }
        },
        THROWTARGET("Throw target")
        {
            public void run(Arena arena, LivingEntity boss)
            {
                System.out.println("Throw target");
                LivingEntity target = getTarget(boss);
                if (target == null) return;
                
                Location bLoc       = boss.getLocation();
                Location loc        = target.getLocation();
                Vector v            = new Vector(loc.getX() - bLoc.getX(), 0, loc.getZ() - bLoc.getZ());
                target.setVelocity(v.normalize().setY(0.8));
            }
        },
        THROWNEARBY("Throw nearby players")
        {
            public void run(Arena arena, LivingEntity boss)
            {
                for (Player p : getNearbyPlayers(arena, boss, 5))
                {
                    Location bLoc = boss.getLocation();
                    Location loc  = p.getLocation();
                    Vector v      = new Vector(loc.getX() - bLoc.getX(), 0, loc.getZ() - bLoc.getZ());
                    p.setVelocity(v.normalize().setY(0.8));
                }
            }
        },
        THROWDISTANT("Throw distant players")
        {
            public void run(Arena arena, LivingEntity boss)
            {
                for (Player p : getDistantPlayers(arena, boss, 8))
                {
                    Location bLoc = boss.getLocation();
                    Location loc  = p.getLocation();
                    Vector v      = new Vector(loc.getX() - bLoc.getX(), 0, loc.getZ() - bLoc.getZ());
                    p.setVelocity(v.normalize().setY(0.8));
                }
            }
        },
        FETCHTARGET("Fetch target")
        {
            public void run(Arena arena, LivingEntity boss)
            {
                LivingEntity target = getTarget(boss);
                if (target != null) target.teleport(boss);
            }
        },
        FETCHNEARBY("Fetch nearby players")
        {
            public void run(Arena arena, LivingEntity boss)
            {
                for (Player p : getNearbyPlayers(arena, boss, 5))
                    p.teleport(boss);
            }
        },
        FETCHDISTANT("Fetch distant players")
        {
            public void run(Arena arena, LivingEntity boss)
            {
                for (Player p : getDistantPlayers(arena, boss, 8))
                    p.teleport(boss);
            }
        };
        
        private String name;
        
        private BossAbility(String name)
        {
            this.name = name;
        }
        
        /**
         * The run-method that all boss abilities must define.
         * The method is called in the ability cycle for the given boss.
         * @param arena The Arena the boss is in
         * @param boss The boss entity
         */
        public abstract void run(Arena arena, LivingEntity boss);
        
        /**
         * Get the target player of the LivingEntity if possible.
         * @param entity The entity whose target to get
         * @return The target player, or null
         */
        protected LivingEntity getTarget(LivingEntity entity)
        {
            if (entity instanceof Creature)
            {
                LivingEntity target = null;
                try
                {
                    target = ((Creature) entity).getTarget();
                }
                catch (Exception e) {}
                
                if (target instanceof Player)
                    return target;
            }
            return null;
        }
        
        /**
         * Get a list of nearby players
         * @param arena The arena
         * @param boss The boss
         * @param x The 'radius' in which to grab players
         * @return A list of nearby players
         */
        protected List<Player> getNearbyPlayers(Arena arena, Entity boss, int x)
        {
            List<Player> result = new LinkedList<Player>();
            for (Entity e : boss.getNearbyEntities(x, x, x))
                if (arena.getLivingPlayers().contains(e))
                    result.add((Player) e);
            return result;
        }
        
        /**
         * Get a list of distant players
         * @param arena The arena
         * @param boss The boss
         * @param x The 'radius' in which to exclude players 
         * @return A list of distant players
         */
        protected List<Player> getDistantPlayers(Arena arena, Entity boss, int x)
        {
            List<Player> result = new LinkedList<Player>();
            for (Player p : arena.getLivingPlayers())
                if (p.getLocation().distanceSquared(boss.getLocation()) > x*x)
                    result.add(p);
            return result;
        }
        
        public static BossAbility fromString(String string)
        {
            return WaveUtils.getEnumFromString(BossAbility.class, string);
        }
        
        public String toString()
        {
            return name;
        }
    }
    
    public enum BossHealth
    {
        LOW(5), MEDIUM(9), HIGH(14), PSYCHO(25);
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
        LOW(10), MEDIUM(20), HIGH(30), PSYCHO(60);
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
     * Get the branch type.
     * @return The WaveBranch of this Wave.
     */
    public WaveBranch getBranch();
    
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