package com.garbagemule.MobArena.waves;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Wolf;

import com.garbagemule.MobArena.MobArena;
import com.garbagemule.MobArena.framework.Arena;

public enum MACreature
{
    // Default creatures
    ZOMBIE(EntityType.ZOMBIE),            ZOMBIES(EntityType.ZOMBIE), 
    SKELETON(EntityType.SKELETON),        SKELETONS(EntityType.SKELETON),
    SPIDER(EntityType.SPIDER),            SPIDERS(EntityType.SPIDER),
    CREEPER(EntityType.CREEPER),          CREEPERS(EntityType.CREEPER),
    WOLF(EntityType.WOLF),                WOLVES(EntityType.WOLF),
    
    // Special creatures
    ZOMBIEPIGMAN(EntityType.PIG_ZOMBIE),  ZOMBIEPIGMEN(EntityType.PIG_ZOMBIE),
    POWEREDCREEPER(EntityType.CREEPER),   POWEREDCREEPERS(EntityType.CREEPER),
    ANGRYWOLF(EntityType.WOLF),           ANGRYWOLVES(EntityType.WOLF),
    GIANT(EntityType.GIANT),              GIANTS(EntityType.GIANT),
    GHAST(EntityType.GHAST),              GHASTS(EntityType.GHAST),
    ENDERMAN(EntityType.ENDERMAN),        ENDERMEN(EntityType.ENDERMAN),
    CAVESPIDER(EntityType.CAVE_SPIDER),   CAVESPIDERS(EntityType.CAVE_SPIDER),
    SILVERFISH(EntityType.SILVERFISH),
    
    // 1.0 creatures
    BLAZE(EntityType.BLAZE),              BLAZES(EntityType.BLAZE),
    ENDERDRAGON(EntityType.ENDER_DRAGON), ENDERDRAGONS(EntityType.ENDER_DRAGON),
    SNOWMAN(EntityType.SNOWMAN),          SNOWMEN(EntityType.SNOWMAN),
    MUSHROOMCOW(EntityType.MUSHROOM_COW), MUSHROOMCOWS(EntityType.MUSHROOM_COW),
    VILLAGER(EntityType.VILLAGER),        VILLAGERS(EntityType.VILLAGER),        
    
    // Passive creatures
    CHICKEN(EntityType.CHICKEN),          CHICKENS(EntityType.CHICKEN),
    COW(EntityType.COW),                  COWS(EntityType.COW),
    PIG(EntityType.PIG),                  PIGS(EntityType.PIG),
    SHEEP(EntityType.SHEEP),
    SQUID(EntityType.SQUID),              SQUIDS(EntityType.SQUID),
    
    // Extended creatures
    EXPLODINGSHEEP(EntityType.SHEEP),
    
    // Slimes
    SLIME(EntityType.SLIME),              SLIMES(EntityType.SLIME),
    SLIMETINY(EntityType.SLIME),          SLIMESTINY(EntityType.SLIME),
    SLIMESMALL(EntityType.SLIME),         SLIMESSMALL(EntityType.SLIME),
    SLIMEBIG(EntityType.SLIME),           SLIMESBIG(EntityType.SLIME),
    SLIMEHUGE(EntityType.SLIME),          SLIMESHUGE(EntityType.SLIME),
    
    // Magma cubes
    MAGMACUBE(EntityType.MAGMA_CUBE),     MAGMACUBES(EntityType.MAGMA_CUBE),
    MAGMACUBETINY(EntityType.MAGMA_CUBE), MAGMACUBESTINY(EntityType.MAGMA_CUBE),
    MAGMACUBESMALL(EntityType.MAGMA_CUBE),MAGMACUBESSMALL(EntityType.MAGMA_CUBE),
    MAGMACUBEBIG(EntityType.MAGMA_CUBE),  MAGMACUBESBIG(EntityType.MAGMA_CUBE),
    MAGMACUBEHUGE(EntityType.MAGMA_CUBE), MAGMACUBESHUGE(EntityType.MAGMA_CUBE);
    
    private EntityType type;
    
    private MACreature(EntityType type) {
        this.type = type;
    }
    
    public EntityType getType() {
        return type;
    }
    
    public static MACreature fromString(String string) {
        return WaveUtils.getEnumFromString(MACreature.class, string.replaceAll("[-_\\.]", ""));
    }
    
    public LivingEntity spawn(Arena arena, World world, Location loc) {
        LivingEntity e = world.spawnCreature(loc, type);
        
        switch (this)
        {
            case EXPLODINGSHEEP:
                arena.getMonsterManager().addExplodingSheep(e);
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
        
        if (e instanceof Creature) {
            Creature c = (Creature) e;
            c.setTarget(WaveUtils.getClosestPlayer(arena, e));
        }
        
        return e;
    }
}
