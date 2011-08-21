package com.garbagemule.MobArena.waves;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Wolf;

import com.garbagemule.MobArena.Arena;
import com.garbagemule.MobArena.MobArena;
import com.garbagemule.MobArena.util.WaveUtils;

public enum MACreature
{
    // Default creatures
    ZOMBIE(CreatureType.ZOMBIE),            ZOMBIES(CreatureType.ZOMBIE), 
    SKELETON(CreatureType.SKELETON),        SKELETONS(CreatureType.SKELETON),
    SPIDER(CreatureType.SPIDER),            SPIDERS(CreatureType.SPIDER),
    CREEPER(CreatureType.CREEPER),          CREEPERS(CreatureType.CREEPER),
    WOLF(CreatureType.WOLF),                WOLVES(CreatureType.WOLF),
    
    // Special creatures
    ZOMBIE_PIGMAN(CreatureType.PIG_ZOMBIE), ZOMBIE_PIGMEN(CreatureType.PIG_ZOMBIE),
    POWERED_CREEPER(CreatureType.CREEPER),  POWERED_CREEPERS(CreatureType.CREEPER),
    ANGRY_WOLF(CreatureType.WOLF),          ANGRY_WOLVES(CreatureType.WOLF),
    HUMAN(CreatureType.MONSTER),            HUMANS(CreatureType.MONSTER),
    GIANT(CreatureType.GIANT),              GIANTS(CreatureType.GIANT),
    GHAST(CreatureType.GHAST),              GHASTS(CreatureType.GHAST),
    
    // Passive creatures
    CHICKEN(CreatureType.CHICKEN),          CHICKENS(CreatureType.CHICKEN),
    COW(CreatureType.COW),                  COWS(CreatureType.COW),
    PIG(CreatureType.PIG),                  PIGS(CreatureType.PIG),
    SHEEP(CreatureType.SHEEP),
    SQUID(CreatureType.SQUID),              SQUIDS(CreatureType.SQUID),
    
    // Extended creatures
    EXPLODING_SHEEP(CreatureType.SHEEP),
    
    // Slimes
    SLIME(CreatureType.SLIME),              SLIMES(CreatureType.SLIME),
    SLIME_TINY(CreatureType.SLIME),         SLIMES_TINY(CreatureType.SLIME),
    SLIME_SMALL(CreatureType.SLIME),        SLIMES_SMALL(CreatureType.SLIME),
    SLIME_BIG(CreatureType.SLIME),          SLIMES_BIG(CreatureType.SLIME),
    SLIME_HUGE(CreatureType.SLIME),         SLIMES_HUGE(CreatureType.SLIME);
    
    private CreatureType type;
    
    private MACreature(CreatureType type)
    {
        this.type = type;
    }
    
    public CreatureType getType()
    {
        return type;
    }
    
    public static MACreature fromString(String string)
    {
        return WaveUtils.getEnumFromString(MACreature.class, string);
    }
    
    public LivingEntity spawn(Arena arena, World world, Location loc)
    {
        LivingEntity e = world.spawnCreature(loc, type);
        
        switch (this)
        {
            case SHEEP:
            case EXPLODING_SHEEP:
                arena.addExplodingSheep(e);
                break;
            case POWERED_CREEPERS:
                ((Creeper) e).setPowered(true);
                break;
            case ANGRY_WOLVES:
                ((Wolf) e).setAngry(true);
                break;
            case SLIME:
            case SLIMES:
                ((Slime) e).setSize( (1 + MobArena.random.nextInt(3)) );
                break;
            case SLIME_TINY:
            case SLIMES_TINY:
                ((Slime) e).setSize(1);
                break;
            case SLIME_SMALL:
            case SLIMES_SMALL:
                ((Slime) e).setSize(2);
                break;
            case SLIME_BIG:
            case SLIMES_BIG:
                ((Slime) e).setSize(3);
                break;
            case SLIME_HUGE:
            case SLIMES_HUGE:
                ((Slime) e).setSize(4);
                break;
            default:
                break;
        }
        
        return e;
    }
}
