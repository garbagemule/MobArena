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
    ZOMBIEPIGMAN(CreatureType.PIG_ZOMBIE),  ZOMBIEPIGMEN(CreatureType.PIG_ZOMBIE),
    POWEREDCREEPER(CreatureType.CREEPER),   POWEREDCREEPERS(CreatureType.CREEPER),
    ANGRYWOLF(CreatureType.WOLF),           ANGRYWOLVES(CreatureType.WOLF),
    //HUMAN(CreatureType.VILLAGER),           HUMANS(CreatureType.VILLAGER),
    GIANT(CreatureType.GIANT),              GIANTS(CreatureType.GIANT),
    GHAST(CreatureType.GHAST),              GHASTS(CreatureType.GHAST),
    ENDERMAN(CreatureType.ENDERMAN),        ENDERMEN(CreatureType.ENDERMAN),
    CAVESPIDER(CreatureType.CAVE_SPIDER),   CAVESPIDERS(CreatureType.CAVE_SPIDER),
    SILVERFISH(CreatureType.SILVERFISH),
    
    // 1.0 creatures
    BLAZE(CreatureType.BLAZE),              BLAZES(CreatureType.BLAZE),
    ENDERDRAGON(CreatureType.ENDER_DRAGON), ENDERDRAGONS(CreatureType.ENDER_DRAGON),
    SNOWMAN(CreatureType.SNOWMAN),          SNOWMEN(CreatureType.SNOWMAN),
    MUSHROOMCOW(CreatureType.MUSHROOM_COW), MUSHROOMCOWS(CreatureType.MUSHROOM_COW),
    VILLAGER(CreatureType.VILLAGER),        VILLAGERS(CreatureType.VILLAGER),        
    
    // Passive creatures
    CHICKEN(CreatureType.CHICKEN),          CHICKENS(CreatureType.CHICKEN),
    COW(CreatureType.COW),                  COWS(CreatureType.COW),
    PIG(CreatureType.PIG),                  PIGS(CreatureType.PIG),
    SHEEP(CreatureType.SHEEP),
    SQUID(CreatureType.SQUID),              SQUIDS(CreatureType.SQUID),
    
    // Extended creatures
    EXPLODINGSHEEP(CreatureType.SHEEP),
    
    // Slimes
    SLIME(CreatureType.SLIME),              SLIMES(CreatureType.SLIME),
    SLIMETINY(CreatureType.SLIME),          SLIMESTINY(CreatureType.SLIME),
    SLIMESMALL(CreatureType.SLIME),         SLIMESSMALL(CreatureType.SLIME),
    SLIMEBIG(CreatureType.SLIME),           SLIMESBIG(CreatureType.SLIME),
    SLIMEHUGE(CreatureType.SLIME),          SLIMESHUGE(CreatureType.SLIME),
    
    // Magma cubes
    MAGMACUBE(CreatureType.MAGMA_CUBE),     MAGMACUBES(CreatureType.MAGMA_CUBE),
    MAGMACUBETINY(CreatureType.MAGMA_CUBE), MAGMACUBESTINY(CreatureType.MAGMA_CUBE),
    MAGMACUBESMALL(CreatureType.MAGMA_CUBE),MAGMACUBESSMALL(CreatureType.MAGMA_CUBE),
    MAGMACUBEBIG(CreatureType.MAGMA_CUBE),  MAGMACUBESBIG(CreatureType.MAGMA_CUBE),
    MAGMACUBEHUGE(CreatureType.MAGMA_CUBE), MAGMACUBESHUGE(CreatureType.MAGMA_CUBE);
    
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
        return WaveUtils.getEnumFromString(MACreature.class, string.replaceAll("[-_\\.]", ""));
    }
    
    public LivingEntity spawn(Arena arena, World world, Location loc)
    {
        LivingEntity e = world.spawnCreature(loc, type);
        
        switch (this)
        {
            case EXPLODINGSHEEP:
                arena.addExplodingSheep(e);
                break;
            case POWEREDCREEPERS:
                ((Creeper) e).setPowered(true);
                break;
            case ANGRYWOLVES:
                ((Wolf) e).setAngry(true);
                break;
            case SLIME:
            case SLIMES:
            case MAGMACUBE:
            case MAGMACUBES:
                ((Slime) e).setSize( (1 + MobArena.random.nextInt(3)) );
                break;
            case SLIMETINY:
            case SLIMESTINY:
            case MAGMACUBETINY:
            case MAGMACUBESTINY:
                ((Slime) e).setSize(1);
                break;
            case SLIMESMALL:
            case SLIMESSMALL:
            case MAGMACUBESMALL:
            case MAGMACUBESSMALL:
                ((Slime) e).setSize(2);
                break;
            case SLIMEBIG:
            case SLIMESBIG:
            case MAGMACUBEBIG:
            case MAGMACUBESBIG:
                ((Slime) e).setSize(3);
                break;
            case SLIMEHUGE:
            case SLIMESHUGE:
            case MAGMACUBEHUGE:
            case MAGMACUBESHUGE:
                ((Slime) e).setSize(4);
                break;
            default:
                break;
        }
        
        return e;
    }
}
