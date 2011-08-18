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
    FIREAURA("Fire Aura")
    {
        public void run(Arena arena, LivingEntity boss)
        {
            for (Player p : getNearbyPlayers(arena, boss, 5))
                    p.setFireTicks(20);
        }
    },
    LIGHTNINGAURA("Lightning Aura")
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
    DISORIENTTARGET("Disorient Target")
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
    ROOTTARGET("Root Target")
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
    LIVINGBOMB("Living Bomb")
    {
        public void run(final Arena arena, LivingEntity boss)
        {
            final LivingEntity target = getTarget(boss);
            if (target == null) return;
            
            // Set the target on fire
            target.setFireTicks(60);
            
            // Create an explosion after 3 seconds
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(arena.getPlugin(),
                new Runnable()
                {
                    public void run()
                    {
                        if (!arena.getLivingPlayers().contains(target))
                            return;
                        
                        arena.getWorld().createExplosion(target.getLocation(), 2F);
                        for(Player p : getNearbyPlayers(arena, target, 3))
                            p.setFireTicks(40);
                    }
                }, 61);
        }
    },
    CHAINLIGHTNING("Chain Lightning")
    {
        public void run(Arena arena, LivingEntity boss)
        {
            final LivingEntity target = getTarget(boss);
            if (target == null) return;
            
            strikeLightning(arena, (Player) target, new LinkedList<Player>());
        }
        
        private void strikeLightning(final Arena arena, final Player p, final List<Player> done)
        {
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(arena.getPlugin(),
                new Runnable()
                {
                    public void run()
                    {
                        if (!arena.getLivingPlayers().contains(p))
                            return;
                        
                        // Smite the target
                        arena.getWorld().strikeLightning(p.getLocation());
                        done.add(p);
                        
                        // Grab all nearby players
                        List<Player> nearby = getNearbyPlayers(arena, p, 4);
                        
                        // Remove all that are "done", and return if empty
                        nearby.removeAll(done);
                        if (nearby.isEmpty()) return;
                        
                        // Otherwise, smite the next target!
                        strikeLightning(arena, nearby.get(0), done);
                    }
                }, 8);
        }
    },
    WARPTOPLAYER("Warp")
    {
        public void run(Arena arena, LivingEntity boss)
        {
            List<Player> list = arena.getLivingPlayers();
            boss.teleport(list.get((new Random()).nextInt(list.size())));
        }
    },
    THROWTARGET("Throw Target")
    {
        public void run(Arena arena, LivingEntity boss)
        {
            LivingEntity target = getTarget(boss);
            if (target == null) return;
            
            Location bLoc       = boss.getLocation();
            Location loc        = target.getLocation();
            Vector v            = new Vector(loc.getX() - bLoc.getX(), 0, loc.getZ() - bLoc.getZ());
            target.setVelocity(v.normalize().setY(0.8));
        }
    },
    THROWNEARBY("Throw Nearby Players")
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
    THROWDISTANT("Throw Distant Players")
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
    FETCHTARGET("Fetch Target")
    {
        public void run(Arena arena, LivingEntity boss)
        {
            LivingEntity target = getTarget(boss);
            if (target != null) target.teleport(boss);
        }
    },
    FETCHNEARBY("Fetch Nearby Players")
    {
        public void run(Arena arena, LivingEntity boss)
        {
            for (Player p : getNearbyPlayers(arena, boss, 5))
                p.teleport(boss);
        }
    },
    FETCHDISTANT("Fetch Distant Players")
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
