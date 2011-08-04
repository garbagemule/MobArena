package com.garbagemule.MobArena.waves;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Wolf;

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
    SLIME(CreatureType.SLIME),              SLIMES(CreatureType.SLIME),
    GIANT(CreatureType.GIANT),              GIANTS(CreatureType.GIANT),
    GHAST(CreatureType.GHAST),              GHASTS(CreatureType.GHAST);
    
    // Misc
    // EXPLODING_SHEEP(CreatureType.SHEEP), // Explode (power: 1) when close enough to players
    // PLAGUED_PIGS(CreatureType.PIG),      // Damage "aura" (getNearbyEntities)
    // MAD_COWS(CreatureType.COW);          // Ram/throw players
    // 
    
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
    
    public LivingEntity spawn(World world, Location loc)
    {
        LivingEntity e = world.spawnCreature(loc, type);
        
        switch (this)
        {
            case POWERED_CREEPERS:
                ((Creeper) e).setPowered(true);
                break;
            case ANGRY_WOLVES:
                ((Wolf) e).setAngry(true);
                break;
            case SLIMES:
                ((Slime) e).setSize(2);
                break;                
            default:
                break;
        }
        
        return e;
    }
    
    public static LivingEntity spawn(MACreature creature, World world, Location loc)
    {
        LivingEntity e = world.spawnCreature(loc, creature.type);
        
        switch (creature)
        {
            case POWERED_CREEPERS:
                ((Creeper) e).setPowered(true);
                break;
            case ANGRY_WOLVES:
                ((Wolf) e).setAngry(true);
                break;
            case SLIMES:
                ((Slime) e).setSize(2);
                break;                
            default:
                break;
        }
        
        return e;
    }
}
